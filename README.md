# SocialGraphAI - Real-Time Personalized Social Feed Ranking Engine

A production-grade, FAANG-level microservices platform for real-time social feed ranking with advanced graph analytics, event-driven architecture, and comprehensive observability.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Functional Requirements](#functional-requirements)
3. [Non-Functional Requirements](#non-functional-requirements)
4. [Tech Stack](#tech-stack)
5. [Architecture](#architecture)
6. [Quick Start](#quick-start)
7. [API Documentation](#api-documentation)
8. [Monitoring & Observability](#monitoring--observability)
9. [Testing](#testing)
10. [Deployment](#deployment)
11. [Contributing](#contributing)

---

## Project Overview

**SocialGraphAI** is a real-time social feed ranking system designed to deliver personalized, ranked content to users based on their social graph, engagement patterns, and algorithmic scoring. The system processes millions of posts, manages complex follow relationships, and serves ranked feeds with sub-100ms latency.

### Key Features
- Real-time post ingestion via Kafka
- Graph-based relationship management (Neo4j)
- Personalized feed ranking with affinity scoring
- Sub-100ms feed retrieval with Redis caching
- JWT-based authentication and rate limiting
- Distributed tracing and centralized logging
- Horizontal scalability with microservices
- Production-ready with comprehensive monitoring

---

## Functional Requirements

### 1. User Management
- **User Registration & Authentication**
  - Register new users with email and password
  - Login with JWT token generation
  - Password hashing with BCrypt
  - Token refresh mechanism

- **User Profile Management**
  - View user profiles
  - Update user information
  - Deactivate/delete accounts

### 2. Social Graph Management
- **Follow/Unfollow**
  - Follow other users
  - Unfollow users
  - View followers list
  - View following list
  - Check if user is following another user

- **Graph Analytics**
  - Mutual followers detection
  - Follower count
  - Following count
  - Shortest path between users (future)

### 3. Post Management
- **Post Creation**
  - Create text posts
  - Add media attachments (future)
  - Set post visibility (public/private)
  - Timestamp tracking

- **Post Retrieval**
  - Get post by ID
  - Get posts by author
  - Get post metadata

### 4. Feed Ranking & Delivery
- **Personalized Feed**
  - Retrieve ranked feed for user
  - Pagination support (limit, offset)
  - Real-time ranking based on:
    - Recency (time decay)
    - Affinity (engagement history)
    - Social proximity (follower relationships)

- **Feed Caching**
  - Cache ranked feeds in Redis
  - Automatic cache invalidation
  - Cache warming on post creation

### 5. Engagement Tracking
- **Likes & Comments** (future)
  - Like/unlike posts
  - Comment on posts
  - Track engagement metrics

- **Engagement Analytics**
  - Like count per post
  - Comment count per post
  - Engagement rate calculation

### 6. Search & Discovery (future)
- Full-text search on posts
- Hashtag search
- User search
- Trending topics

---

## Non-Functional Requirements

### 1. Performance
- **Latency**
  - Feed retrieval: < 100ms (p99)
  - Post creation: < 500ms (p99)
  - User lookup: < 50ms (p99)

- **Throughput**
  - Support 10,000+ concurrent users
  - Process 100,000+ posts/hour
  - Serve 1,000,000+ feed requests/hour

- **Scalability**
  - Horizontal scaling via microservices
  - Load balancing across instances
  - Database sharding (future)

### 2. Reliability & Availability
- **Uptime**: 99.9% SLA
- **Fault Tolerance**
  - Circuit breakers for service calls
  - Retry logic with exponential backoff
  - Dead Letter Queues for failed messages
  - Graceful degradation

- **Data Consistency**
  - Idempotent operations
  - Transaction support
  - Event sourcing (future)

### 3. Security
- **Authentication & Authorization**
  - JWT-based authentication
  - Role-based access control (RBAC)
  - Rate limiting (100 req/min per user)
  - Input validation and sanitization

- **Data Protection**
  - Password hashing (BCrypt)
  - HTTPS/TLS encryption
  - SQL injection prevention
  - XSS protection

- **Audit & Compliance**
  - Audit logging for sensitive operations
  - User activity tracking
  - GDPR compliance (data deletion)

### 4. Observability
- **Logging**
  - Structured logging (JSON format)
  - Centralized log aggregation (ELK Stack)
  - Log levels: DEBUG, INFO, WARN, ERROR
  - Request/response logging

- **Metrics**
  - Prometheus metrics export
  - Key metrics:
    - HTTP request latency
    - Kafka consumer lag
    - Redis hit rate
    - Database connection pool usage
    - JVM memory/GC metrics

- **Tracing**
  - Distributed tracing (Jaeger)
  - Trace sampling (10%)
  - Service-to-service tracing
  - Latency breakdown per service

- **Alerting**
  - Prometheus alerts for:
    - High error rates (> 1%)
    - High latency (p99 > 500ms)
    - Service unavailability
    - Database connection pool exhaustion

### 5. Maintainability
- **Code Quality**
  - SonarQube analysis
  - Code coverage > 80%
  - Automated code reviews
  - Consistent code style

- **Documentation**
  - API documentation (Swagger/OpenAPI)
  - Architecture diagrams
  - Runbooks for common issues
  - Developer setup guide

- **Testing**
  - Unit tests (JUnit 5)
  - Integration tests (Testcontainers)
  - E2E tests (Playwright)
  - Load testing (Gatling)

### 6. Deployment & DevOps
- **CI/CD Pipeline**
  - Automated builds (GitHub Actions)
  - Automated testing
  - Container scanning (Trivy)
  - Automated deployment

- **Infrastructure**
  - Docker containerization
  - Docker Compose for local dev
  - Kubernetes ready (future)
  - Infrastructure as Code (Terraform)

---

## Tech Stack

### Backend Services
| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| Framework | Spring Boot | 3.2.0 | Microservices framework |
| Language | Java | 17+ | Primary language |
| Build Tool | Maven | 3.8+ | Dependency management |
| JDK | OpenJDK | 17 LTS | Java runtime |

### Databases & Storage
| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| Relational DB | PostgreSQL | 15 | User, post metadata |
| Graph DB | Neo4j | 5.x | Follow relationships |
| Cache | Redis | 7.x | Feed caching, sessions |
| Message Queue | Apache Kafka | 3.5.x | Event streaming |

### Message Broker & Streaming
| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| Broker | Kafka | 3.5.x | Post events |
| Coordination | Zookeeper | 3.8.x | Kafka coordination |
| Schema Registry | Confluent Schema Registry | 7.x | Kafka schema management (future) |

### Observability & Monitoring
| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| Metrics | Prometheus | 2.45.x | Metrics collection |
| Dashboards | Grafana | 10.x | Visualization |
| Tracing | Jaeger | 1.40.x | Distributed tracing |
| Logging | ELK Stack | 8.x | Log aggregation |
| - Elasticsearch | 8.x | Log storage |
| - Kibana | 8.x | Log visualization |
| - Logstash | 8.x | Log processing |

### Security & Authentication
| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| Auth | JWT (JSON Web Tokens) | - | Token-based auth |
| Password Hashing | BCrypt | - | Secure password storage |
| Rate Limiting | Bucket4j | 7.x | API rate limiting |
| Resilience | Resilience4j | 2.x | Circuit breakers, retries |

### Testing & Quality
| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| Unit Testing | JUnit 5 | 5.9.x | Unit tests |
| Mocking | Mockito | 5.x | Mock objects |
| Integration Testing | Testcontainers | 1.19.x | Container-based tests |
| Load Testing | Gatling | 3.9.x | Performance testing |
| Code Quality | SonarQube | 9.x | Code analysis |
| Container Scanning | Trivy | 0.45.x | Vulnerability scanning |

### DevOps & Deployment
| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| Containerization | Docker | 24.x | Container runtime |
| Orchestration | Docker Compose | 2.x | Local orchestration |
| CI/CD | GitHub Actions | - | Automation |
| Container Registry | Docker Hub | - | Image storage |

### Development Tools
| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| IDE | IntelliJ IDEA / VS Code | Latest | Development |
| Version Control | Git | 2.40+ | Source control |
| API Testing | Postman / cURL | Latest | API testing |
| Database Client | pgAdmin / DBeaver | Latest | DB management |

---

## Architecture

### System Architecture Diagram

\`\`\`
┌─────────────────────────────────────────────────────────────────┐
│                        Client Applications                       │
└────────────────────────────┬────────────────────────────────────┘
                             │
                    ┌────────▼────────┐
                    │  API Gateway    │
                    │  (Load Balancer)│
                    └────────┬────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
   ┌────▼────┐         ┌────▼────┐         ┌────▼────┐
   │  User   │         │  Feed   │         │  Feed   │
   │ Service │         │ Ingestor│         │   API   │
   │ (8080)  │         │ (8090)  │         │ (8081)  │
   └────┬────┘         └────┬────┘         └────┬────┘
        │                   │                    │
        │              ┌────▼────┐               │
        │              │  Kafka  │               │
        │              │ (Events)│               │
        │              └────┬────┘               │
        │                   │                    │
        │              ┌────▼────────┐           │
        │              │ Feed Ranker │           │
        │              │  (8100)     │           │
        │              └────┬────────┘           │
        │                   │                    │
   ┌────▼────┐         ┌────▼────┐         ┌────▼────┐
   │ Postgres │         │  Redis  │         │  Neo4j  │
   │ (Users)  │         │ (Cache) │         │ (Graph) │
   └──────────┘         └─────────┘         └─────────┘

Observability Layer:
┌──────────────────────────────────────────────────────────────────┐
│ Prometheus │ Grafana │ Jaeger │ ELK Stack │ Alertmanager        │
└──────────────────────────────────────────────────────────────────┘
\`\`\`

### Data Flow

\`\`\`
1. User Registration/Login
   Client → User Service → Postgres → JWT Token

2. Post Creation
   Client → Feed Ingestor → Postgres + Kafka Event

3. Feed Ranking
   Kafka Event → Feed Ranker → User Service (followers) → Redis

4. Feed Retrieval
   Client → Feed API → Redis → Feed Ingestor (post details)

5. Follow Relationship
   Client → User Service → Neo4j + Postgres
\`\`\`

### Microservices

#### 1. User Service (Port 8080)
- **Responsibilities**
  - User registration and authentication
  - User profile management
  - Follow/unfollow operations
  - Follower/following list retrieval

- **Dependencies**
  - PostgreSQL (user data)
  - Neo4j (follow relationships)
  - Redis (session cache)

- **Key Endpoints**
  - `POST /auth/register` - Register new user
  - `POST /auth/login` - Login and get JWT
  - `GET /users/{username}` - Get user profile
  - `POST /users/{from}/follow/{to}` - Follow user
  - `GET /users/{username}/followers` - Get followers

#### 2. Feed Ingestor (Port 8090)
- **Responsibilities**
  - Post creation and storage
  - Kafka event publishing
  - Post metadata retrieval

- **Dependencies**
  - PostgreSQL (post data)
  - Kafka (event publishing)

- **Key Endpoints**
  - `POST /post/create` - Create new post
  - `GET /post/{id}` - Get post details
  - `GET /posts/author/{authorId}` - Get author's posts

#### 3. Feed Ranker (Port 8100)
- **Responsibilities**
  - Consume post events from Kafka
  - Calculate ranking scores
  - Update Redis feed cache
  - Handle failed messages via DLT

- **Dependencies**
  - Kafka (event consumption)
  - Redis (feed cache)
  - User Service (follower lookup)

- **Scoring Algorithm**
  \`\`\`
  Score = 0.6 × Affinity + 0.4 × Recency
  
  Affinity = engagement_history (likes, comments, shares)
  Recency = time_decay_function(post_age)
  \`\`\`

#### 4. Feed API (Port 8081)
- **Responsibilities**
  - Retrieve personalized feeds
  - Pagination support
  - Post detail enrichment

- **Dependencies**
  - Redis (feed cache)
  - Feed Ingestor (post details)

- **Key Endpoints**
  - `GET /feed?userId={id}&limit=10&offset=0` - Get ranked feed

---

## Quick Start

### Prerequisites
- Docker Desktop 24.x+
- Java 17+
- Maven 3.8+
- Git 2.40+

### Step 1: Clone/Download Project
\`\`\`bash
# Option A: Download ZIP from v0
unzip socialgraphai.zip
cd socialgraphai

# Option B: Clone from GitHub
git clone <repo-url>
cd socialgraphai
\`\`\`

### Step 2: Start Infrastructure
\`\`\`bash
cd backend/socialgraphai
docker-compose up -d

# Verify all containers are running
docker ps
\`\`\`

### Step 3: Start Spring Boot Services (4 separate terminals)

**Terminal 1: User Service**
\`\`\`bash
cd backend/socialgraphai/user-service
mvn clean spring-boot:run
\`\`\`

**Terminal 2: Feed Ingestor**
\`\`\`bash
cd backend/socialgraphai/feed-ingestor
mvn clean spring-boot:run
\`\`\`

**Terminal 3: Feed Ranker**
\`\`\`bash
cd backend/socialgraphai/feed-ranker
mvn clean spring-boot:run
\`\`\`

**Terminal 4: Feed API**
\`\`\`bash
cd backend/socialgraphai/feed-api
mvn clean spring-boot:run
\`\`\`

### Step 4: Test End-to-End Flow
\`\`\`bash
# Register users
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@example.com","password":"password123"}'

# Login and get token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"password123"}'

# Create post
curl -X POST http://localhost:8090/post/create \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"content":"Hello World!"}'

# Get feed
curl -X GET "http://localhost:8081/feed?userId=alice&limit=10" \
  -H "Authorization: Bearer <TOKEN>"
\`\`\`

---

## API Documentation

### Authentication Endpoints

#### Register User
\`\`\`http
POST /auth/register
Content-Type: application/json

{
  "username": "alice",
  "email": "alice@example.com",
  "password": "password123"
}

Response: 201 Created
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "alice",
  "email": "alice@example.com",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
\`\`\`

#### Login
\`\`\`http
POST /auth/login
Content-Type: application/json

{
  "username": "alice",
  "password": "password123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": "550e8400-e29b-41d4-a716-446655440000"
}
\`\`\`

### User Endpoints

#### Get User Profile
\`\`\`http
GET /users/{username}
Authorization: Bearer <TOKEN>

Response: 200 OK
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "alice",
  "email": "alice@example.com",
  "followerCount": 150,
  "followingCount": 200,
  "createdAt": "2025-10-24T10:00:00Z"
}
\`\`\`

#### Follow User
\`\`\`http
POST /users/{from}/follow/{to}
Authorization: Bearer <TOKEN>

Response: 200 OK
{
  "message": "alice is now following bob"
}
\`\`\`

#### Get Followers
\`\`\`http
GET /users/{username}/followers?limit=10&offset=0
Authorization: Bearer <TOKEN>

Response: 200 OK
{
  "followers": [
    {
      "id": "...",
      "username": "bob",
      "email": "bob@example.com"
    }
  ],
  "totalCount": 150
}
\`\`\`

### Post Endpoints

#### Create Post
\`\`\`http
POST /post/create
Authorization: Bearer <TOKEN>
Content-Type: application/json

{
  "content": "Hello World! This is my first post 🚀"
}

Response: 201 Created
{
  "id": "post-123",
  "authorId": "alice",
  "content": "Hello World! This is my first post 🚀",
  "createdAt": "2025-10-24T16:35:00Z"
}
\`\`\`

#### Get Post
\`\`\`http
GET /post/{postId}
Authorization: Bearer <TOKEN>

Response: 200 OK
{
  "id": "post-123",
  "authorId": "alice",
  "content": "Hello World! This is my first post 🚀",
  "likeCount": 42,
  "commentCount": 5,
  "createdAt": "2025-10-24T16:35:00Z"
}
\`\`\`

### Feed Endpoints

#### Get Personalized Feed
\`\`\`http
GET /feed?userId={userId}&limit=10&offset=0
Authorization: Bearer <TOKEN>

Response: 200 OK
{
  "userId": "bob",
  "posts": [
    {
      "id": "post-123",
      "authorId": "alice",
      "content": "Hello World! This is my first post 🚀",
      "score": 0.85,
      "likeCount": 42,
      "commentCount": 5,
      "createdAt": "2025-10-24T16:35:00Z"
    }
  ],
  "totalCount": 1,
  "hasMore": false
}
\`\`\`

### Full Swagger Documentation
- **URL**: http://localhost:8080/swagger-ui.html
- All endpoints with request/response schemas
- Try-it-out functionality

---

## Monitoring & Observability

### Prometheus Metrics
- **URL**: http://localhost:9090
- **Key Metrics**
  - `http_requests_total` - Total HTTP requests
  - `http_request_duration_seconds` - Request latency
  - `kafka_consumer_lag` - Kafka consumer lag
  - `redis_connected_clients` - Redis connections
  - `jvm_memory_used_bytes` - JVM memory usage

### Grafana Dashboards
- **URL**: http://localhost:3000
- **Credentials**: admin / admin
- **Pre-built Dashboards**
  - Service Health
  - Request Latency
  - Kafka Metrics
  - Redis Performance
  - Database Connections

### Jaeger Distributed Tracing
- **URL**: http://localhost:16686
- **Features**
  - Trace requests across services
  - Latency breakdown per service
  - Error tracking
  - Service dependency graph

### ELK Stack (Elasticsearch, Logstash, Kibana)
- **Kibana URL**: http://localhost:5601
- **Features**
  - Centralized logging
  - Log search and filtering
  - Log analytics
  - Alert creation

### Neo4j Graph Browser
- **URL**: http://localhost:7474
- **Credentials**: neo4j / neo4jpass
- **Features**
  - Visualize follow relationships
  - Query graph data
  - Performance analysis

---

## Testing

### Unit Tests
\`\`\`bash
cd backend/socialgraphai/user-service
mvn test
\`\`\`

### Integration Tests
\`\`\`bash
cd backend/socialgraphai/user-service
mvn verify
\`\`\`

### Load Testing (Gatling)
\`\`\`bash
cd backend/socialgraphai/load-tests
mvn gatling:test
\`\`\`

### Test Coverage
\`\`\`bash
mvn clean test jacoco:report
# Report: target/site/jacoco/index.html
\`\`\`

---

## Deployment

### Docker Build
\`\`\`bash
cd backend/socialgraphai/user-service
docker build -t socialgraphai/user-service:1.0.0 .
\`\`\`

### Push to Registry
\`\`\`bash
docker tag socialgraphai/user-service:1.0.0 <registry>/user-service:1.0.0
docker push <registry>/user-service:1.0.0
\`\`\`

### Kubernetes Deployment (future)
\`\`\`bash
kubectl apply -f k8s/
\`\`\`

### CI/CD Pipeline
- **GitHub Actions**: `.github/workflows/`
- **Automated**: Build → Test → Scan → Deploy

---

## Contributing

### Code Style
- Follow Google Java Style Guide
- Use 2-space indentation
- Max line length: 120 characters

### Commit Messages
\`\`\`
[SERVICE] Brief description

Detailed explanation if needed.

Fixes #123
\`\`\`

### Pull Request Process
1. Create feature branch: `git checkout -b feature/xyz`
2. Make changes and commit
3. Push to GitHub: `git push origin feature/xyz`
4. Create Pull Request
5. Wait for CI/CD to pass
6. Request review from maintainers
7. Merge after approval

---

## Support & Troubleshooting

### Common Issues

**Kafka Connection Error**
\`\`\`bash
docker-compose down -v
docker-compose up -d
\`\`\`

**Postgres Connection Error**
\`\`\`bash
docker restart postgres
sleep 5
# Restart services
\`\`\`

**Port Already in Use**
\`\`\`bash
lsof -i :8080
kill -9 <PID>
\`\`\`

### Documentation
- [Complete Setup Guide](COMPLETE_SETUP_GUIDE.md)
- [Docker Setup Guide](DOCKER_SETUP.md)
- [Performance Tuning](PERFORMANCE_TUNING.md)

### Contact
- Issues: GitHub Issues
- Discussions: GitHub Discussions
- Email: support@socialgraphai.dev

---

## License

MIT License - See LICENSE file for details

---

## Acknowledgments

Built with Spring Boot, Kafka, Redis, Neo4j, and modern DevOps practices for production-grade systems.
