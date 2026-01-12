# SocialGraphAI Backend

This folder contains a complete Spring Boot scaffold for a real-time social feed ranking engine:
- user-service (Postgres + Neo4j)
- feed-ingestor (Kafka producer + Postgres)
- feed-ranker (Kafka consumer + Redis)
- feed-api (Redis reader + post details via feed-ingestor)

## Quickstart

1) Start infra:
- Install Docker and run: `docker compose -f backend/socialgraphai/docker-compose.yml up -d`

2) Start services (ports):
- user-service: 8080
- feed-ingestor: 8090
- feed-ranker: 8100
- feed-api: 8081

3) Test flow:
- Create users
- Follow: `POST /users/{from}/follow/{to}`
- Create post: `POST /post/create`
- Get feed: `GET /feed?userId={username}`

See endpoints on the Next.js homepage (app/page.tsx).
