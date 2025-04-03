package com.generic.service.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
public class RedisCacheService<T, ID> {

    private final ReactiveRedisTemplate<String, T> redisTemplate;
    private final String keyPrefix;
    private final Duration defaultTtl;

    /**
     * Cache an entity
     */
    public Mono<T> cache(ID id, T entity) {
        String key = createKey(id);
        return redisTemplate.opsForValue()
                .set(key, entity, defaultTtl)
                .thenReturn(entity)
                .doOnSuccess(e -> log.debug("Cached entity with key: {}", key))
                .onErrorResume(error -> {
                    log.warn("Redis caching failed for key: {}. Error: {}", key, error.getMessage());
                    // Return the entity anyway so application continues to work
                    return Mono.just(entity);
                });
    }

    /**
     * Cache an entity with custom TTL
     */
    public Mono<T> cache(ID id, T entity, Duration ttl) {
        String key = createKey(id);
        return redisTemplate.opsForValue()
                .set(key, entity, ttl)
                .thenReturn(entity)
                .doOnSuccess(e -> log.debug("Cached entity with key: {} and TTL: {}", key, ttl))
                .doOnError(error -> log.error("Failed to cache entity with key: {}", key, error));
    }

    /**
     * Get an entity from cache
     */
    public Mono<T> get(ID id) {
        String key = createKey(id);
        return redisTemplate.opsForValue().get(key)
                .doOnSuccess(entity -> {
                    if (entity != null) {
                        log.debug("Cache hit for key: {}", key);
                    } else {
                        log.debug("Cache miss for key: {}", key);
                    }
                }).onErrorResume(error -> {
                    log.warn("Redis retrieval failed for key: {}. Error: {}", key, error.getMessage());
                    // Return empty on error to fall back to database
                    return Mono.empty();
                });
    }

    /**
     * Delete an entity from cache
     */
    public Mono<Boolean> delete(ID id) {
        String key = createKey(id);
        return redisTemplate.opsForValue().delete(key)
                .doOnSuccess(result -> log.debug("Deleted from cache for key: {}", key))
                .onErrorResume(error -> {
                    log.warn("Redis delete failed for key: {}. Error: {}", key, error.getMessage());
                    // Return true so the calling code doesn't fail
                    return Mono.just(Boolean.TRUE);
                });
    }

    /**
     * Create cache key
     */
    protected String createKey(ID id) {
        return keyPrefix + ":" + id.toString();
    }
}
