package com.virgo.ecommerce.service;

import com.virgo.ecommerce.utils.dto.restClientDto.MidtransRequestDTO;
import com.virgo.ecommerce.utils.dto.restClientDto.MidtransResponseDTO;

public interface MidtransService {
    MidtransResponseDTO chargePayment(MidtransRequestDTO req);

    MidtransResponseDTO getStatus(String order_id);

    MidtransResponseDTO changeStatus(String order_id, String status);
}
