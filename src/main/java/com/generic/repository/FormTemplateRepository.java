package com.generic.repository;

import com.generic.enumration.TemplateCategory;
import com.generic.model.FormTemplate;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface FormTemplateRepository extends ReactiveMongoRepository<FormTemplate, String> {

    Flux<FormTemplate> findByCategory(TemplateCategory category);

    Flux<FormTemplate> findByIsSystem(boolean isSystem);
}
