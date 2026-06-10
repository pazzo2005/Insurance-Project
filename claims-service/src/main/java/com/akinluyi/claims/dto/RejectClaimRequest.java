package com.akinluyi.claims.dto;

import jakarta.validation.constraints.NotBlank;

/** Request body for rejecting a claim. */
public record RejectClaimRequest(@NotBlank String reason) {
}
