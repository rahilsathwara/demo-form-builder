package com.generic.repository;

import com.generic.model.FormAnalytics;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface FormAnalyticsRepository extends ReactiveMongoRepository<FormAnalytics, String> {
    Mono<FormAnalytics> findByFormId(String formId);
}
