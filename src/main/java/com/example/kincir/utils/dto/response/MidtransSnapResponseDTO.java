package com.example.kincir.utils.dto.response;

import com.example.kincir.model.meta.Transaction;
import com.example.kincir.model.meta.TransactionDetail;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MidtransSnapResponseDTO {

    @JsonProperty("token")
    private String token;

    @JsonProperty("redirect_url")
    private String redirect_url;

    @JsonProperty("transaction")
    private Transaction transaction;

    @JsonProperty("transaction_detail")
    private List<TransactionDetail> transaction_detail;
}
