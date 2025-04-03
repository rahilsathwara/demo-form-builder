package com.generic.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class ReactiveKafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}") private String bootstrapServers;
    @Value("${spring.kafka.consumer.group-id}") private String groupId;


    @Bean
    public <K, V> ReactiveKafkaProducerTemplate<K, V> reactiveKafkaProducerTemplate(
            Class<K> keyClass,
            Class<V> valueClass) {

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, getSerializerForClass(keyClass));
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, getSerializerForClass(valueClass));

        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(props));
    }

    // Consumer factory method - renamed to avoid conflicts
    public <V> ReactiveKafkaConsumerTemplate<String, V> createConsumerTemplate(
            String topic,
            Class<V> valueClass) {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.generic.model");

        ReceiverOptions<String, V> receiverOptions = ReceiverOptions
                .<String, V>create(props)
                .subscription(Collections.singleton(topic));

        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }



    private Class<?> getSerializerForClass(Class<?> dataClass) {
        if (String.class.equals(dataClass)) {
            return StringSerializer.class;
        } else {
            return JsonSerializer.class;
        }
    }
}
