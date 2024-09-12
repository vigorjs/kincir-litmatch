package com.smith.helmify.service.impl;

import com.smith.helmify.config.advisers.exception.NotFoundException;
import com.smith.helmify.model.enums.TransactionStatus;
import com.smith.helmify.model.meta.Transaction;
import com.smith.helmify.repo.TransactionRepository;
import com.smith.helmify.service.MidtransService;
import com.smith.helmify.utils.dto.restClientDto.MidtransRequestDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransResponseDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransSnapRequestDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransSnapResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MidtransServiceImpl implements MidtransService {

    private final RestClient restClient;
    private final HttpHeaders headers;
    private final TransactionRepository transactionRepository;

    @Value("${midtrans.api-url}")
    private String midtransApiUrl;
    @Value("${midtrans.api-snap-url}")
    private String midtransSnapApiUrl;

    public MidtransServiceImpl(RestClient restClient, HttpHeaders headers, TransactionRepository transactionRepository){
        this.restClient = restClient;
        this.headers = headers;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public MidtransResponseDTO chargePayment(MidtransRequestDTO req) {
            var midtransResponseDto = restClient.post()
                    .uri(midtransApiUrl+"v2/charge")
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .body(req)
                    .retrieve()
                    .body(MidtransResponseDTO.class);

            assert midtransResponseDto != null;
            if (midtransResponseDto.getStatus_code().equals("200") || midtransResponseDto.getStatus_code().equals("201")){
                return midtransResponseDto;
            }
            throw new IllegalArgumentException(midtransResponseDto.getStatus_message());
    }

    @Override
    public MidtransSnapResponseDTO chargePaymentSnap(MidtransSnapRequestDTO req){
        String url = midtransSnapApiUrl.endsWith("/") ? midtransSnapApiUrl : midtransSnapApiUrl + "/";
        var midtransSnapResponseDto = restClient.post()
                .uri(url + "snap/v1/transactions")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(req)
                .retrieve()
                .body(MidtransSnapResponseDTO.class);

        assert midtransSnapResponseDto != null;
        if (!midtransSnapResponseDto.getToken().isEmpty() || !midtransSnapResponseDto.getToken().isBlank()){
            return midtransSnapResponseDto;
        }
        throw new IllegalArgumentException("Error midtrans SNAP");
    }

    @Override
    public MidtransResponseDTO getStatus(String order_id) {
        try {
            var midtransResponseDto = restClient.get()
                    .uri(midtransApiUrl + order_id + "/status")
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .retrieve()
                    .body(MidtransResponseDTO.class);

            assert midtransResponseDto != null;
            return midtransResponseDto;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public MidtransResponseDTO changeStatus(String order_id, String status) {
        try {
            var midtransResponseDto = restClient.post()
                    .uri(midtransApiUrl + order_id + "/" + status)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .retrieve()
                    .body(MidtransResponseDTO.class);

            assert midtransResponseDto != null;
            Transaction transaction = transactionRepository.findByOrderId(order_id).orElseThrow(() -> new NotFoundException("Transaction Not Found"));
            transaction.setStatus(TransactionStatus.valueOf("cancel"));
            transactionRepository.save(transaction);

            return midtransResponseDto;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
