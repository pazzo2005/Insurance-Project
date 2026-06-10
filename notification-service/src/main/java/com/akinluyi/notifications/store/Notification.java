package com.akinluyi.notifications.store;

import java.time.Instant;

/** A notification recorded in response to a claim event. */
public record Notification(
        String type,
        String claimNumber,
        String message,
        Instant receivedAt) {

    public static Notification of(String type, String claimNumber, String message) {
        return new Notification(type, claimNumber, message, Instant.now());
    }
}
