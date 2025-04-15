package com.generic.service.impl;

import com.generic.model.FormVersion;
import com.generic.repository.FormVersionRepository;
import com.generic.service.FormVersionService;
import com.generic.service.base.GenericEntityService;
import com.generic.service.cache.RedisCacheService;
import com.generic.service.event.KafkaEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class FormVersionServiceImpl extends GenericEntityService<FormVersion, String> implements FormVersionService {

    private final FormVersionRepository formVersionRepository;

    protected FormVersionServiceImpl(
            FormVersionRepository formVersionRepository,
            RedisCacheService<FormVersion, String> cacheService,
            KafkaEventService<String, FormVersion> eventService) {

        super(formVersionRepository, cacheService, eventService, FormVersion::getId);
        this.formVersionRepository = formVersionRepository;
    }

    @Override
    protected Mono<FormVersion> updateEntityFields(FormVersion existingVersion, FormVersion versionUpdates) {
        //todo remaining update version logic

        return Mono.just(existingVersion);
    }

    @Override
    public Flux<FormVersion> getFormVersionByFormId(String formId) {
        return formVersionRepository.findByFormId(formId);
    }

    @Override
    public Mono<FormVersion> getVersionById(String versionId) {
        return findById(versionId);
    }

    @Override
    public Mono<FormVersion> createVersion(FormVersion versionRequest) {
        return create(versionRequest);
    }
}
