package com.akinluyi.claims.events;

import com.akinluyi.claims.common.event.ClaimEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Default in-process event publisher. Delivers events to Spring
 * {@code @EventListener} beans within this application context — zero
 * infrastructure required, ideal for local development and tests.
 */
@Component
@Profile("!kafka")
public class LocalEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LocalEventPublisher.class);

    private final ApplicationEventPublisher delegate;

    public LocalEventPublisher(ApplicationEventPublisher delegate) {
        this.delegate = delegate;
    }

    @Override
    public void publish(ClaimEvent event) {
        log.info("Publishing {} (in-process) for claim {}",
                event.getClass().getSimpleName(), event.claimNumber());
        delegate.publishEvent(event);
    }
}
