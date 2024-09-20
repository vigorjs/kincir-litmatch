package com.smith.helmify.service.impl;

import com.smith.helmify.config.advisers.exception.AuthenticationException;
import com.smith.helmify.config.advisers.exception.NotFoundException;
import com.smith.helmify.model.enums.TransactionStatus;
import com.smith.helmify.model.meta.Transaction;
import com.smith.helmify.model.meta.User;
import com.smith.helmify.repo.TransactionRepository;
import com.smith.helmify.service.AuthenticationService;
import com.smith.helmify.service.MidtransService;
import com.smith.helmify.utils.dto.restClientDto.MidtransRequestDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransResponseDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransSnapRequestDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransSnapResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class MidtransServiceImpl implements MidtransService {

    private final RestClient restClient;
    private final HttpHeaders headers;
    private final TransactionRepository transactionRepository;
    private final AuthenticationService authenticationService;

    @Value("${midtrans.api-url}")
    private String midtransApiUrl;
    @Value("${midtrans.api-snap-url}")
    private String midtransSnapApiUrl;

    public MidtransServiceImpl(RestClient restClient, HttpHeaders headers, TransactionRepository transactionRepository, AuthenticationService authenticationService){
        this.restClient = restClient;
        this.headers = headers;
        this.transactionRepository = transactionRepository;
        this.authenticationService = authenticationService;
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
                    .uri(midtransApiUrl + "v2/" + order_id + "/status")
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
    @CacheEvict(value = "transactions", allEntries = true)
    public MidtransResponseDTO changeStatus(String order_id, String status) {
        try {
            Transaction transaction = transactionRepository.findByOrderId(order_id).orElseThrow(() -> new NotFoundException("Transaction Not Found"));
            User user = authenticationService.getUserAuthenticated();
            if (transaction.getUser().getEmail().equals(user.getEmail())) {
                MidtransResponseDTO midtransResponseDto = restClient.post()
                        .uri(midtransApiUrl + "v2/" + order_id + "/" + status)
                        .headers(httpHeaders -> httpHeaders.addAll(headers))
                        .retrieve()
                        .body(MidtransResponseDTO.class);

                assert midtransResponseDto != null;
                transaction.setStatus(TransactionStatus.valueOf(midtransResponseDto.getTransaction_status()));
                transactionRepository.update(transaction);

                return midtransResponseDto;
            }
            throw new BadCredentialsException("Access Denied, Wrong user!");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}