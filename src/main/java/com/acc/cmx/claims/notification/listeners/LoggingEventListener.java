package com.acc.cmx.claims.notification.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoggingEventListener {

    @EventListener
    public void onAny(Object event) {
        log.info("Domain event received: {}", event.getClass().getSimpleName());
    }
}
