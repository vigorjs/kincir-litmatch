package com.example.kincir.utils.dto.response;

import com.example.kincir.model.meta.Transaction;
import com.example.kincir.model.meta.TransactionDetail;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
