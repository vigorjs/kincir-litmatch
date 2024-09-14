package com.smith.helmify.service;

import com.smith.helmify.model.meta.Transaction;
import com.smith.helmify.utils.dto.restClientDto.MidtransRequestDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransResponseDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransSnapRequestDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransSnapResponseDTO;

import java.util.List;

public interface TransactionService {
    MidtransResponseDTO create(MidtransRequestDTO req);
    List<Transaction> getAll(Integer userId);
    Transaction getById(Integer id);
    MidtransSnapResponseDTO createSnap(MidtransSnapRequestDTO req);
}
