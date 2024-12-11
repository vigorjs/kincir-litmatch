package com.example.kincir.utils.dto.request;

import com.example.kincir.model.meta.SubscriptionPlan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequestDTO {

    @NotNull
    @NotBlank
    @NotEmpty
    private SubscriptionPlan subscriptionPlan;
}
