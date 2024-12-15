package com.example.kincir.service.impl;

import com.example.kincir.service.LitatomService;
import com.example.kincir.utils.dto.response.RoundInfoResponseDTO;
import com.example.kincir.utils.dto.response.RoundResultResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class LitatomServiceImpl implements LitatomService {

    private final RestTemplate restTemplate;
    private final HttpHeaders headers;

    private static final String BASE_URL = "https://www.litatom.com/api/sns/v1/lit/multiplayer_box";
    private static final String COMMON_PARAMS = "loc=US&appid=NxUUhRSQH3qPJmY2Vn%2FedRz4vrc%3D&base_url=https:%2F%2Fwww.litatom.com%2F&model=Galaxy+S7&lang=en&uuid=ee3a0a4d9b961fc7&version=6.94.1&platform=android&sid=session.v2.1418270463747805463";

    @Override
    public RoundInfoResponseDTO getRoundInfo() {
        try {
            String url = BASE_URL + "/get_round_info?" + COMMON_PARAMS;
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            return restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    RoundInfoResponseDTO.class
            ).getBody();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public RoundResultResponseDTO getRoundResultByRoundTime(Integer roundTime) {
        try {
            String url = BASE_URL + "/get_result?round_times=" + roundTime +
                    "&party_id=613a79a4a679ae79eb0b1420&" + COMMON_PARAMS;
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            return restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    RoundResultResponseDTO.class
            ).getBody();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}