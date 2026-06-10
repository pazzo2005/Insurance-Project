package com.akinluyi.claims.exception;

/** Thrown when a claim cannot be found by id. */
public class ClaimNotFoundException extends RuntimeException {

    public ClaimNotFoundException(Long id) {
        super("Claim not found: " + id);
    }
}
