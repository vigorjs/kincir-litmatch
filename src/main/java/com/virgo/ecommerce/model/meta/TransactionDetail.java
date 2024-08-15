package com.virgo.ecommerce.model.meta;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetail {

    private Integer id;
    private Integer product_id;
    private Integer transaction_id;
    private Long amount;
    private Integer quantity;

}