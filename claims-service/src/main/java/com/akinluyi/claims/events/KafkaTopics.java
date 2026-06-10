package com.akinluyi.claims.events;

import com.akinluyi.claims.common.event.ClaimApprovedEvent;
import com.akinluyi.claims.common.event.ClaimEvent;
import com.akinluyi.claims.common.event.ClaimRejectedEvent;
import com.akinluyi.claims.common.event.ClaimSubmittedEvent;

/** Kafka topic names and event-to-topic routing. */
public final class KafkaTopics {

    public static final String CLAIM_SUBMITTED = "claim.submitted";
    public static final String CLAIM_APPROVED = "claim.approved";
    public static final String CLAIM_REJECTED = "claim.rejected";

    private KafkaTopics() {
    }

    public static String topicFor(ClaimEvent event) {
        if (event instanceof ClaimSubmittedEvent) {
            return CLAIM_SUBMITTED;
        }
        if (event instanceof ClaimApprovedEvent) {
            return CLAIM_APPROVED;
        }
        if (event instanceof ClaimRejectedEvent) {
            return CLAIM_REJECTED;
        }
        throw new IllegalArgumentException("No topic for event: " + event.getClass());
    }
}
