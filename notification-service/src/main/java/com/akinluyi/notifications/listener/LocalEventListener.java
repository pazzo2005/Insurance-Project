package com.akinluyi.notifications.listener;

import com.akinluyi.claims.common.event.ClaimApprovedEvent;
import com.akinluyi.claims.common.event.ClaimRejectedEvent;
import com.akinluyi.claims.common.event.ClaimSubmittedEvent;
import com.akinluyi.notifications.store.Notification;
import com.akinluyi.notifications.store.NotificationStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * In-process listener (default profile). Consumes claim events delivered via
 * Spring's application-event bus within this context. The kafka profile uses
 * {@link KafkaEventListener} instead for genuine cross-service delivery.
 */
@Component
@Profile("!kafka")
public class LocalEventListener {

    private static final Logger log = LoggerFactory.getLogger(LocalEventListener.class);

    private final NotificationStore store;

    public LocalEventListener(NotificationStore store) {
        this.store = store;
    }

    @EventListener
    public void onSubmitted(ClaimSubmittedEvent event) {
        record("CLAIM_SUBMITTED", event.claimNumber(),
                "Claim " + event.claimNumber() + " submitted by " + event.claimantName());
    }

    @EventListener
    public void onApproved(ClaimApprovedEvent event) {
        record("CLAIM_APPROVED", event.claimNumber(),
                "Claim " + event.claimNumber() + " was approved");
    }

    @EventListener
    public void onRejected(ClaimRejectedEvent event) {
        record("CLAIM_REJECTED", event.claimNumber(),
                "Claim " + event.claimNumber() + " was rejected: " + event.reason());
    }

    private void record(String type, String claimNumber, String message) {
        log.info("Notification [{}] {}", type, message);
        store.record(Notification.of(type, claimNumber, message));
    }
}
