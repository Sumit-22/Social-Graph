package com.social.user.graph;

import org.springframework.data.neo4j.core.schema.*;

import java.util.HashSet;
import java.util.Set;

@Node("UserNode")
public class FollowNode {
  @Id @GeneratedValue
  private Long id;

  @Property("username")
  private String username;

  @Relationship(type = "FOLLOWS")
  private Set<FollowNode> follows = new HashSet<>();

  public FollowNode() {}
  public FollowNode(String username) { this.username = username; }

  public Long getId() { return id; }
  public String getUsername() { return username; }
  public Set<FollowNode> getFollows() { return follows; }
  public void setUsername(String username) { this.username = username; }
}
