package com.social.ingestor.kafka;

public class PostEvent {
  private Long postId;
  private String authorId;
  private String content;
  private long timestamp;

  public PostEvent() {}
  public PostEvent(Long postId, String authorId, String content, long timestamp) {
    this.postId = postId; this.authorId = authorId; this.content = content; this.timestamp = timestamp;
  }
  public Long getPostId() { return postId; }
  public String getAuthorId() { return authorId; }
  public String getContent() { return content; }
  public long getTimestamp() { return timestamp; }
}
