package com.virgo.ecommerce.service;

import com.virgo.ecommerce.utils.dto.restClientDto.MidtransRequestDTO;
import com.virgo.ecommerce.utils.dto.restClientDto.MidtransResponseDTO;

public interface TransactionService {
    MidtransResponseDTO create(MidtransRequestDTO req);
}
