package com.generic.service;

import com.generic.model.FormSubmission;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface FormSubmissionService {

    Mono<FormSubmission> submitForm(FormSubmission submission);
    Flux<FormSubmission> getSubmissionsForForm(String formId);
    Flux<FormSubmission> getSubmissionsForFormInDateRange(String formId, LocalDateTime start, LocalDateTime end);
    Mono<Long> getSubmissionCount(String formId);
    Mono<FormSubmission> getSubmission(String submissionId);

    Mono<FormSubmission> rejectSubmission(String id, String reason);

    Flux<FormSubmission> getLatestSubmissions(int limit);

    Flux<FormSubmission> searchSubmissions(String query);
}
