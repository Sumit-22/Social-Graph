package com.social.ranker.kafka;

public class PostEvent {
  private Long postId;
  private String authorId;
  private String content;
  private long timestamp;

  public Long getPostId() { return postId; }
  public String getAuthorId() { return authorId; }
  public String getContent() { return content; }
  public long getTimestamp() { return timestamp; }
  public void setPostId(Long postId) { this.postId = postId; }
  public void setAuthorId(String authorId) { this.authorId = authorId; }
  public void setContent(String content) { this.content = content; }
  public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
