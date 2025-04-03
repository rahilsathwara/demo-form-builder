package com.generic.service.processor;

import com.generic.config.ReactiveKafkaConfig;
import com.generic.model.FormSubmission;
import com.generic.model.Forms;
import com.generic.service.event.KafkaEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class EventProcessorConfig {
    @Value("${spring.kafka.form-topic}") private String formTopic;
    @Value("${spring.kafka.submission-topic}") private String submissionTopic;

    @Bean
    public KafkaEventHandler<Forms> formEventHandler(
            ReactiveKafkaConfig reactiveKafkaConfig) {
        return new KafkaEventHandler<>(
                reactiveKafkaConfig.createConsumerTemplate(formTopic, Forms.class),
                formTopic,
                this::processFormEvent
        );
    }

    @Bean
    public KafkaEventHandler<FormSubmission> submissionEventHandler(
            ReactiveKafkaConfig reactiveKafkaConfig) {
        return new KafkaEventHandler<>(
                reactiveKafkaConfig.createConsumerTemplate(submissionTopic, FormSubmission.class),
                submissionTopic,
                this::processSubmissionEvent
        );
    }

    private Mono<Void> processFormEvent(Forms form) {
        log.info("Processing form event for form ID: {}", form.getId());
        // Implement form event processing logic here
        return Mono.empty();
    }

    private Mono<Void> processSubmissionEvent(FormSubmission submission) {
        log.info("Processing submission event for submission ID: {}", submission.getId());
        // Implement submission event processing logic here
        return Mono.empty();
    }

    @Bean
    public Void startEventProcessors(
            KafkaEventHandler<Forms> formEventHandler,
            KafkaEventHandler<FormSubmission> submissionEventHandler) {
        // Start processing events
        formEventHandler.startProcessing();
        submissionEventHandler.startProcessing();
        return null;
    }
}
