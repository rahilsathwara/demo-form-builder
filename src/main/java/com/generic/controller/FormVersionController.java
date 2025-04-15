package com.generic.controller;

import com.generic.model.FormVersion;
import com.generic.service.FormVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/form-versions")
@RequiredArgsConstructor
public class FormVersionController {

    private final FormVersionService versionService;

    @GetMapping("/{versionId}")
    public Mono<FormVersion> getVersionByVersionId(@PathVariable("versionId") String versionId) {
        return versionService.getVersionById(versionId);
    }

    @GetMapping("/form/{formId}")
    public Flux<FormVersion> getAllVersionByFormId(@PathVariable("formId") String formId) {
        return versionService.getFormVersionByFormId(formId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<FormVersion> createFormVersion(@RequestBody FormVersion versionRequest) {
        return versionService.createVersion(versionRequest);
    }
}
