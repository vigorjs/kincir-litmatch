package com.virgo.ecommerce.model.meta;

import com.virgo.ecommerce.model.enums.TransactionStatus;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private Integer id;
    private Integer user_id;
    private String order_id;
    private String status;
    private Long gross_amount;

}