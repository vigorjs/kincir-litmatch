package com.smith.helmify.utils.dto.restClientDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smith.helmify.model.meta.Transaction;
import com.smith.helmify.model.meta.TransactionDetail;
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
