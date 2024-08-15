package com.virgo.ecommerce.utils.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {

    @NotBlank
    @NotNull
    @NotEmpty
    private String username;

    @NotBlank
    @NotNull
    @NotEmpty
    @Email
    private String email;

    @NotBlank
    @NotNull
    @NotEmpty
    private String password;


}
