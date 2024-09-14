package com.smith.helmify.utils.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequestDTO {

    private String machine_id;

    private String service_name;

    @Nullable
    private String service_description;

    @Min(value = 0, message = "price cant be negative")
    private Long price;

    @Nullable
    @Min(value = 0, message = "Error stock cant be negative")
    private Integer stock;

    private String category;

    private MultipartFile multipartFile;
}