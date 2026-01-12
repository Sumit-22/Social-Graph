package com.social.user.service;

import com.social.user.graph.FollowNode;
import com.social.user.repository.FollowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FollowService {
  private final FollowRepository followRepository;

  public FollowService(FollowRepository followRepository) {
    this.followRepository = followRepository;
  }

  @Transactional
  public FollowNode follow(String from, String to) {
    if (from == null || from.isBlank() || to == null || to.isBlank()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    if (from.equals(to)) {
      throw new IllegalArgumentException("Cannot follow yourself");
    }
    try {
      return followRepository.createFollow(from, to);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create follow relationship: " + e.getMessage(), e);
    }
  }

  public List<String> followersOf(String username) {
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    return followRepository.findFollowers(username);
  }

  public List<String> followingOf(String username) {
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    return followRepository.findFollowing(username);
  }
}
