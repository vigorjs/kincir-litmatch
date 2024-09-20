package com.smith.helmify.utils.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smith.helmify.model.meta.User;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponseDTO {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    @Nullable
    private String refreshToken;

    @JsonProperty("user")
    @Nullable
    private User user;
}
