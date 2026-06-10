package com.akinluyi.claims.dto;

import com.akinluyi.claims.common.domain.ClaimType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/** Request body for submitting a new claim. */
public record SubmitClaimRequest(
        @NotBlank String policyNumber,
        @NotBlank String claimantName,
        @NotNull ClaimType type,
        @Size(max = 2000) String description,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal amount) {
}
