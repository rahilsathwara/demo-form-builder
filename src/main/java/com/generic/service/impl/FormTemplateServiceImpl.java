package com.generic.service.impl;

import com.generic.enumration.TemplateCategory;
import com.generic.model.FormTemplate;
import com.generic.repository.FormTemplateRepository;
import com.generic.service.FormTemplateService;
import com.generic.service.base.GenericEntityService;
import com.generic.service.cache.RedisCacheService;
import com.generic.service.event.KafkaEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Slf4j
public class FormTemplateServiceImpl extends GenericEntityService<FormTemplate, String> implements FormTemplateService {

    private final FormTemplateRepository templateRepository;

    protected FormTemplateServiceImpl(FormTemplateRepository templateRepository, RedisCacheService<FormTemplate, String> cacheService, KafkaEventService<String, FormTemplate> eventService) {
        super(templateRepository, cacheService, eventService, FormTemplate::getId);
        this.templateRepository = templateRepository;
    }

    @Override
    public Mono<FormTemplate> getTemplateById(String id) {
        return findById(id);
    }

    @Override
    public Flux<FormTemplate> getTemplatesByCategory(TemplateCategory category) {
        return templateRepository.findByCategory(category);
    }

    @Override
    public Flux<FormTemplate> getSystemTemplates() {
        return templateRepository.findByIsSystem(Boolean.TRUE);
    }

    @Override
    public Mono<FormTemplate> createTemplate(FormTemplate template) {
        return create(template);
    }

    @Override
    public Mono<FormTemplate> updateTemplate(String id, FormTemplate templateUpdates) {
        FormTemplate existingTemplate = getTemplateById(id).block();
        return updateEntityFields(existingTemplate, templateUpdates);
    }

    @Override
    public Mono<FormTemplate> incrementUsageCount(String id) {
        return findById(id)  // Use generic method from parent class
                .flatMap(template -> {
                    // Apply domain-specific logic
                    template.setUsageCount(template.getUsageCount() + 1);

                    // Use generic update method that handles caching and events
                    return repository.save(template)
                            .flatMap(savedTemplate -> {
                                // Use the cache service from parent class
                                return cacheService.cache(id, savedTemplate)
                                        .flatMap(cachedTemplate ->
                                                // Use the event service from parent class
                                                eventService.publishEvent(id, cachedTemplate)
                                                        .thenReturn(cachedTemplate)
                                        );
                            });
                });
    }



    @Override
    public Mono<Void> deleteTemplate(String id) {
        return delete(id);
    }

    @Override
    protected Mono<FormTemplate> updateEntityFields(FormTemplate existingTemplate, FormTemplate templateUpdates) {
        if (templateUpdates.getName() != null) {
            existingTemplate.setName(templateUpdates.getName());
        }
        if (templateUpdates.getDescription() != null) {
            existingTemplate.setDescription(templateUpdates.getDescription());
        }
        if (templateUpdates.getCategory() != null) {
            existingTemplate.setCategory(templateUpdates.getCategory());
        }
        if (templateUpdates.getThumbnail() != null) {
            existingTemplate.setThumbnail(templateUpdates.getThumbnail());
        }
        if (templateUpdates.getFormDefinition() != null) {
            existingTemplate.setFormDefinition(templateUpdates.getFormDefinition());
        }
        existingTemplate.setUpdatedAt(LocalDateTime.now());

        return Mono.just(existingTemplate);
    }


    /**
     * Example of a more complex custom method that combines generic and specific logic
     */
    public Mono<FormTemplate> cloneTemplate(String sourceId, String newName) {
        return findById(sourceId)  // Use generic method
                .flatMap(sourceTemplate -> {
                    // Create new template instance
                    FormTemplate newTemplate = new FormTemplate();
                    newTemplate.setName(newName);
                    newTemplate.setDescription(sourceTemplate.getDescription() + " (Clone)");
                    newTemplate.setCategory(sourceTemplate.getCategory());
                    newTemplate.setThumbnail(sourceTemplate.getThumbnail());
                    newTemplate.setFormDefinition(sourceTemplate.getFormDefinition());
                    newTemplate.setUsageCount(0);
                    newTemplate.setSystem(false);
                    newTemplate.setCreatedBy(sourceTemplate.getCreatedBy());
                    newTemplate.setCreatedAt(LocalDateTime.now());
                    newTemplate.setUpdatedAt(LocalDateTime.now());

                    // Use generic create method to handle caching and events
                    return create(newTemplate);
                });
    }

}
