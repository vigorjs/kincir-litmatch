package com.smith.helmify.utils.dto.restClientDto;

import com.smith.helmify.model.meta.Service;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MidtransRequestDTO {

    @NotNull
    private  String machine_id;

    @Nullable
    private String payment_type;

    private List<ServiceRequest> item_detail;

    private TransactionDetails transaction_details;

    private BankTransfer bank_transfer;

    @Nullable
    private CustomExpiry custom_expiry;

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class ServiceRequest{
        private String name;
        private Long price;
        private Integer serviceId;

        @Nullable
        private Integer quantity = 1;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TransactionDetails {
        @Nullable
        private String order_id;

        @Nullable
        private Long gross_amount;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class BankTransfer {
        private String bank;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomExpiry {
        private String unit;
        private Integer expiry_duration;
        private String order_time;
    }


}
