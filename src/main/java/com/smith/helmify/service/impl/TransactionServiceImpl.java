package com.smith.helmify.service.impl;

import com.smith.helmify.config.advisers.exception.NotFoundException;
import com.smith.helmify.model.enums.TransactionStatus;
import com.smith.helmify.model.meta.*;
import com.smith.helmify.repo.ServiceRepository;
import com.smith.helmify.repo.ServiceStockRepository;
import com.smith.helmify.repo.TransactionDetailRepository;
import com.smith.helmify.repo.TransactionRepository;
import com.smith.helmify.service.AuthenticationService;
import com.smith.helmify.service.MachineService;
import com.smith.helmify.service.MidtransService;
import com.smith.helmify.service.TransactionService;
import com.smith.helmify.utils.dto.ServiceDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransRequestDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransResponseDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransSnapRequestDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransSnapResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    @Override
    @Transactional
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

            // Hitung total untuk setiap item
            total += service.getPrice();
        }

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

        MidtransResponseDTO midtransResponse = midtransService.chargePayment(req);
        transaction.setOrder_id(midtransResponse.getOrder_id());
        transactionRepository.update(transaction);


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

            Machine machine = machineService.getById(serviceRequest.getMachineId());

            TransactionDetail transactionDetail = new TransactionDetail();
            transactionDetail.setMachine(machine);
            transactionDetail.setService(service);
            transactionDetail.setTransaction(transaction);
            transactionDetail.setQuantity(serviceRequest.getQuantity());
            transactionDetail.setAmount(service.getPrice());

            transactionDetailRepository.save(transactionDetail);
        }

        // jalankan thread untuk mengecek status pembayaran dengan looping hit ke midtrans sebanyak expired time
        executorService.submit( () -> updateTransactionStatus(transaction.getId(), transaction) );
        return midtransResponse;
    }

    @Override
    @Transactional
    public MidtransSnapResponseDTO createSnap(MidtransSnapRequestDTO req) {
        User user = authenticationService.getUserAuthenticated();

        // Validasi dan setel informasi yang diperlukan untuk request checkout service
        midtransSnapChargeRequestValidation(req);

        // Hitung total harga dari semua produk request
        Long total = 0L;
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

            // Hitung total untuk setiap item
            total += service.getPrice();
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
                "SNAPDUA-" + transaction.getId(),
                total
        );
        req.setTransaction_details(transactionDetails);

        MidtransSnapResponseDTO midtransSnapResponse = midtransService.chargePaymentSnap(req);
        transaction.setOrder_id(midtransSnapResponse.getToken());
        transactionRepository.update(transaction);

        // create transaction_detail
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

            Machine machine = machineService.getById(serviceRequest.getMachineId());

            TransactionDetail transactionDetail = new TransactionDetail();
            transactionDetail.setMachine(machine);
            transactionDetail.setService(service);
            transactionDetail.setTransaction(transaction);
            transactionDetail.setQuantity(serviceRequest.getQuantity());
            transactionDetail.setAmount(service.getPrice());

            transactionDetailRepository.save(transactionDetail);
        }

        // jalankan thread untuk mengecek status pembayaran dengan looping hit ke midtrans sebanyak expired time
        executorService.submit(() -> updateTransactionStatus(transaction.getId(), transaction));
        return midtransSnapResponse;
    }

    private void midtransSnapChargeRequestValidation(MidtransSnapRequestDTO req) {
        if (req.getCredit_card().getSecure() == null) {
            req.getCredit_card().setSecure(true);
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

    public void updateTransactionStatus(Integer id, Transaction obj) {
        boolean flag = false;
        for (int i = 0; i < 120; i++) {
            try {
                MidtransResponseDTO response = midtransService.getStatus(obj.getOrder_id());

                if (response != null && String.valueOf(TransactionStatus.settlement).equals(response.getTransaction_status())) {
                    Transaction Transaction = transactionRepository.findById(id).orElseThrow();
                    Transaction.setStatus(TransactionStatus.valueOf("settlement"));
                    transactionRepository.save(Transaction);
                    flag = true;
                    break;
                }
                Thread.sleep(3000);
            } catch (Exception e) {
                log.error("error in updateTransactionStatus() {}", e.getMessage());
            }
        }
        if (!flag){
            midtransService.changeStatus(obj.getOrder_id(), String.valueOf(TransactionStatus.cancel));
        }
        log.info("Exiting updateTransactionStatus()");
    }
}
