package com.example.kincir.service;

import com.example.kincir.utils.dto.request.MidtransRequestDTO;
import com.example.kincir.utils.dto.request.MidtransSnapRequestDTO;
import com.example.kincir.utils.dto.response.MidtransResponseDTO;
import com.example.kincir.utils.dto.response.MidtransSnapResponseDTO;

public interface MidtransService {
    MidtransResponseDTO chargePayment(MidtransRequestDTO req);
    MidtransSnapResponseDTO chargePaymentSnap(MidtransSnapRequestDTO
                                                      req);

    MidtransResponseDTO getStatus(String order_id);

    MidtransResponseDTO changeStatus(String order_id, String status);
}
