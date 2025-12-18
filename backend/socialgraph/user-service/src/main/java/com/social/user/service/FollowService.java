package com.social.user.service;

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
  public void follow(String from, String to) {
    followRepository.createFollow(from, to);
  }

  public List<String> followersOf(String username) {
    return followRepository.findFollowers(username);
  }

  public List<String> followingOf(String username) {
    return followRepository.findFollowing(username);
  }
}
