package com.akinluyi.claims.events;

import com.akinluyi.claims.common.event.ClaimEvent;

/**
 * Abstraction over event delivery. Two implementations are selected by Spring
 * profile: {@code LocalEventPublisher} (default) delivers events in-process via
 * Spring's {@code ApplicationEventPublisher}; {@code KafkaEventPublisher}
 * (profile {@code kafka}) publishes them to Kafka topics for cross-service
 * consumption.
 */
public interface EventPublisher {

    void publish(ClaimEvent event);
}
