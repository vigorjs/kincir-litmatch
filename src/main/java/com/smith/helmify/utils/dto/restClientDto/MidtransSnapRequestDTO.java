package com.smith.helmify.utils.dto.restClientDto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MidtransSnapRequestDTO {

    @NotNull
    private String machine_id;

    @Nullable
    private List<MidtransSnapRequestDTO.ServiceRequest> item_details;

    private MidtransSnapRequestDTO.TransactionDetails transaction_details;

    @Nullable
    private MidtransSnapRequestDTO.CreditCard credit_card;

    @Nullable
    private MidtransSnapRequestDTO.CustomExpiry custom_expiry;

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
    public static class CreditCard {
        private Boolean secure = true;
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
