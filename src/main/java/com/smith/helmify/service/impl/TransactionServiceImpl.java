package com.smith.helmify.service.impl;

import com.smith.helmify.config.advisers.exception.InternalException;
import com.smith.helmify.config.advisers.exception.NotFoundException;
import com.smith.helmify.model.enums.MachineStatus;
import com.smith.helmify.model.enums.ServiceCategories;
import com.smith.helmify.model.enums.TransactionStatus;
import com.smith.helmify.model.meta.*;
import com.smith.helmify.repo.ServiceRepository;
import com.smith.helmify.repo.ServiceStockRepository;
import com.smith.helmify.repo.TransactionDetailRepository;
import com.smith.helmify.repo.TransactionRepository;
import com.smith.helmify.service.*;
import com.smith.helmify.utils.dto.ServiceDTO;
import com.smith.helmify.utils.dto.TransactionResponseDTO;
import com.smith.helmify.utils.dto.restClientDto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionDetailRepository transactionDetailRepository;
    private final MidtransService midtransService;
    private final AuthenticationService authenticationService;
    private final ExecutorService executorService;
    private final ServiceRepository serviceRepository;
    private final ServiceStockRepository serviceStockRepository;
    private final MachineService machineService;
    private final IotService iotService;

    @Override
    @Transactional
    @CacheEvict(value = "transactions", allEntries = true)
    public MidtransResponseDTO create(MidtransRequestDTO req) {
        User user = authenticationService.getUserAuthenticated();

        // Validasi dan setel informasi yang diperlukan untuk request checkout service
        midtransChargeRequestValidation(req);

        // Hitung total harga dari semua produk request
        Long total = 0L;
        for (MidtransRequestDTO.ServiceRequest serviceRequest : req.getItem_detail()) {
            ServiceDTO serviceDTO = serviceRepository.findById(serviceRequest.getServiceId()).orElseThrow(() -> new NotFoundException("Service Not Found"));

            Service service = Service.builder()
                    .id(serviceDTO.getId())
                    .service_name(serviceDTO.getService_name())
                    .price(serviceDTO.getPrice())
                    .service_description(serviceDTO.getService_description())
                    .user(user)
                    .build();

            ServiceStock currentStock = serviceStockRepository.findByServiceId(service.getId())
                    .orElseThrow(() -> new NotFoundException("Stock not found for service: " + service.getService_name()));

            if (currentStock.getQuantity() < 1) {
                throw new RuntimeException("Insufficient stock for service: " + service.getService_name());
            }
            serviceRequest.setName(service.getService_name());
            serviceRequest.setPrice(service.getPrice());
            serviceRequest.setQuantity(1); //set agar quantity 1 ngikutin flow bisnisnya

            // Hitung total untuk setiap item
            total += service.getPrice();
        }

        // Tambahkan service untuk cuci dan ngeringinnya
        MidtransRequestDTO.ServiceRequest newServiceRequest = new MidtransRequestDTO.ServiceRequest();
        newServiceRequest.setServiceId(null); // ID service ini tidak ada di database, jadi diset null
        newServiceRequest.setName("Quick Clean & Dry up");
        newServiceRequest.setPrice(25000L);
        newServiceRequest.setQuantity(1);
        req.getItem_detail().add(newServiceRequest);
        // Tambahkan harga service baru ke total
        total += newServiceRequest.getPrice();

        // Simpan transaksi ke database untuk mendapatkan ID
        Transaction transactionBuild = Transaction.builder()
                .user(user)
                .order_id(null)
                .gross_amount(total)
                .status(TransactionStatus.valueOf("pending"))
                .build();
        Transaction transaction = transactionRepository.save(transactionBuild);

        // Charge payment via Midtrans
        MidtransRequestDTO.TransactionDetails transactionDetails = new MidtransRequestDTO.TransactionDetails(
                "TESTDUAA-"+transaction.getId(),
                total
        );

        req.setTransaction_details(transactionDetails);

        MidtransResponseDTO midtransResponse = midtransService.chargePayment(req); //ini pas ngecharge
        transaction.setOrder_id(midtransResponse.getOrder_id());
        transactionRepository.update(transaction);

        //get machine
        Machine machine = machineService.getById(req.getMachine_id());


        // create transaction_detail
        for (MidtransRequestDTO.ServiceRequest serviceRequest : req.getItem_detail()) {
            ServiceDTO serviceDTO = serviceRepository.findById(serviceRequest.getServiceId()).orElseThrow(() -> new NotFoundException("Service Not Found"));

            Service service = Service.builder()
                    .id(serviceDTO.getId())
                    .service_name(serviceDTO.getService_name())
                    .price(serviceDTO.getPrice())
                    .service_description(serviceDTO.getService_description())
                    .user(user)
                    .build();

            TransactionDetail transactionDetail = new TransactionDetail();
            transactionDetail.setMachine(machine);
            transactionDetail.setService(service);
            transactionDetail.setTransaction(transaction);
            transactionDetail.setQuantity(serviceRequest.getQuantity());
            transactionDetail.setAmount(service.getPrice());

            transactionDetailRepository.save(transactionDetail);
        }

        // jalankan thread untuk mengecek status pembayaran dengan looping hit ke midtrans sebanyak expired time
//        executorService.submit( () -> updateTransactionStatus(transaction) );
        return midtransResponse;
    }

    @Override
    @Transactional
    @CacheEvict(value = "transactions", allEntries = true)
    public MidtransSnapResponseDTO createSnap(MidtransSnapRequestDTO req) {
        User user = authenticationService.getUserAuthenticated();
        Machine machine = machineService.getById(req.getMachine_id());

        // validasi machine must be ready
        if (!machine.getStatus().equals(MachineStatus.READY.name())){
            throw new InternalException("Machine still Working / wasn't Ready please wait...");
        }

        // Validasi dan setel informasi yang diperlukan untuk request checkout service
        midtransSnapChargeRequestValidation(req);

        // Hitung total harga dari semua produk request
        Long total = 0L;
        if (req.getItem_details() == null || req.getItem_details().isEmpty()) {
            // Tambahkan service untuk ngeringin doang
            MidtransSnapRequestDTO.ServiceRequest newServiceRequest = new MidtransSnapRequestDTO.ServiceRequest();
            newServiceRequest.setServiceId(null); // ID service ini tidak ada di database, jadi diset null
            newServiceRequest.setName("Only Dry");
            newServiceRequest.setPrice(15000L);
            newServiceRequest.setQuantity(1);
            req.getItem_details().add(newServiceRequest);
            // Tambahkan harga service baru ke total
            total += newServiceRequest.getPrice();
        } else {
            for (MidtransSnapRequestDTO.ServiceRequest serviceRequest : req.getItem_details()) {
                ServiceDTO serviceDTO = serviceRepository.findById(serviceRequest.getServiceId())
                        .orElseThrow(() -> new NotFoundException("Service Not Found"));

                Service service = Service.builder()
                        .id(serviceDTO.getId())
                        .service_name(serviceDTO.getService_name())
                        .price(serviceDTO.getPrice())
                        .service_description(serviceDTO.getService_description())
                        .user(user)
                        .build();

                ServiceStock currentStock = serviceStockRepository.findByServiceId(service.getId())
                        .orElseThrow(() -> new NotFoundException("Stock not found for service: " + service.getService_name()));

                if (currentStock.getQuantity() < 1) {
                    throw new RuntimeException("Insufficient stock for service: " + service.getService_name());
                }
                serviceRequest.setName(service.getService_name());
                serviceRequest.setPrice(service.getPrice());
                serviceRequest.setQuantity(1); //set agar quantity 1 ngikutin flow bisnisnya

                // Hitung total untuk setiap item
                total += service.getPrice();
            }

            // Tambahkan service untuk cuci dan ngeringinnya
            MidtransSnapRequestDTO.ServiceRequest newServiceRequest = new MidtransSnapRequestDTO.ServiceRequest();
            newServiceRequest.setServiceId(null); // ID service ini tidak ada di database, jadi diset null
            newServiceRequest.setName("Quick Clean & Dry up");
            newServiceRequest.setPrice(25000L);
            newServiceRequest.setQuantity(1);
            req.getItem_details().add(newServiceRequest);
            // Tambahkan harga service baru ke total
            total += newServiceRequest.getPrice();
        }

        // Simpan transaksi ke database untuk mendapatkan ID
        Transaction transactionBuild = Transaction.builder()
                .user(user)
                .order_id(null)
                .gross_amount(total)
                .status(TransactionStatus.valueOf("pending"))
                .build();
        Transaction transaction = transactionRepository.save(transactionBuild);

        // Charge payment via Midtrans Snap
        MidtransSnapRequestDTO.TransactionDetails transactionDetails = new MidtransSnapRequestDTO.TransactionDetails(
                "HELMTRX-" + transaction.getId(),
                total
        );
        req.setTransaction_details(transactionDetails);

        MidtransSnapResponseDTO midtransSnapResponse = midtransService.chargePaymentSnap(req); //charge ke midtrans

//        transaction.setOrder_id(midtransSnapResponse.getToken());
        transaction.setOrder_id(transactionDetails.getOrder_id());
        transactionRepository.update(transaction);

//        if (midtransSnapResponse.getTransaction_detail() == null) {
//            midtransSnapResponse.setTransaction_detail(new ArrayList<>());
//        }
        midtransSnapResponse.setTransaction_detail(Optional.ofNullable(midtransSnapResponse.getTransaction_detail())
                .orElse(new ArrayList<>()));

        // create transaction_detail
        TransactionDetail transactionDetail = null;
        for (MidtransSnapRequestDTO.ServiceRequest serviceRequest : req.getItem_details()) {
            if (serviceRequest.getServiceId() == null || serviceRequest.getName().toLowerCase().contains("clean")) {
                continue;
            }
            ServiceDTO serviceDTO = serviceRepository.findById(serviceRequest.getServiceId())
                    .orElseThrow(() -> new NotFoundException("Service Not Found - nudros was hacked this program"));

            Service service = Service.builder()
                    .id(serviceDTO.getId())
                    .service_name(serviceDTO.getService_name())
                    .price(serviceDTO.getPrice())
                    .service_description(serviceDTO.getService_description())
                    .user(user)
                    .build();

            transactionDetail = new TransactionDetail();
            transactionDetail.setMachine(machine);
            transactionDetail.setService(service);
            transactionDetail.setTransaction(transaction);
            transactionDetail.setQuantity(serviceRequest.getQuantity());
            transactionDetail.setAmount(service.getPrice());

            TransactionDetail savedTransactionDetail = transactionDetailRepository.save(transactionDetail);
            midtransSnapResponse.getTransaction_detail().add(savedTransactionDetail);
        }

        //set transaction ke response
        midtransSnapResponse.setTransaction(transaction);

        // jalankan thread untuk mengecek status pembayaran dengan looping hit ke midtrans sebanyak expired time
        // executorService.submit(() -> updateTransactionStatus(transaction.getId(), transaction, transactionDetails.getOrder_id()));
        return midtransSnapResponse;
    }

    @Override
    @Cacheable(value = "transactions", key = "#userId != null ? #userId : 'all'")
    public List<TransactionResponseDTO> getAll(Integer userId) {
        List<Transaction> transactions = transactionRepository.findAll(userId);
        List<TransactionResponseDTO> result =  new ArrayList<>();
        for(var trans : transactions){
            List<TransactionDetail> tempTransDetail = transactionDetailRepository.findByTransactionId(trans.getId());
            TransactionResponseDTO tempDTO = TransactionResponseDTO.builder()
                    .transaction(trans)
                    .transactionDetails(tempTransDetail)
            .build();
            result.add(tempDTO);
        }

        return result;
    }

    @Override
    @Cacheable(value = "transactions", key = "#id")
    public TransactionResponseDTO getById(Integer id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new NotFoundException("Transaction is not found"));
        List<TransactionDetail> tempTransDetail = transactionDetailRepository.findByTransactionId(transaction.getId());
        TransactionResponseDTO tempDTO = TransactionResponseDTO.builder()
                .transaction(transaction)
                .transactionDetails(tempTransDetail)
                .build();

     return  tempDTO;
    }

    private void midtransSnapChargeRequestValidation(MidtransSnapRequestDTO req) {
        if (req.getCredit_card() == null) {
            MidtransSnapRequestDTO.CreditCard creditCard = new MidtransSnapRequestDTO.CreditCard();
            creditCard.setSecure(true);
            req.setCredit_card(creditCard);
        }

        if (req.getCustom_expiry() == null) {
            req.setCustom_expiry(new MidtransSnapRequestDTO.CustomExpiry());
            req.getCustom_expiry().setUnit("minute");
            req.getCustom_expiry().setExpiry_duration(60);
            req.getCustom_expiry().setOrder_time(getFormattedOrderTime());
        }
    }


    private void midtransChargeRequestValidation(MidtransRequestDTO req) {
        if (req.getPayment_type() == null) {
            req.setPayment_type("bank_transfer");
        }
        if (req.getCustom_expiry() == null) {
            req.setCustom_expiry(new MidtransRequestDTO.CustomExpiry());
            req.getCustom_expiry().setUnit("minute");
            req.getCustom_expiry().setExpiry_duration(60);
            req.getCustom_expiry().setOrder_time(getFormattedOrderTime());
        }
    }

    private String getFormattedOrderTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z")
                .withZone(ZoneId.of("Asia/Jakarta"));
        return formatter.format(Instant.now());
    }

