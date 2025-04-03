package com.generic.repository;

import com.generic.model.FormSubmission;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface FormSubmissionRepository extends ReactiveMongoRepository<FormSubmission, String> {

    Flux<FormSubmission> findByFormId(String formId);

    Flux<FormSubmission> findByFormIdAndSubmittedAtBetween(
            String formId, LocalDateTime start, LocalDateTime end);

    Mono<Long> countByFormId(String formId);

    @Query(value = "{'formId': ?0}", count = true)
    Mono<Long> getSubmissionCount(String formId);
}
