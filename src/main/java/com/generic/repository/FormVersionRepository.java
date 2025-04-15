package com.generic.repository;

import com.generic.model.FormVersion;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface FormVersionRepository extends ReactiveMongoRepository<FormVersion, String> {
    Flux<FormVersion> findByFormId(String formId);
}
