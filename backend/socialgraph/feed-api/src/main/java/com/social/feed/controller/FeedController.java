package com.social.feed.controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/feed")
public class FeedController {

  private final StringRedisTemplate redis;
  private final RestTemplate rest;

  public FeedController(StringRedisTemplate redis, RestTemplate rest) {
    this.redis = redis;
    this.rest = rest;
  }

  @GetMapping
  public ResponseEntity<List<Map<String, Object>>> getFeed(
      @RequestParam String userId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit
  ) {
    String key = "feed:" + userId;
    int start = Math.max(0, (page - 1) * limit);
    int end = start + limit - 1;

    Set<String> ids = redis.opsForZSet().reverseRange(key, start, end);
    if (ids == null || ids.isEmpty()) {
      return ResponseEntity.ok(Collections.emptyList());
    }

    List<Map<String, Object>> out = new ArrayList<>();
    for (String id : ids) {
      try {
        Map<?, ?> post = rest.getForObject("http://localhost:8090/post/" + id, Map.class);
        if (post != null) {
          @SuppressWarnings("unchecked")
          Map<String, Object> asMap = new LinkedHashMap<>((Map<String, Object>) post);
          out.add(asMap);
        } else {
          out.add(Map.of("postId", id));
        }
      } catch (Exception ex) {
        out.add(Map.of("postId", id));
      }
    }
    return ResponseEntity.ok(out);
  }
}
