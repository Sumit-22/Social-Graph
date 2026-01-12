# Docker Setup & Troubleshooting Guide

## Quick Start

### 1. Start Infrastructure
\`\`\`bash
cd backend/socialgraphai
chmod +x scripts/startup.sh
./scripts/startup.sh
\`\`\`

This will:
- Stop any existing containers
- Start Zookeeper (waits for health check)
- Start Kafka (waits for Zookeeper to be healthy)
- Start Redis, Postgres, Neo4j
- Start Prometheus, Grafana, Jaeger, ELK Stack

### 2. Start Spring Boot Services (in separate terminals)

**Terminal 1 - User Service:**
\`\`\`bash
cd backend/socialgraphai/user-service
mvn spring-boot:run
\`\`\`

**Terminal 2 - Feed Ingestor:**
\`\`\`bash
cd backend/socialgraphai/feed-ingestor
mvn spring-boot:run
\`\`\`

**Terminal 3 - Feed Ranker:**
\`\`\`bash
cd backend/socialgraphai/feed-ranker
mvn spring-boot:run
\`\`\`

**Terminal 4 - Feed API:**
\`\`\`bash
cd backend/socialgraphai/feed-api
mvn spring-boot:run
\`\`\`

### 3. Verify Everything is Running
\`\`\`bash
chmod +x scripts/health-check.sh
./scripts/health-check.sh
\`\`\`

---

## Troubleshooting

### Issue: Kafka crashes with "Kafka server is still starting up, cannot shut down!"

**Root Cause:** Zookeeper is not ready when Kafka tries to connect.

**Solution:**
\`\`\`bash
# 1. Clean up everything
docker-compose down -v

# 2. Wait 10 seconds
sleep 10

# 3. Start only Zookeeper first
docker-compose up -d zookeeper

# 4. Wait for Zookeeper to be healthy (check logs)
docker-compose logs -f zookeeper

# 5. Once healthy, start Kafka
docker-compose up -d kafka

# 6. Check Kafka logs
docker-compose logs -f kafka
\`\`\`

### Issue: "Connection refused" when connecting to Kafka from Spring Boot

**Root Cause:** Services are using `localhost:9092` but Kafka is in a Docker network.

**Solution:** Services should connect to `kafka:9092` (Docker DNS name), not `localhost:9092`.

Check your `application.yml`:
\`\`\`yaml
spring:
  kafka:
    bootstrap-servers: kafka:9092  # NOT localhost:9092
\`\`\`

### Issue: Postgres connection refused

**Solution:**
\`\`\`bash
# Check if Postgres is running
docker-compose ps postgres

# Check Postgres logs
docker-compose logs postgres

# Verify connection
psql -h localhost -U pguser -d socialdb -c "SELECT 1"
\`\`\`

### Issue: Redis connection refused

**Solution:**
\`\`\`bash
# Check if Redis is running
docker-compose ps redis

# Test Redis connection
redis-cli -p 6379 ping
# Should return: PONG
\`\`\`

### Issue: Neo4j not responding

**Solution:**
\`\`\`bash
# Check Neo4j logs
docker-compose logs neo4j

# Access Neo4j Browser
# http://localhost:7474
# Username: neo4j
# Password: neo4jpass
\`\`\`

### Issue: Out of memory errors

**Solution:** Increase Docker memory limits:
1. Open Docker Desktop → Settings → Resources
2. Increase Memory to at least 8GB
3. Increase Swap to 2GB
4. Restart Docker

### Issue: Port already in use

**Solution:**
\`\`\`bash
# Find what's using the port (example: 9092)
lsof -i :9092

# Kill the process
kill -9 <PID>

# Or change the port in docker-compose.yml
\`\`\`

---

## Service Ports Reference

| Service | Port | URL |
|---------|------|-----|
| Zookeeper | 2181 | - |
| Kafka | 9092 | - |
| Redis | 6379 | - |
| Postgres | 5432 | - |
| Neo4j | 7474/7687 | http://localhost:7474 |
| Prometheus | 9090 | http://localhost:9090 |
| Grafana | 3001 | http://localhost:3001 |
| Jaeger | 16686 | http://localhost:16686 |
| Elasticsearch | 9200 | http://localhost:9200 |
| Kibana | 5601 | http://localhost:5601 |
| User Service | 8080 | http://localhost:8080 |
| Feed API | 8081 | http://localhost:8081 |
| Feed Ingestor | 8090 | http://localhost:8090 |
| Feed Ranker | 8100 | http://localhost:8100 |

---

## Useful Docker Commands

\`\`\`bash
# View all running containers
docker-compose ps

# View logs for a specific service
docker-compose logs -f kafka

# View logs for all services
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v

# Restart a specific service
docker-compose restart kafka

# Execute command in container
docker-compose exec kafka kafka-topics.sh --list --bootstrap-server localhost:9092

# View resource usage
docker stats
\`\`\`

---

## Testing the Full Flow

Once all services are running:

### 1. Create Users
\`\`\`bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"password123"}'

curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"bob","password":"password123"}'
\`\`\`

### 2. Login and Get JWT Token
\`\`\`bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"password123"}'
\`\`\`

Response:
\`\`\`json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": "alice-uuid"
}
\`\`\`

### 3. Create Follow Relationship
\`\`\`bash
curl -X POST http://localhost:8080/users/alice/follow/bob \
  -H "Authorization: Bearer <TOKEN>"
\`\`\`

### 4. Create a Post
\`\`\`bash
curl -X POST http://localhost:8090/post/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"authorId":"bob","content":"Hello World!"}'
\`\`\`

### 5. Fetch Personalized Feed
\`\`\`bash
curl -X GET "http://localhost:8081/feed?userId=alice&limit=10" \
  -H "Authorization: Bearer <TOKEN>"
\`\`\`

---

## Monitoring

### Prometheus
- URL: http://localhost:9090
- Query metrics: `http_requests_total`, `kafka_consumer_lag`, `redis_connected_clients`

### Grafana
- URL: http://localhost:3001
- Username: admin
- Password: admin
- Add Prometheus as data source: http://prometheus:9090

### Jaeger
- URL: http://localhost:16686
- View distributed traces across services

### Kibana
- URL: http://localhost:5601
- View centralized logs from all services

---

## Performance Tuning

### Kafka
- Batch size: 16KB (default)
- Compression: snappy
- Replication factor: 1 (for dev), 3 (for prod)

### Redis
- Max memory: 2GB
- Eviction policy: allkeys-lru
- Persistence: AOF enabled

### Postgres
- Max connections: 200
- Shared buffers: 256MB
- Effective cache size: 1GB

### Neo4j
- Heap size: 512MB - 1GB
- Page cache: 512MB

---

## Cleanup

\`\`\`bash
# Stop all services
docker-compose down

# Remove all volumes (WARNING: deletes data)
docker-compose down -v

# Remove all images
docker-compose down --rmi all
