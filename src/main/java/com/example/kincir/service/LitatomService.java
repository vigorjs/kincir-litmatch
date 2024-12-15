package com.example.kincir.service;

import com.example.kincir.utils.dto.response.RoundInfoResponseDTO;
import com.example.kincir.utils.dto.response.RoundResultResponseDTO;

public interface LitatomService {
    RoundInfoResponseDTO getRoundInfo();

    RoundResultResponseDTO getRoundResultByRoundTime(Integer roundTime);
}
