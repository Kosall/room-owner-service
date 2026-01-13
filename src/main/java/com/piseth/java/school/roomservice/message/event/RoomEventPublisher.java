package com.piseth.java.school.roomservice.message.event;

import reactor.core.publisher.Mono;

public interface RoomEventPublisher {
    Mono<Void> publish(String topic, String key, RoomEventEnvelope<?> envelope);
}