//    @Override
//    @CacheEvict(value = "transactions", allEntries = true)
//    public void updateTransactionStatus(Transaction req) {
//        boolean flag = false;
//        for (int i = 0; i < 100; i++) {
//            try {
//                MidtransResponseDTO response = midtransService.getStatus(req.getOrder_id());
//                if (response != null && !"200".equals(response.getStatus_code())) {
//                    response = midtransService.getStatus(req.getOrder_id());
//                }
//
//                if (response != null && (TransactionStatus.settlement.name().equals(response.getTransaction_status()) || TransactionStatus.capture.name().equals(response.getTransaction_status()))) {
//                    Transaction transaction = transactionRepository.findById(req.getId()).orElseThrow();
//                    transaction.setStatus(TransactionStatus.settlement);
//                    transactionRepository.update(transaction);
//
//                    flag = true;
//                    break;
//                }
//                Thread.sleep(3000);
//            } catch (Exception e) {
//                log.error("error in updateTransactionStatus() {}", e.getMessage());
//            }
//        }
//        if (!flag) {
//            midtransService.changeStatus(req.getOrder_id(), TransactionStatus.cancel.name());
//        }
//        log.info("Exiting updateTransactionStatus()");
//    }

    @Override
    @CacheEvict(value = "transactions", allEntries = true)
    public void refreshAndUpdateTransactionStatus(Transaction req, String machine_id) {
        try {
            MidtransResponseDTO response = midtransService.getStatus(req.getOrder_id());

            if (response != null) {
                String transactionStatus = response.getTransaction_status();

                if ("200".equals(response.getStatus_code())) {
                    if (!TransactionStatus.settlement.name().equals(transactionStatus)) {
                        midtransService.changeStatus(req.getOrder_id(), transactionStatus);
                    } else {
                        Transaction transaction = transactionRepository.findById(req.getId())
                                .orElseThrow(() -> new NotFoundException("Transaction not found"));
                        transaction.setStatus(TransactionStatus.settlement);
                        transactionRepository.update(transaction);
                    }
                } else {
                    log.warn("Midtrans response failed or status code is not 200 for order_id: {}", req.getOrder_id());
                }
            } else {
                log.warn("Midtrans response is null for order_id: {}", req.getOrder_id());
            }

            TransactionResponseDTO transaction = getById(req.getId());
            String transactionStatusName = transaction.getTransaction().getStatus().name();

            if (TransactionStatus.settlement.name().equals(transactionStatusName) ||
                    TransactionStatus.capture.name().equals(transactionStatusName)) {

                String parfume = "";
                String sabun = "";

                for( TransactionDetail transactionDetail : transaction.getTransactionDetails()){
                    if (transactionDetail.getService().getCategory().equals(ServiceCategories.PARFUM.name())){
                        parfume = transactionDetail.getService().getService_name();
                    }else {
                        sabun = transactionDetail.getService().getService_name();
                    }
                }

                iotService.IotAction(new IotRequestDTO(sabun, parfume, machine_id));
            }
        } catch (Exception e) {
            log.error("Error in refreshAndUpdateTransactionStatus(): {}", e.getMessage(), e);
        }
        log.info("Exiting refreshAndUpdateTransactionStatus()");
    }

}