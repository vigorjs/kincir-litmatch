package com.virgo.ecommerce.model.meta;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductStock {
    private Integer id;
    private Integer products_id;
    private Integer quantity;
}