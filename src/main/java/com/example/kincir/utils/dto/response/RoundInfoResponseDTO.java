package com.example.kincir.utils.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoundInfoResponseDTO {
    @JsonProperty("data")
    private Data data;
    @JsonProperty("result")
    private int result;
    @JsonProperty("success")
    private boolean success;

    @Getter
    @Setter
    public static class Data {

        @JsonProperty("end")
        private long end;

        @JsonProperty("now")
        private long now;

        @JsonProperty("round_times")
        private int roundTimes;

        @JsonProperty("start")
        private long start;
    }
}