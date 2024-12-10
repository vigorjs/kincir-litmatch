package com.example.kincir.service;

import com.example.kincir.model.meta.Transaction;
import com.example.kincir.utils.dto.request.MidtransRequestDTO;
import com.example.kincir.utils.dto.request.MidtransSnapRequestDTO;
import com.example.kincir.utils.dto.response.MidtransResponseDTO;
import com.example.kincir.utils.dto.response.MidtransSnapResponseDTO;
import com.example.kincir.utils.dto.response.TransactionResponseDTO;

import java.util.List;

public interface TransactionService {
    MidtransResponseDTO create(MidtransRequestDTO req);
    List<TransactionResponseDTO> getAll(Integer userId);
    TransactionResponseDTO getById(Integer id);
    MidtransSnapResponseDTO createSnap(MidtransSnapRequestDTO req);
    void refreshAndUpdateTransactionStatus(Transaction obj, String machine_id);
}
