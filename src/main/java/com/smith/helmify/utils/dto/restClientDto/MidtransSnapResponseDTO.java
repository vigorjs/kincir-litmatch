package com.smith.helmify.utils.dto.restClientDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MidtransSnapResponseDTO {

    @JsonProperty("token")
    private String token;

    @JsonProperty("redirect_url")
    private String redirect_url;
}
