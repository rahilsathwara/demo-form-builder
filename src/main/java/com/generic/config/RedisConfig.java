package com.generic.config;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Bean
    public <T> ReactiveRedisTemplate<String, T> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory,
            Class<T> entityClass) {

        Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(entityClass);

        RedisSerializationContext.RedisSerializationContextBuilder<String, T> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, T> context = builder
                .value(serializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    // Redis health check to properly detect
    @Bean
    public HealthIndicator redisHealthIndicator(ReactiveRedisConnectionFactory connectionFactory) {
        return new AbstractHealthIndicator() {
            @Override
            protected void doHealthCheck(Health.Builder builder) throws Exception {
                try {
                    // This will connect to Redis and check if it's responding
                    connectionFactory.getReactiveConnection().ping().block(Duration.ofSeconds(5));
                    builder.up();
                } catch (Exception e) {
                    builder.down(e);
                }
            }
        };
    }
}
