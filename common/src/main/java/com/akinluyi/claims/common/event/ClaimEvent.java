package com.akinluyi.claims.common.event;

import java.time.Instant;

/**
 * Common contract for all claim domain events. Implemented by immutable record
 * events that are published in-process (local profile) or to Kafka (kafka profile).
 */
public interface ClaimEvent {
    Long claimId();

    String claimNumber();

    Instant occurredAt();
}
