package com.akinluyi.claims.common.api;

import java.time.Instant;

/** Standard error payload returned by the REST APIs. */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path) {

    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(Instant.now(), status, error, message, path);
    }
}
