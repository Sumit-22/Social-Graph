package com.social.user.config;

import org.neo4j.driver.Driver;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jAuditing;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableNeo4jRepositories(basePackages = "com.social.user.repository")
@EntityScan(basePackages = "com.social.user.graph")
@EnableTransactionManagement
@EnableNeo4jAuditing
public class Neo4jConfig {

  @Bean
  public Driver neo4jDriver(Driver driver) {
    return driver;
  }
}
