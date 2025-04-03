package com.generic.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

@Slf4j
@RequiredArgsConstructor
public class KafkaEventService<K, V>{
    private final ReactiveKafkaProducerTemplate<K, V> kafkaTemplate;
    private final String topic;

    /**
     * Publish an event to Kafka
     */
    public Mono<SenderResult<Void>> publishEvent(K key, V event) {
        return kafkaTemplate.send(topic, key, event)
                .doOnSuccess(result -> log.info("Event published to topic: {} with key: {}", topic, key))
                .doOnError(error -> log.error("Failed to publish event to topic: {} with key: {}", topic, key, error));
    }
}
