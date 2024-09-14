package com.smith.helmify.utils.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequestDTO {

    @Min(message = "stars cant be negative", value = 0)
    @Max(message = "stars cant be more than 5", value = 5)
    private Integer stars;

    private String title;

    @Nullable
    private String description;
}