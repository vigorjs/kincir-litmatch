package com.virgo.ecommerce.utils.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.virgo.ecommerce.model.meta.User;
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
    private String refreshToken;
    @JsonProperty("user")
    @Nullable
    private User user;
}
