package com.generic.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class KafkaEventHandler<V> {
    private final ReactiveKafkaConsumerTemplate<String, V> consumerTemplate;
    private final String topic;
    private final Function<V, Mono<Void>> eventProcessor;

    /**
     * Start consuming events
     */
    public Flux<V> consumeEvents() {
        return consumerTemplate
                .receiveAutoAck()
                .doOnNext(record ->
                        log.info("Received event: key={}, value={} from topic={}, partition={}, offset={}",
                                record.key(), record.value(), record.topic(), record.partition(), record.offset())
                )
                .map(ConsumerRecord::value)
                .doOnError(e -> log.error("Error consuming event from topic: {}", topic, e));
    }

    /**
     * Start consuming and processing events
     */
    public void startProcessing() {
        consumeEvents()
                .flatMap(eventProcessor)
                .subscribe(
                        success -> log.debug("Successfully processed event from topic: {}", topic),
                        error -> log.error("Error processing event from topic: {}", topic, error)
                );
    }
}
