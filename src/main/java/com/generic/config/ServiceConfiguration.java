package com.generic.config;

import com.generic.model.*;
import com.generic.service.cache.RedisCacheService;
import com.generic.service.event.KafkaEventService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;

import java.time.Duration;

@Configuration
public class ServiceConfiguration {
    @Value("${spring.kafka.form-topic:form-events}") private String formTopic;
    @Value("${spring.kafka.submission-topic:form-submissions}") private String submissionTopic;
    @Value("${spring.kafka.template-topic:template-events}") private String templateTopic;
    @Value("${spring.kafka.analytics-topic:analytics-events}") private String analyticsTopic;
    @Value("${spring.kafka.form-version-topic:form-versions}") private String formVersionsTopic;

    // Redis Cache Services
    @Bean
    public RedisCacheService<Forms, String> formCacheService(
            ReactiveRedisConnectionFactory redisFactory,
            RedisConfig redisConfig) {
        return new RedisCacheService<>(
                redisConfig.reactiveRedisTemplate(redisFactory, Forms.class),
                "form",
                Duration.ofHours(24)
        );
    }

    @Bean
    public RedisCacheService<FormSubmission, String> submissionCacheService(
            ReactiveRedisConnectionFactory redisFactory,
            RedisConfig redisConfig) {
        return new RedisCacheService<>(
                redisConfig.reactiveRedisTemplate(redisFactory, FormSubmission.class),
                "submission",
                Duration.ofHours(1)  // Shorter TTL for submissions
        );
    }

    @Bean
    public RedisCacheService<FormTemplate, String> templateCacheService(
            ReactiveRedisConnectionFactory redisFactory,
            RedisConfig redisConfig) {
        return new RedisCacheService<>(
                redisConfig.reactiveRedisTemplate(redisFactory, FormTemplate.class),
                "template",
                Duration.ofDays(7)  // Longer TTL for templates
        );
    }

    @Bean
    public RedisCacheService<FormAnalytics, String> analyticsCacheService(
            ReactiveRedisConnectionFactory redisFactory,
            RedisConfig redisConfig) {
        return new RedisCacheService<>(
                redisConfig.reactiveRedisTemplate(redisFactory, FormAnalytics.class),
                "analytics",
                Duration.ofMinutes(30)  // Analytics change frequently
        );
    }

    @Bean
    public RedisCacheService<FormVersion, String> formVersionCacheService(
            ReactiveRedisConnectionFactory redisFactory,
            RedisConfig redisConfig) {
        return new RedisCacheService<>(
                redisConfig.reactiveRedisTemplate(redisFactory, FormVersion.class),
                "versions",
                Duration.ofDays(1)
        );
    }


    // Kafka Event Services
    @Bean
    public KafkaEventService<String, Forms> formEventService(
            ReactiveKafkaConfig kafkaConfig) {
        ReactiveKafkaProducerTemplate<String, Forms> template = kafkaConfig.reactiveKafkaProducerTemplate(String.class, Forms.class);

        return new KafkaEventService<>(
                template,
                formTopic
        );
    }

    @Bean
    public KafkaEventService<String, FormSubmission> submissionEventService(
            ReactiveKafkaConfig kafkaConfig) {

        ReactiveKafkaProducerTemplate<String, FormSubmission> template =
                kafkaConfig.reactiveKafkaProducerTemplate(String.class, FormSubmission.class);

        return new KafkaEventService<>(template, submissionTopic);
    }

    @Bean
    public KafkaEventService<String, FormTemplate> templateEventService(
            ReactiveKafkaConfig kafkaConfig) {

        ReactiveKafkaProducerTemplate<String, FormTemplate> template =
                kafkaConfig.reactiveKafkaProducerTemplate(String.class, FormTemplate.class);

        return new KafkaEventService<>(template, templateTopic);
    }

    @Bean
    public KafkaEventService<String, FormAnalytics> analyticsEventService(
            ReactiveKafkaConfig kafkaConfig) {

        ReactiveKafkaProducerTemplate<String, FormAnalytics> template =
                kafkaConfig.reactiveKafkaProducerTemplate(String.class, FormAnalytics.class);

        return new KafkaEventService<>(template, analyticsTopic);
    }

    @Bean
    public KafkaEventService<String, FormVersion> versionsEventService(
            ReactiveKafkaConfig kafkaConfig) {

        ReactiveKafkaProducerTemplate<String, FormVersion> template =
                kafkaConfig.reactiveKafkaProducerTemplate(String.class, FormVersion.class);

        return new KafkaEventService<>(template, formVersionsTopic);
    }
}
