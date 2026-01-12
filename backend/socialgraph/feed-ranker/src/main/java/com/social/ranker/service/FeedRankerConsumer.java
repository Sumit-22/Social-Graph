package com.social.ranker.service;

import com.social.ranker.kafka.PostEvent;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
// FIX: Added the correct import from spring-retry
import org.springframework.retry.annotation.Backoff;

import java.util.List;

@Service
public class FeedRankerConsumer {

    private static final Logger logger = LoggerFactory.getLogger(FeedRankerConsumer.class);
    private final StringRedisTemplate redis;
    private final RestTemplate rest;

    @Autowired
    @Lazy
    private FeedRankerConsumer self;

    public FeedRankerConsumer(StringRedisTemplate redis, RestTemplate rest) {
        this.redis = redis;
        this.rest = rest;
    }

    // FIX: Used the correct @Backoff annotation (imported from spring-retry)
    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = "post-created", groupId = "feed-ranker-group")
    public void onMessage(PostEvent evt, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            logger.info("Processing post event: {} from topic: {}", evt.getPostId(), topic);
            self.processPostEvent(evt);
        } catch (Exception e) {
            logger.error("Error processing post event: {}", evt.getPostId(), e);
            throw new RuntimeException("Failed to process post event", e);
        }
    }

    @CircuitBreaker(name = "user-service", fallbackMethod = "fallbackGetFollowers")
    @Retry(name = "user-service")
    public void processPostEvent(PostEvent evt) {
        List<?> followers = fetchFollowers(evt.getAuthorId());

        if (followers == null || followers.isEmpty()) {
            logger.warn("No followers found for author: {}", evt.getAuthorId());
            return;
        }

        double base = computeRecencyScore(evt.getTimestamp());
        for (Object obj : followers) {
            try {
                String follower = String.valueOf(obj);
                double affinity = 0.5;
                double score = 0.6 * affinity + 0.4 * base;
                String key = "feed:" + follower;

                redis.opsForZSet().add(key, String.valueOf(evt.getPostId()), score);
                redis.opsForZSet().removeRange(key, 0, -101);

                logger.debug("Added post {} to feed of user {}", evt.getPostId(), follower);
            } catch (Exception e) {
                logger.error("Error adding post to follower feed", e);
            }
        }
    }

    private List<?> fetchFollowers(String authorId) {
        String followersUrl = "http://localhost:8080/users/" + authorId + "/followers";
        try {
            logger.info("Fetching followers for author: {}", authorId);
            return rest.getForObject(followersUrl, List.class);
        } catch (RestClientException e) {
            logger.error("Failed to fetch followers for author: {}", authorId, e);
            throw e;
        }
    }

    public void fallbackGetFollowers(PostEvent evt, Exception e) {
        logger.warn("Circuit breaker fallback for author: {}", evt.getAuthorId());
    }

    private double computeRecencyScore(long ts) {
        long now = System.currentTimeMillis();
        double day = 1000.0 * 60 * 60 * 24;
        return Math.max(0.0, 1.0 - ((now - ts) / day));
    }
}