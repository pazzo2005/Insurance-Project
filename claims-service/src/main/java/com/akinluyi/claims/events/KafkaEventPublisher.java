package com.akinluyi.claims.events;

import com.akinluyi.claims.common.event.ClaimEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka-backed event publisher (active under the {@code kafka} profile). Routes
 * each claim event to its topic, keyed by claim number for ordering per claim.
 */
@Component
@Profile("kafka")
public class KafkaEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(ClaimEvent event) {
        String topic = KafkaTopics.topicFor(event);
        log.info("Publishing {} to Kafka topic {} for claim {}",
                event.getClass().getSimpleName(), topic, event.claimNumber());
        kafkaTemplate.send(topic, event.claimNumber(), event);
    }
}
