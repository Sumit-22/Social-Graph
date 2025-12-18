package com.social.ingestor.controller;

import com.social.ingestor.kafka.PostEvent;
import com.social.ingestor.model.Post;
import com.social.ingestor.repo.PostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class PostController {

  private final PostRepository postRepository;
  private final KafkaTemplate<String, PostEvent> kafkaTemplate;

  public PostController(PostRepository postRepository, KafkaTemplate<String, PostEvent> kafkaTemplate) {
    this.postRepository = postRepository;
    this.kafkaTemplate = kafkaTemplate;
  }

  @PostMapping("/create")
  public ResponseEntity<Post> create(@RequestBody Post p) {
    if (p.getAuthorId() == null || p.getAuthorId().isBlank()) {
      return ResponseEntity.badRequest().build();
    }
    p.setTimestamp(System.currentTimeMillis());
    Post saved = postRepository.save(p);
    PostEvent evt = new PostEvent(saved.getId(), saved.getAuthorId(), saved.getContent(), saved.getTimestamp());
    kafkaTemplate.send("post-created", evt);
    return ResponseEntity.ok(saved);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Post> getById(@PathVariable Long id) {
    return postRepository.findById(id).map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}
