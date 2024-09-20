package com.smith.helmify.utils.dto.restClientDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IotResponseDTO {

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

}
