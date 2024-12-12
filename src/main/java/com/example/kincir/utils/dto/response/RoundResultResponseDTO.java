package com.example.kincir.utils.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoundResultResponseDTO {
    @JsonProperty("data")
    private Data data;
    @JsonProperty("result")
    private int result;
    @JsonProperty("success")
    private boolean success;

    @Getter
    @Setter
    public static class Data {
        @JsonProperty("file_id")
        private String fileId;
    }
}