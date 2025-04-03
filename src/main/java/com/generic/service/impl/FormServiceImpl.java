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

import java.time.LocalDate;
import java.time.LocalDateTime;
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
                    form.setCreatedAt(LocalDateTime.now());
                    form.setUpdatedAt(LocalDateTime.now());

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
    public Flux<Forms> getFormsByStatus(FormStatus status) {
        return formRepository.findByStatus(status);
    }

    @Override
    public Flux<Forms> getFormsByUser(String userId) {
        return formRepository.findByCreatedBy(userId);
    }

    @Override
    public Mono<Forms> updateForm(String id, Forms formUpdates) {
        return getFormById(id)
                .flatMap(existingForm -> updateEntityFields(existingForm, formUpdates));
    }

    @Override
    public Mono<Forms> addElement(String formId, FormElement element) {
        return findById(formId)
                .map(form -> {
                    form.addElement(element);
                    form.setUpdatedAt(LocalDateTime.now());
                    return form;
                })
                .flatMap(this::create);  // Reuses create which handles caching and events
    }

    @Override
    public Mono<Forms> removeElement(String formId, String elementId) {
        return findById(formId)
                .map(form -> {
                    form.removeElement(elementId);
                    form.setUpdatedAt(LocalDateTime.now());
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
        if (updatedForm.getTitle() != null) {
            existingForm.setTitle(updatedForm.getTitle());
        }
        if (updatedForm.getDescription() != null) {
            existingForm.setDescription(updatedForm.getDescription());
        }
        if (updatedForm.getStatus() != null) {
            existingForm.setStatus(updatedForm.getStatus());
        }
        if (updatedForm.getFormType() != null) {
            existingForm.setFormType(updatedForm.getFormType());
        }
        if (updatedForm.getTheme() != null) {
            existingForm.setTheme(updatedForm.getTheme());
        }
        if (updatedForm.getSettings() != null && !updatedForm.getSettings().isEmpty()) {
            existingForm.getSettings().putAll(updatedForm.getSettings());
        }

        existingForm.setUpdatedAt(LocalDateTime.now());

        return Mono.just(existingForm);
    }
}
