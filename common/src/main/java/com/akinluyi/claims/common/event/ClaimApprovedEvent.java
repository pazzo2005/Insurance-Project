package com.akinluyi.claims.common.event;

import java.time.Instant;

/** Emitted when a claim is approved. */
public record ClaimApprovedEvent(
        Long claimId,
        String claimNumber,
        Instant occurredAt) implements ClaimEvent {
}
