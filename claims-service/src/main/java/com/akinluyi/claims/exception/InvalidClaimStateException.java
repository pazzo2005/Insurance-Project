package com.akinluyi.claims.exception;

/** Thrown when an operation is not valid for a claim's current status. */
public class InvalidClaimStateException extends RuntimeException {

    public InvalidClaimStateException(String message) {
        super(message);
    }
}
