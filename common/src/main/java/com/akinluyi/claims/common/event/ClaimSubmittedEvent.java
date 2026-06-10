package com.akinluyi.claims.common.event;

import com.akinluyi.claims.common.domain.ClaimType;
import java.math.BigDecimal;
import java.time.Instant;

/** Emitted when a new claim is submitted. */
public record ClaimSubmittedEvent(
        Long claimId,
        String claimNumber,
        String claimantName,
        ClaimType type,
        BigDecimal amount,
        Instant occurredAt) implements ClaimEvent {
}
