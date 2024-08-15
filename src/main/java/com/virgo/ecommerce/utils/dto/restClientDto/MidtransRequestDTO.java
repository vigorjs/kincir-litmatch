package com.virgo.ecommerce.utils.dto.restClientDto;

import com.virgo.ecommerce.model.meta.Product;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MidtransRequestDTO {

    @Nullable
    private String payment_type;

    private List<ProductRequest> item_detail;

    private TransactionDetails transaction_details;

    private BankTransfer bank_transfer;

    @Nullable
    private CustomExpiry custom_expiry;

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class ProductRequest{
        private Product product;
        private Integer quantity;
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
