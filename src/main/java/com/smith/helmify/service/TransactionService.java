package com.smith.helmify.service;

import com.smith.helmify.model.meta.Transaction;
import com.smith.helmify.utils.dto.TransactionResponseDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransRequestDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransResponseDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransSnapRequestDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransSnapResponseDTO;

import java.util.List;

public interface TransactionService {
    MidtransResponseDTO create(MidtransRequestDTO req);
    List<TransactionResponseDTO> getAll(Integer userId);
    TransactionResponseDTO getById(Integer id);
    MidtransSnapResponseDTO createSnap(MidtransSnapRequestDTO req);
    void refreshAndUpdateTransactionStatus(Transaction obj, String machine_id);
}
