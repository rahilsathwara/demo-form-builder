package com.generic.service;

import com.generic.model.FormVersion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FormVersionService {
    Flux<FormVersion> getFormVersionByFormId(String formId);

    Mono<FormVersion> getVersionById(String versionId);

    Mono<FormVersion> createVersion(FormVersion versionRequest);
}