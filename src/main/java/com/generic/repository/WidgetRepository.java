package com.generic.repository;

import com.generic.enumration.WidgetCategory;
import com.generic.model.Widget;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface WidgetRepository extends ReactiveMongoRepository<Widget, String> {

    Flux<Widget> findByCategory(WidgetCategory category);

    Flux<Widget> findByIsSystem(boolean isSystem);

    Mono<Widget> findByType(String type);
}