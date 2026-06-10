package com.akinluyi.notifications.listener;

import static org.assertj.core.api.Assertions.assertThat;

import com.akinluyi.claims.common.domain.ClaimType;
import com.akinluyi.claims.common.event.ClaimApprovedEvent;
import com.akinluyi.claims.common.event.ClaimRejectedEvent;
import com.akinluyi.claims.common.event.ClaimSubmittedEvent;
import com.akinluyi.notifications.store.Notification;
import com.akinluyi.notifications.store.NotificationStore;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Verifies the in-process event flow end to end: events published on the Spring
 * application-event bus are consumed by {@link LocalEventListener} and recorded.
 */
@SpringBootTest
class LocalEventListenerTest {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private NotificationStore store;

    @Test
    void records_a_notification_for_each_claim_event() {
        publisher.publishEvent(new ClaimSubmittedEvent(
                1L, "CLM-1", "Jane Doe", ClaimType.AUTO, new BigDecimal("100"), Instant.now()));
        publisher.publishEvent(new ClaimApprovedEvent(1L, "CLM-1", Instant.now()));
        publisher.publishEvent(new ClaimRejectedEvent(2L, "CLM-2", "missing docs", Instant.now()));

        assertThat(store.findAll()).extracting(Notification::type)
                .containsExactly("CLAIM_SUBMITTED", "CLAIM_APPROVED", "CLAIM_REJECTED");
    }
}
