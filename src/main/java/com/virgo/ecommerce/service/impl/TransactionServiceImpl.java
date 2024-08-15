package com.virgo.ecommerce.service.impl;

import com.virgo.ecommerce.config.advisers.exception.NotFoundException;
import com.virgo.ecommerce.model.enums.TransactionStatus;
import com.virgo.ecommerce.model.meta.*;
import com.virgo.ecommerce.repo.ProductStockRepository;
import com.virgo.ecommerce.repo.TransactionDetailRepository;
import com.virgo.ecommerce.repo.TransactionRepository;
import com.virgo.ecommerce.service.AuthenticationService;
import com.virgo.ecommerce.service.MidtransService;
import com.virgo.ecommerce.service.TransactionService;
import com.virgo.ecommerce.utils.dto.restClientDto.MidtransRequestDTO;
import com.virgo.ecommerce.utils.dto.restClientDto.MidtransResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionDetailRepository transactionDetailRepository;
    private final MidtransService midtransService;
    private final ProductStockRepository productStockRepository;
    private final AuthenticationService authenticationService;
    private final ExecutorService executorService;

    @Override
    @Transactional
    public MidtransResponseDTO create(MidtransRequestDTO req) {
        User user = authenticationService.getUserAuthenticated();

        // Validasi dan setel informasi yang diperlukan untuk request checkout product
        midtransChargeRequestValidation(req);

        // Hitung total harga dari semua produk request
        Long total = 0L;
        for (MidtransRequestDTO.ProductRequest productRequest : req.getItem_detail()) {
            Product product = productRequest.getProduct();
            ProductStock currentStock = productStockRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new NotFoundException("Stock not found for product: " + product.getProduct_name()));

            if (currentStock.getQuantity() < productRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getProduct_name());
            }

            // Hitung total untuk setiap item
            total += product.getPrice() * productRequest.getQuantity();
        }

        // Simpan transaksi ke database untuk mendapatkan ID
        Transaction transactionBuild = Transaction.builder()
                .user_id(user.getId())
                .order_id(null)
                .gross_amount(total)
                .status("pending")
                .build();
        Transaction transaction = transactionRepository.save(transactionBuild);

        MidtransRequestDTO.TransactionDetails transactionDetails = new MidtransRequestDTO.TransactionDetails(
                "TSK-"+transaction.getId(),
                total
        );

        req.setTransaction_details(transactionDetails);
        // Charge payment via Midtrans
        MidtransResponseDTO midtransResponse = midtransService.chargePayment(req);
        transaction.setOrder_id(midtransResponse.getOrder_id());
        transactionRepository.update(transaction);


        // create transaction_detail
        for (MidtransRequestDTO.ProductRequest productRequest : req.getItem_detail()) {
            Product product = productRequest.getProduct();
            ProductStock currentStock = productStockRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new NotFoundException("Stock not found for product: " + product.getProduct_name()));

            TransactionDetail transactionDetail = new TransactionDetail();
            transactionDetail.setProduct_id(product.getId());
            transactionDetail.setTransaction_id(transaction.getId());
            transactionDetail.setQuantity(productRequest.getQuantity());
            transactionDetail.setAmount(product.getPrice() * productRequest.getQuantity());

            transactionDetailRepository.save(transactionDetail);

            currentStock.setQuantity(currentStock.getQuantity() - productRequest.getQuantity());
            productStockRepository.update(currentStock);
        }

        // jalankan thread untuk mengecek status pembayaran dengan looping hit ke midtrans sebanyak expired time
        executorService.submit( () -> updateTransactionStatus(transaction.getId(), transaction) );
        return midtransResponse;
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
                    Transaction.setStatus("settlement");
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
