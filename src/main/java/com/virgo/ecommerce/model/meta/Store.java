package com.virgo.ecommerce.model.meta;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Store {

    private Integer id;
    private Integer user_id;
    private String store_name;
    private String store_description;

}