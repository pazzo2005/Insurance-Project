package com.akinluyi.claims.events;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

/** Declares the claim event topics (created automatically under the kafka profile). */
@Configuration
@Profile("kafka")
public class KafkaConfig {

    @Bean
    NewTopic claimSubmittedTopic() {
        return TopicBuilder.name(KafkaTopics.CLAIM_SUBMITTED).partitions(1).replicas(1).build();
    }

    @Bean
    NewTopic claimApprovedTopic() {
        return TopicBuilder.name(KafkaTopics.CLAIM_APPROVED).partitions(1).replicas(1).build();
    }

    @Bean
    NewTopic claimRejectedTopic() {
        return TopicBuilder.name(KafkaTopics.CLAIM_REJECTED).partitions(1).replicas(1).build();
    }
}
