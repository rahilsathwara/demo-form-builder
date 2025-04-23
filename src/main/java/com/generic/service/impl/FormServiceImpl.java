package com.generic.service.impl;

import com.generic.enumration.FormStatus;
import com.generic.exception.DuplicateFormException;
import com.generic.model.FormElement;
import com.generic.model.Forms;
import com.generic.payload.request.FormRequest;
import com.generic.repository.FormRepository;
import com.generic.repository.FormSubmissionRepository;
import com.generic.service.FormService;
import com.generic.service.base.GenericEntityService;
import com.generic.service.cache.RedisCacheService;
import com.generic.service.event.KafkaEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FormServiceImpl extends GenericEntityService<Forms, String> implements FormService {

    private final FormRepository formRepository;

    public FormServiceImpl(
            FormRepository formRepository,
            RedisCacheService<Forms, String> formCacheService,
            KafkaEventService<String, Forms> formEventService) {
        super(formRepository, formCacheService, formEventService, Forms::getId);
        this.formRepository = formRepository;
    }


    @Override
    public Mono<Forms> createForm(Forms form) {
        return formRepository.findByTitle(form.getTitle())
                .flatMap(existingForm -> {
                    // Return error if form with this title already exists
                    return Mono.error(new DuplicateFormException("A form with title '" + form.getTitle() + "' already exists"));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    // No duplicate found, proceed with creation
                    form.setCreatedAt(Instant.now());
                    form.setUpdatedAt(Instant.now());

                    if (form.getStatus() == null) {
                        form.setStatus(FormStatus.DRAFT);
                    }

                    return super.create(form);
                })).cast(Forms.class);
    }

    @Override
    public Flux<Forms> getAllForms() {
        return findAll();
    }

    @Override
    public Mono<Forms> getFormById(String formId) {
        return formRepository.findById(formId);
    }

    @Override
    public Mono<Forms> getPublishedForm(String id) {
        return formRepository.findByIdAndStatus(id,FormStatus.PUBLISHED);
    }

    @Override
    public Flux<Forms> getFormsByStatus(FormStatus status) {
        return formRepository.findByStatus(status);
    }

    @Override
    public Flux<Forms> getFormsByUser(String userId) {
        return formRepository.findByCreatedBy(userId);
    }

    @Override
    public Mono<Forms> updateForm(String id, Forms formUpdates) {
        return update(id, formUpdates);
    }

    @Override
    public Mono<Forms> changeFormStatus(String id, FormStatus status) {
        return formRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Form not found with id: " + id)))
                .flatMap(existingForm -> {
                    existingForm.setStatus(status);
                    existingForm.setUpdatedAt(Instant.now());

                    return create(existingForm);
                });
    }

    @Override
    public Mono<Forms> addElement(String formId, FormElement element) {
        return findById(formId)
                .map(form -> {
                    form.addElement(element);
                    form.setUpdatedAt(Instant.now());
                    return form;
                })
                .flatMap(this::create);  // Reuses create which handles caching and events
    }

    @Override
    public Mono<Forms> removeElement(String formId, String elementId) {
        return findById(formId)
                .map(form -> {
                    form.removeElement(elementId);
                    form.setUpdatedAt(Instant.now());
                    return form;
                })
                .flatMap(this::create);  // Reuses create which handles caching and events
    }

    @Override
    public Mono<Void> deleteForm(String id) {
        return delete(id);
    }

    @Override
    protected Mono<Forms> updateEntityFields(Forms existingForm, Forms updatedForm) {
        try {
            if (updatedForm == null) {
                return Mono.error(new IllegalArgumentException("Updated form must not be null"));
            }

            Optional.ofNullable(updatedForm.getTitle())
                    .filter(title -> !title.trim().isEmpty())
                    .ifPresent(existingForm::setTitle);

            Optional.ofNullable(updatedForm.getDescription())
                    .ifPresent(existingForm::setDescription);

            Optional.ofNullable(updatedForm.getStatus())
                    .ifPresent(existingForm::setStatus);

            Optional.ofNullable(updatedForm.getFormType())
                    .ifPresent(existingForm::setFormType);

            Optional.ofNullable(updatedForm.getTheme())
                    .ifPresent(existingForm::setTheme);

            if (updatedForm.getSettings() != null && !updatedForm.getSettings().isEmpty()) {
                existingForm.getSettings().putAll(updatedForm.getSettings());
            } else {
                existingForm.setSettings(new java.util.HashMap<>());
            }

            // Replace or merge elements
            if (updatedForm.getElements() != null && !updatedForm.getElements().isEmpty()) {
                existingForm.setElements(new ArrayList<>(updatedForm.getElements()));
                existingForm.updateElementOrder();
            } else {
                existingForm.setElements(new ArrayList<>());
            }

            // Replace or merge sections
            if (updatedForm.getSections() != null && !updatedForm.getSections().isEmpty()) {
                existingForm.setSections(new ArrayList<>(updatedForm.getSections()));
            } else {
                existingForm.setSections(new ArrayList<>());
            }

            // Replace or merge scripts
            if (updatedForm.getScripts() != null && !updatedForm.getScripts().isEmpty()) {
                existingForm.setScripts(new ArrayList<>(updatedForm.getScripts()));
            } else {
                existingForm.setScripts(new ArrayList<>());
            }

            Optional.ofNullable(updatedForm.getTemplateId())
                    .ifPresent(existingForm::setTemplateId);

            existingForm.setTemplate(updatedForm.isTemplate());

            existingForm.setUpdatedAt(Instant.now());

            return Mono.just(existingForm);

        } catch (Exception ex) {
            log.error("Failed to update form fields: {}", ex.getMessage(), ex);
            return Mono.error(new RuntimeException("Failed to update form fields", ex));
        }
    }
}
