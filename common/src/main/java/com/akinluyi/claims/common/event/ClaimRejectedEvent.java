package com.akinluyi.claims.common.event;

import java.time.Instant;

/** Emitted when a claim is rejected, carrying the rejection reason. */
public record ClaimRejectedEvent(
        Long claimId,
        String claimNumber,
        String reason,
        Instant occurredAt) implements ClaimEvent {
}
