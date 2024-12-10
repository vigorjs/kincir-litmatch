package com.example.kincir.utils.responseWrapper;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class Response {
    public static <T> ResponseEntity<SuccessResponse<T>> success(HttpStatus httpStatus, String message, T data) {
        SuccessResponse<T> response = SuccessResponse.<T>builder()
                .status(httpStatus.value())
                .message(message)
                .data(data)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    public static <T> ResponseEntity<SuccessResponse<T>> success(HttpStatus httpStatus, String message) {
        SuccessResponse<T> response = SuccessResponse.<T>builder()
                .status(httpStatus.value())
                .message(message)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    public static <T> ResponseEntity<SuccessResponse<T>> success(T data, HttpStatus httpStatus) {
        SuccessResponse<T> response = SuccessResponse.<T>builder()
                .message("SUCCESS")
                .data(data)
                .status(httpStatus.value())
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    public static <T> ResponseEntity<SuccessResponse<T>> success(T data) {
        return success(data, HttpStatus.OK);
    }

    public static ResponseEntity<ErrorResponse> error(List<String> errors, HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus).body(ErrorResponse.builder()
                .message("ERROR")
                .errors(errors)
                .status(httpStatus.value())
                .build()
        );
    }

    public static ResponseEntity<ErrorResponse> error(String message, HttpStatus httpStatus) {
        return error(List.of(message), httpStatus);
    }

    public static <T> ResponseEntity<PageableResponse<T>> pageable(Page<T> pageable, HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus).body(PageableResponse.<T>builder()
                .message("Success")
                .status(httpStatus.value())
                .items(pageable.getContent())
                .totalItems(pageable.getTotalElements())
                .currentPage(pageable.getNumber())
                .totalPages(pageable.getTotalPages())
                .build()
        );
    }
}
