package com.generic.service;

import com.generic.enumration.TemplateCategory;
import com.generic.model.FormTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FormTemplateService {
    Mono<FormTemplate> getTemplateById(String id);
    Flux<FormTemplate> getTemplatesByCategory(TemplateCategory category);
    Flux<FormTemplate> getSystemTemplates();
    Mono<FormTemplate> createTemplate(FormTemplate template);
    Mono<FormTemplate> updateTemplate(String id, FormTemplate templateUpdates);
    Mono<FormTemplate> incrementUsageCount(String id);
    Mono<Void> deleteTemplate(String id);
}