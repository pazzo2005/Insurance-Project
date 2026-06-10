package com.akinluyi.notifications.listener;

import com.akinluyi.claims.common.event.ClaimApprovedEvent;
import com.akinluyi.claims.common.event.ClaimRejectedEvent;
import com.akinluyi.claims.common.event.ClaimSubmittedEvent;
import com.akinluyi.notifications.store.Notification;
import com.akinluyi.notifications.store.NotificationStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener (active under the {@code kafka} profile). Consumes claim events
 * from Kafka topics published by the claims service — the cross-service,
 * production-shaped event flow. Topic names match
 * {@code claims-service} KafkaTopics.
 */
@Component
@Profile("kafka")
public class KafkaEventListener {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventListener.class);

    private final NotificationStore store;

    public KafkaEventListener(NotificationStore store) {
        this.store = store;
    }

    @KafkaListener(topics = "claim.submitted", groupId = "notification-service")
    public void onSubmitted(ClaimSubmittedEvent event) {
        record("CLAIM_SUBMITTED", event.claimNumber(),
                "Claim " + event.claimNumber() + " submitted by " + event.claimantName());
    }

    @KafkaListener(topics = "claim.approved", groupId = "notification-service")
    public void onApproved(ClaimApprovedEvent event) {
        record("CLAIM_APPROVED", event.claimNumber(),
                "Claim " + event.claimNumber() + " was approved");
    }

    @KafkaListener(topics = "claim.rejected", groupId = "notification-service")
    public void onRejected(ClaimRejectedEvent event) {
        record("CLAIM_REJECTED", event.claimNumber(),
                "Claim " + event.claimNumber() + " was rejected: " + event.reason());
    }

    private void record(String type, String claimNumber, String message) {
        log.info("Notification [{}] {}", type, message);
        store.record(Notification.of(type, claimNumber, message));
    }
}
