package com.example.kincir.utils.responseWrapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageableResponse<T> {
    private String message;
    private Integer status;
    private List<T> items;
    private Long totalItems;
    private Integer currentPage;
    private Integer totalPages;
}
