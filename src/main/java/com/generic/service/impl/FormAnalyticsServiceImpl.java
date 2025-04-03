package com.generic.service.impl;

import com.generic.model.FormAnalytics;
import com.generic.repository.FormAnalyticsRepository;
import com.generic.service.FormAnalyticsService;
import com.generic.service.base.GenericEntityService;
import com.generic.service.cache.RedisCacheService;
import com.generic.service.event.KafkaEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@Slf4j
public class FormAnalyticsServiceImpl extends GenericEntityService<FormAnalytics, String> implements FormAnalyticsService {

    public FormAnalyticsServiceImpl(
            FormAnalyticsRepository repository,
            RedisCacheService<FormAnalytics, String> analyticsCacheService,
            KafkaEventService<String, FormAnalytics> analyticsEventService) {
        super(repository, analyticsCacheService, analyticsEventService, FormAnalytics::getId);
    }

    @Override
    public Mono<FormAnalytics> updateEntityFields(FormAnalytics existing, FormAnalytics updated) {
        if (updated.getViewCount() > 0) {
            existing.setViewCount(existing.getViewCount() + updated.getViewCount());
        }
        if (updated.getSubmissionCount() > 0) {
            existing.setSubmissionCount(existing.getSubmissionCount() + updated.getSubmissionCount());
        }
        if (updated.getAbandonmentCount() > 0) {
            existing.setAbandonmentCount(existing.getAbandonmentCount() + updated.getAbandonmentCount());
        }
        if (updated.getMetricData() != null) {
            if (existing.getMetricData() == null) {
                existing.setMetricData(new HashMap<>());
            }
            existing.getMetricData().putAll(updated.getMetricData());
        }

        existing.setLastUpdated(LocalDateTime.now());
        return Mono.just(existing);
    }

    @Override
    public Mono<FormAnalytics> findByFormId(String formId) {
        return ((FormAnalyticsRepository) repository).findByFormId(formId);
    }

    @Override
    public Mono<FormAnalytics> incrementViewCount(String formId) {
        return ((FormAnalyticsRepository) repository).findByFormId(formId)
                .switchIfEmpty(createDefaultAnalytics(formId))
                .map(analytics -> {
                    analytics.setViewCount(analytics.getViewCount() + 1);
                    analytics.setLastUpdated(LocalDateTime.now());
                    return analytics;
                })
                .flatMap(this::create);
    }

    @Override
    public Mono<FormAnalytics> createDefaultAnalytics(String formId) {
        FormAnalytics analytics = new FormAnalytics();
        analytics.setFormId(formId);
        analytics.setViewCount(0);
        analytics.setSubmissionCount(0);
        analytics.setAbandonmentCount(0);
        analytics.setMetricData(new HashMap<>());
        analytics.setLastUpdated(LocalDateTime.now());
        return Mono.just(analytics);
    }
}
