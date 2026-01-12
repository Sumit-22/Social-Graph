package com.social.ranker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class FeedRankerApplication {
  public static void main(String[] args) {
    SpringApplication.run(FeedRankerApplication.class, args);
  }

  @Bean
  public RestTemplate restTemplate() { return new RestTemplate(); }
}
