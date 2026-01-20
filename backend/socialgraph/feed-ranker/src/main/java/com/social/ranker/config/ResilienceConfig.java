package com.social.ranker.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    private static final Logger logger = LoggerFactory.getLogger(ResilienceConfig.class);

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50.0f)
                .slowCallRateThreshold(50.0f)
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .permittedNumberOfCallsInHalfOpenState(3)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        registry.getEventPublisher()
                .onEntryAdded(event -> logger.info("CircuitBreaker added: {}", event.getAddedEntry().getName()))
                .onEntryRemoved(event -> logger.info("CircuitBreaker removed: {}", event.getRemovedEntry().getName()));

        return registry;
    }

@Bean
public RetryRegistry retryRegistry() {
    RetryConfig config = RetryConfig.custom()
            .maxAttempts(3)
            .intervalFunction(
                io.github.resilience4j.core.IntervalFunction
                    .ofExponentialBackoff(500, 2.0)
            )
            .build();

    RetryRegistry registry = RetryRegistry.of(config);
    registry.getEventPublisher()
            .onEntryAdded(e -> logger.info("Retry added: {}", e.getAddedEntry().getName()))
            .onEntryRemoved(e -> logger.info("Retry removed: {}", e.getRemovedEntry().getName()));

    return registry;
}

    @Bean
    public CircuitBreaker userServiceCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("user-service");
    }

    @Bean
    public Retry userServiceRetry(RetryRegistry registry) {
        return registry.retry("user-service");
    }
}
