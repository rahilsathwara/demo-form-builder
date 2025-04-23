package com.generic.repository;

import com.generic.enumration.FormStatus;
import com.generic.model.Forms;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FormRepository extends ReactiveMongoRepository<Forms, String> {

    Flux<Forms> findByCreatedBy(String createdBy);
    Mono<Forms> findByTitle(String title);
    Mono<Forms> findByIdAndStatus(String id, FormStatus formStatus);
    Mono<Long> countByStatus(FormStatus status);

    Flux<Forms> findByStatus(FormStatus status);


    @Query("{'isTemplate': true}")
    Flux<Forms> findAllTemplates();

    @Query("{'elements.type': ?0}")
    Flux<Forms> findByElementType(String elementType);
}
