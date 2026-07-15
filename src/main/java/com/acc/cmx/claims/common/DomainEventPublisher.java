package com.acc.cmx.claims.common;

public interface DomainEventPublisher {
    void publish(Object event);
}
