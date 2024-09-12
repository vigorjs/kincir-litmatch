package com.smith.helmify.service;

import com.smith.helmify.utils.dto.restClientDto.MidtransRequestDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransResponseDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransSnapRequestDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransSnapResponseDTO;

public interface TransactionService {
    MidtransResponseDTO create(MidtransRequestDTO req);
    MidtransSnapResponseDTO createSnap(MidtransSnapRequestDTO req);
}
