package com.smith.helmify.service;

import com.smith.helmify.utils.dto.restClientDto.MidtransRequestDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransResponseDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransSnapRequestDTO;
import com.smith.helmify.utils.dto.restClientDto.MidtransSnapResponseDTO;

public interface MidtransService {
    MidtransResponseDTO chargePayment(MidtransRequestDTO req);
    MidtransSnapResponseDTO chargePaymentSnap(MidtransSnapRequestDTO req);

    MidtransResponseDTO getStatus(String order_id);

    MidtransResponseDTO changeStatus(String order_id, String status);
}
