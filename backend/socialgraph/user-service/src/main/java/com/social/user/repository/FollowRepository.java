package com.social.user.repository;

import com.social.user.graph.FollowNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends Neo4jRepository<FollowNode, Long> {

  @Query("MERGE (u:UserNode {username: $from}) " +
         "MERGE (v:UserNode {username: $to}) " +
         "MERGE (u)-[:FOLLOWS]->(v) " +
         "RETURN u")
  FollowNode createFollow(String from, String to);

  @Query("MATCH (u:UserNode {username: $username})<-[:FOLLOWS]-(f:UserNode) RETURN f.username")
  List<String> findFollowers(String username);

  @Query("MATCH (u:UserNode {username: $username})-[:FOLLOWS]->(v:UserNode) RETURN v.username")
  List<String> findFollowing(String username);

  @Query("MATCH (u:UserNode {username: $username}) RETURN u LIMIT 1")
  Optional<FollowNode> findNodeByUsername(String username);
}
