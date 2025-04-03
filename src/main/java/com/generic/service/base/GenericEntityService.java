package com.generic.service.base;

import com.generic.service.cache.RedisCacheService;
import com.generic.service.event.KafkaEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
public abstract class GenericEntityService<T, ID> {

    protected final ReactiveCrudRepository<T, ID> repository;
    protected final RedisCacheService<T, ID> cacheService;
    protected final KafkaEventService<ID, T> eventService;
    protected final Function<T, ID> idExtractor;

    protected GenericEntityService(ReactiveCrudRepository<T, ID> repository, RedisCacheService<T, ID> cacheService, KafkaEventService<ID, T> eventService, Function<T, ID> idExtractor) {
        this.repository = repository;
        this.cacheService = cacheService;
        this.eventService = eventService;
        this.idExtractor = idExtractor;
    }

    /**
     * Find entity by ID with caching
     */
    public Mono<T> findById(ID id) {
        return cacheService.get(id)
                .switchIfEmpty(
                        repository.findById(id)
                                .flatMap(entity -> cacheService.cache(id, entity))
                );
    }

    /**
     * Find all entities (no caching for collections)
     */
    public Flux<T> findAll() {
        return repository.findAll();
    }

    /**
     * Create entity with caching and event publishing
     */
    public Mono<T> create(T entity) {
        return repository.save(entity)
                .flatMap(savedEntity -> {
                    ID id = idExtractor.apply(savedEntity);
                    return cacheService.cache(id, savedEntity)
                            .flatMap(cached -> eventService.publishEvent(id, cached)
                                    .thenReturn(cached));
                });
    }

    /**
     * Update entity with cache invalidation and event publishing
     */
    public Mono<T> update(ID id, T entity) {
        return repository.findById(id)
                .flatMap(existingEntity -> updateEntityFields(existingEntity, entity))
                .flatMap(repository::save)
                .flatMap(updatedEntity -> cacheService.cache(id, updatedEntity)
                        .flatMap(cached -> eventService.publishEvent(id, updatedEntity)
                                .thenReturn(cached)));
    }

    /**
     * Delete entity with cache invalidation and event publishing
     */
    public Mono<Void> delete(ID id) {
        return repository.findById(id)
                .flatMap(entity -> cacheService.delete(id)
                        .then(repository.deleteById(id))
                        .then(eventService.publishEvent(id, entity)
                                .then()));
    }

    /**
     * Abstract method for updating entity fields - to be implemented by subclasses
     */
    protected abstract Mono<T> updateEntityFields(T existingEntity, T updatedEntity);
}
