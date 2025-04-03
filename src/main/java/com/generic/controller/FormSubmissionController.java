package com.generic.controller;

import com.generic.model.FormSubmission;
import com.generic.service.FormSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/submissions")
public class FormSubmissionController {

    @Autowired private FormSubmissionService submissionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FormSubmission> submitForm(@RequestBody FormSubmission submission) {
        return submissionService.submitForm(submission);
    }

    @GetMapping("/{submissionId}")
    public Mono<FormSubmission> getSubmission(@PathVariable("submissionId") String submissionId) {
        return submissionService.getSubmission(submissionId);
    }

    @GetMapping("/form/{formId}")
    public Flux<FormSubmission> getSubmissionsForForm(@PathVariable("formId") String formId) {
        return submissionService.getSubmissionsForForm(formId);
    }

    @GetMapping("/form/{formId}/count")
    public Mono<Long> getSubmissionCount(@PathVariable("formId") String formId) {
        return submissionService.getSubmissionCount(formId);
    }

    @GetMapping("/form/{formId}/date-range")
    public Flux<FormSubmission> getSubmissionsInDateRange(
            @PathVariable("formId") String formId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        return submissionService.getSubmissionsForFormInDateRange(formId, start, end);
    }

    @PutMapping("/{id}/reject")
    public Mono<FormSubmission> rejectSubmission(
            @PathVariable String id,
            @RequestParam String reason) {
        return submissionService.rejectSubmission(id, reason);
    }

    @GetMapping("/latest")
    public Flux<FormSubmission> getLatestSubmissions(
            @RequestParam(defaultValue = "10") int limit) {
        return submissionService.getLatestSubmissions(limit);
    }

    @GetMapping("/search")
    public Flux<FormSubmission> searchSubmissions(
            @RequestParam String query) {
        return submissionService.searchSubmissions(query);
    }
}
