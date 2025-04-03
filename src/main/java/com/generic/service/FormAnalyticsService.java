package com.generic.service;

import com.generic.model.FormAnalytics;
import reactor.core.publisher.Mono;

public interface FormAnalyticsService {
    Mono<FormAnalytics> updateEntityFields(FormAnalytics existing, FormAnalytics updated);
    Mono<FormAnalytics> incrementViewCount(String formId);
    Mono<FormAnalytics> createDefaultAnalytics(String formId);
    Mono<FormAnalytics> findByFormId(String formId);
}
