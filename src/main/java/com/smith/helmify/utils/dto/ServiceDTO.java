package com.smith.helmify.utils.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceDTO {

    private Integer id;
    private String service_name;

    @Nullable
    private String service_description;

    @Min(value = 0, message = "price cant be negative")
    private Long price;

    @Min(value = 0, message = "price cant be negative")
    private Integer stock = 0;

}
