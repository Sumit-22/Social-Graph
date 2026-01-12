package com.social.ingestor.model;

import jakarta.persistence.*;

@Entity
@Table(name = "posts")
public class Post {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String authorId;

  @Column(columnDefinition = "text")
  private String content;

  @Column(nullable = false)
  private long timestamp;

  public Long getId() { return id; }
  public String getAuthorId() { return authorId; }
  public String getContent() { return content; }
  public long getTimestamp() { return timestamp; }
  public void setAuthorId(String authorId) { this.authorId = authorId; }
  public void setContent(String content) { this.content = content; }
  public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
