package com.generic.controller;

import com.generic.model.FormAnalytics;
import com.generic.service.FormAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class FormAnalyticsController {
    private final FormAnalyticsService analyticsService;

    @GetMapping("/form/{formId}")
    public Mono<FormAnalytics> getFormAnalytics(@PathVariable("formId") String formId) {
        return analyticsService.findByFormId(formId);
    }

    @PostMapping("/form/{formId}/view")
    public Mono<FormAnalytics> incrementViewCount(@PathVariable("formId") String formId) {
        return analyticsService.incrementViewCount(formId);
    }
}
