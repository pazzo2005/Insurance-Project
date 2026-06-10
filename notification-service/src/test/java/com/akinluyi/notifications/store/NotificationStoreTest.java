package com.akinluyi.notifications.store;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NotificationStoreTest {

    @Test
    void records_and_returns_notifications() {
        NotificationStore store = new NotificationStore();
        store.record(Notification.of("CLAIM_SUBMITTED", "CLM-1", "submitted"));
        store.record(Notification.of("CLAIM_APPROVED", "CLM-1", "approved"));

        assertThat(store.size()).isEqualTo(2);
        assertThat(store.findAll()).extracting(Notification::claimNumber)
                .containsExactly("CLM-1", "CLM-1");
    }
}
