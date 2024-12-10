package com.example.kincir.utils.responseWrapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Generic Success Response Wrapper")
public class SuccessResponse<T> {
    @Schema(example = "SUCCESS")
    private String message;

    @Schema(example = "200")
    private Integer status;

    private T data;
}
