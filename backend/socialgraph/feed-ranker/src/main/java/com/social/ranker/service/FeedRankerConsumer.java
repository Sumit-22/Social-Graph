package com.social.ranker.service;

import com.social.ranker.kafka.PostEvent;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FeedRankerConsumer {

  private final StringRedisTemplate redis;
  private final RestTemplate rest;

  public FeedRankerConsumer(StringRedisTemplate redis, RestTemplate rest) {
    this.redis = redis;
    this.rest = rest;
  }

  @KafkaListener(topics = "post-created", groupId = "feed-ranker-group")
  public void onMessage(PostEvent evt) {
    // Fetch followers of author
    String followersUrl = "http://localhost:8080/users/" + evt.getAuthorId() + "/followers";
    List<?> followers = rest.getForObject(followersUrl, List.class);
    if (followers == null) return;

    double base = computeRecencyScore(evt.getTimestamp());
    for (Object obj : followers) {
      String follower = String.valueOf(obj);
      double affinity = 0.5; // TODO: replace with real affinity measure
      double score = 0.6 * affinity + 0.4 * base;
      String key = "feed:" + follower;
      redis.opsForZSet().add(key, String.valueOf(evt.getPostId()), score);
      // Keep only top 100 items
      redis.opsForZSet().removeRange(key, 0, -101);
    }
  }

  private double computeRecencyScore(long ts) {
    long now = System.currentTimeMillis();
    double day = 1000.0 * 60 * 60 * 24;
    double decay = Math.max(0.0, 1.0 - ((now - ts) / day));
    return decay;
    }
}
