package com.smith.helmify.utils.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smith.helmify.model.meta.Transaction;
import com.smith.helmify.model.meta.TransactionDetail;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDTO {
    @JsonProperty("transaction")
    @NotNull
    private Transaction transaction;

    @JsonProperty("transaction_details")
    @NotNull
    private List<TransactionDetail> transactionDetails;
}
