package com.akinluyi.claims.dto;

import com.akinluyi.claims.common.domain.ClaimStatus;
import com.akinluyi.claims.common.domain.ClaimType;
import com.akinluyi.claims.domain.Claim;
import java.math.BigDecimal;
import java.time.Instant;

/** API representation of a {@link Claim}. */
public record ClaimResponse(
        Long id,
        String claimNumber,
        String policyNumber,
        String claimantName,
        ClaimType type,
        String description,
        BigDecimal amount,
        ClaimStatus status,
        String rejectionReason,
        Instant createdAt,
        Instant updatedAt) {

    public static ClaimResponse from(Claim claim) {
        return new ClaimResponse(
                claim.getId(),
                claim.getClaimNumber(),
                claim.getPolicyNumber(),
                claim.getClaimantName(),
                claim.getType(),
                claim.getDescription(),
                claim.getAmount(),
                claim.getStatus(),
                claim.getRejectionReason(),
                claim.getCreatedAt(),
                claim.getUpdatedAt());
    }
}
