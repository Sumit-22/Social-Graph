package com.social.user.controller;

import com.social.user.entity.User;
import com.social.user.repository.UserRepository;
import com.social.user.service.FollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserRepository userRepository;
  private final FollowService followService;

  public UserController(UserRepository userRepository, FollowService followService) {
    this.userRepository = userRepository;
    this.followService = followService;
  }

  @PostMapping("/create")
  public ResponseEntity<User> create(@RequestBody User u) {
    if (u.getUsername() == null || u.getUsername().isBlank()) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(userRepository.save(u));
  }

  @PostMapping("/{from}/follow/{to}")
  public ResponseEntity<String> follow(@PathVariable String from, @PathVariable String to) {
    followService.follow(from, to);
    return ResponseEntity.ok("followed");
  }

  @GetMapping("/{username}/followers")
  public ResponseEntity<List<String>> followers(@PathVariable String username) {
    return ResponseEntity.ok(followService.followersOf(username));
  }

  @GetMapping("/{username}/following")
  public ResponseEntity<List<String>> following(@PathVariable String username) {
    return ResponseEntity.ok(followService.followingOf(username));
  }
}
