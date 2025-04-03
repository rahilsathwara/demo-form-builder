package com.generic.service.impl;

import com.generic.enumration.SubmissionStatus;
import com.generic.model.FormSubmission;
import com.generic.repository.FormSubmissionRepository;
import com.generic.service.FormSubmissionService;
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
public class FormSubmissionImpl extends GenericEntityService<FormSubmission, String> implements FormSubmissionService {

    private final FormSubmissionRepository submissionRepository;

    public FormSubmissionImpl(
            FormSubmissionRepository submissionRepository,
            RedisCacheService<FormSubmission, String> cacheService,
            KafkaEventService<String, FormSubmission> eventService) {

        super(submissionRepository, cacheService, eventService, FormSubmission::getId);
        this.submissionRepository = submissionRepository;
    }

    @Override
    protected Mono<FormSubmission> updateEntityFields(FormSubmission existingSubmission, FormSubmission updatedSubmission) {
        // In most applications, we would not allow updating form submissions after creation
        // But we could allow status updates or metadata changes

        if (updatedSubmission.getStatus() != null) {
            existingSubmission.setStatus(updatedSubmission.getStatus());
        }

        if (updatedSubmission.getMetadata() != null) {
            existingSubmission.setMetadata(updatedSubmission.getMetadata());
        }

        return Mono.just(existingSubmission);
    }

    @Override
    public Mono<FormSubmission> submitForm(FormSubmission submission) {
        // Set default values if not provided
        if (submission.getSubmittedAt() == null) {
            submission.setSubmittedAt(LocalDateTime.now());
        }

        if (submission.getStatus() == null) {
            submission.setStatus(SubmissionStatus.COMPLETED);
        }

        // Use generic create method to handle caching and events
        return create(submission);
    }

    @Override
    public Flux<FormSubmission> getSubmissionsForForm(String formId) {
        return submissionRepository.findByFormId(formId);
    }

    @Override
    public Flux<FormSubmission> getSubmissionsForFormInDateRange(String formId, LocalDateTime start, LocalDateTime end) {
        return submissionRepository.findByFormIdAndSubmittedAtBetween(formId, start, end);
    }

    @Override
    public Mono<Long> getSubmissionCount(String formId) {
        return submissionRepository.countByFormId(formId);
    }

    @Override
    public Mono<FormSubmission> getSubmission(String submissionId) {
        return findById(submissionId);
    }


    /**
     * Mark a submission as rejected with a reason
     */
    @Override
    public Mono<FormSubmission> rejectSubmission(String submissionId, String rejectionReason) {
        return findById(submissionId)  // Use generic method
                .flatMap(submission -> {
                    submission.setStatus(SubmissionStatus.REJECTED);

                    // Add rejection reason to metadata
                    if (submission.getMetadata() != null) {
                        //submission.getMetadata().put("rejectionReason", rejectionReason);
                    }

                    // Use generic update which handles caching and events
                    return this.create(submission);
                });
    }

    /**
            * Get latest submissions across all forms (example of a more complex query)
     */
    @Override
    public Flux<FormSubmission> getLatestSubmissions(int limit) {
        // This uses a custom repository method that would need to be implemented
        // For example using a MongoDB aggregation
        return submissionRepository.findAll()
                .sort((s1, s2) -> s2.getSubmittedAt().compareTo(s1.getSubmittedAt()))
                .take(limit);
    }

    /**
     * Search submissions by text (example of full-text search)
     */
    @Override
    public Flux<FormSubmission> searchSubmissions(String searchText) {
        // This would typically use a text index in MongoDB
        // Here's a simple implementation that just checks if the JSON representation contains the text
        return submissionRepository.findAll()
                .filter(submission -> {
                    // Check if any data field contains the search text
                    if (submission.getData() != null) {
                        return submission.getData().values().stream()
                                .anyMatch(value -> value != null &&
                                        value.toString().toLowerCase().contains(searchText.toLowerCase()));
                    }
                    return false;
                });
    }
}
