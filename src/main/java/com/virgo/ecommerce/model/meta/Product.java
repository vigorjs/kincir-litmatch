package com.virgo.ecommerce.model.meta;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Integer id;
    private Integer store_id;
    private Long price;
    private String product_name;
    private String product_description;
}