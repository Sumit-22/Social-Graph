# SocialGraphAI - Complete Step-by-Step Setup Guide

## Prerequisites Check

Pehle ye check karo ki tumhare paas sab kuch installed hai:

### 1. Docker Desktop
- Download: https://www.docker.com/products/docker-desktop
- Install karo aur open karo
- Terminal mein check karo:
  \`\`\`bash
  docker --version
  docker-compose --version
  \`\`\`
- Expected output: Docker version 20.10+ aur Docker Compose 2.0+

### 2. Java 17+
- Download: https://www.oracle.com/java/technologies/downloads/
- Install karo
- Terminal mein check karo:
  \`\`\`bash
  java -version
  \`\`\`
- Expected: Java 17 ya usse zyada

### 3. Maven
- Download: https://maven.apache.org/download.cgi
- Install karo
- Terminal mein check karo:
  \`\`\`bash
  mvn --version
  \`\`\`
- Expected: Maven 3.8+

### 4. Git (Optional but recommended)
- Download: https://git-scm.com/
- Install karo

---

## Step 1: Project Download aur Setup (5 minutes)

### Option A: ZIP File se
1. v0 se ZIP download karo
2. Unzip karo kisi folder mein, example: `C:\Projects\socialgraphai` (Windows) ya `~/Projects/socialgraphai` (Mac/Linux)
3. Terminal/Command Prompt kholo aur us folder mein jao:
   \`\`\`bash
   cd ~/Projects/socialgraphai
   \`\`\`

### Option B: GitHub se (agar push kiya ho)
\`\`\`bash
git clone <your-github-repo-url>
cd socialgraphai
\`\`\`

---

## Step 2: Docker Infrastructure Start (10-15 minutes)

Yeh step mein Kafka, Zookeeper, Redis, Postgres, Neo4j, Prometheus, Grafana, Jaeger sab start hoga.

### Terminal 1: Docker Compose Start
\`\`\`bash
cd backend/socialgraphai

# Windows
docker-compose up -d

# Mac/Linux
docker-compose up -d
\`\`\`

**Expected Output:**
\`\`\`
Creating zookeeper ... done
Creating kafka ... done
Creating postgres ... done
Creating redis ... done
Creating neo4j ... done
Creating prometheus ... done
Creating grafana ... done
Creating jaeger ... done
\`\`\`

### Verify Containers are Running
\`\`\`bash
docker ps
\`\`\`

**Expected:** 8 containers running (zookeeper, kafka, postgres, redis, neo4j, prometheus, grafana, jaeger)

### Wait for Services to be Healthy
\`\`\`bash
# Mac/Linux
bash scripts/health-check.sh

# Windows (PowerShell)
powershell -ExecutionPolicy Bypass -File scripts/health-check.ps1
\`\`\`

**Expected Output:**
\`\`\`
✓ Zookeeper is healthy
✓ Kafka is healthy
✓ Postgres is healthy
✓ Redis is healthy
✓ Neo4j is healthy
✓ Prometheus is healthy
✓ Grafana is healthy
✓ Jaeger is healthy
All services are ready!
\`\`\`

---

## Step 3: Spring Boot Services Start (20-30 minutes)

Ab 4 alag-alag terminal/command prompt kholo. Har ek mein ek service start hoga.

### Terminal 2: User Service (Port 8080)
\`\`\`bash
cd backend/socialgraphai/user-service
mvn clean spring-boot:run
\`\`\`

**Expected Output (last few lines):**
\`\`\`
2025-10-24 16:30:00.123  INFO 12345 --- [main] c.s.user.UserServiceApplication : Started UserServiceApplication in 15.234 seconds
2025-10-24 16:30:00.456  INFO 12345 --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat started on port(s): 8080
\`\`\`

### Terminal 3: Feed Ingestor (Port 8090)
\`\`\`bash
cd backend/socialgraphai/feed-ingestor
mvn clean spring-boot:run
\`\`\`

**Expected Output:**
\`\`\`
2025-10-24 16:30:30.123  INFO 12345 --- [main] c.s.ingestor.FeedIngestorApplication : Started FeedIngestorApplication in 12.456 seconds
2025-10-24 16:30:30.456  INFO 12345 --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat started on port(s): 8090
\`\`\`

### Terminal 4: Feed Ranker (Port 8100)
\`\`\`bash
cd backend/socialgraphai/feed-ranker
mvn clean spring-boot:run
\`\`\`

**Expected Output:**
\`\`\`
2025-10-24 16:30:45.123  INFO 12345 --- [main] c.s.ranker.FeedRankerApplication : Started FeedRankerApplication in 10.234 seconds
2025-10-24 16:30:45.456  INFO 12345 --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat started on port(s): 8100
\`\`\`

### Terminal 5: Feed API (Port 8081)
\`\`\`bash
cd backend/socialgraphai/feed-api
mvn clean spring-boot:run
\`\`\`

**Expected Output:**
\`\`\`
2025-10-24 16:31:00.123  INFO 12345 --- [main] c.s.feed.FeedApiApplication : Started FeedApiApplication in 11.567 seconds
2025-10-24 16:31:00.456  INFO 12345 --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat started on port(s): 8081
\`\`\`

---

## Step 4: Test Karo - End-to-End Flow (5 minutes)

Ab ek naya terminal kholo aur ye commands run karo:

### 4.1 User Create Karo
\`\`\`bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "password": "password123"
  }'
\`\`\`

**Expected Response:**
\`\`\`json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "alice",
  "email": "alice@example.com",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
\`\`\`

**Token ko save karo** (aage use hoga)

### 4.2 Dusra User Create Karo
\`\`\`bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "bob",
    "email": "bob@example.com",
    "password": "password123"
  }'
\`\`\`

**Token ko save karo**

### 4.3 Follow Relationship Create Karo
\`\`\`bash
# Bob ko Alice ko follow karna hai
curl -X POST http://localhost:8080/users/bob/follow/alice \
  -H "Authorization: Bearer <BOB_TOKEN>" \
  -H "Content-Type: application/json"
\`\`\`

**Expected Response:**
\`\`\`json
{
  "message": "bob is now following alice"
}
\`\`\`

### 4.4 Post Create Karo (Alice se)
\`\`\`bash
curl -X POST http://localhost:8090/post/create \
  -H "Authorization: Bearer <ALICE_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Hello World! This is my first post 🚀"
  }'
\`\`\`

**Expected Response:**
\`\`\`json
{
  "id": "post-123",
  "authorId": "alice",
  "content": "Hello World! This is my first post 🚀",
  "createdAt": "2025-10-24T16:35:00Z"
}
\`\`\`

### 4.5 Wait 2-3 Seconds (Kafka processing)
\`\`\`bash
sleep 3
\`\`\`

### 4.6 Feed Fetch Karo (Bob ka)
\`\`\`bash
curl -X GET "http://localhost:8081/feed?userId=bob&limit=10" \
  -H "Authorization: Bearer <BOB_TOKEN>"
\`\`\`

**Expected Response:**
\`\`\`json
{
  "userId": "bob",
  "posts": [
    {
      "id": "post-123",
      "authorId": "alice",
      "content": "Hello World! This is my first post 🚀",
      "score": 0.85,
      "createdAt": "2025-10-24T16:35:00Z"
    }
  ],
  "totalCount": 1
}
\`\`\`

**Agar ye response mila, to congratulations! 🎉 Full flow kaam kar raha hai!**

---

## Step 5: Dashboards Access Karo (Optional but Recommended)

### Swagger API Documentation
- URL: http://localhost:8080/swagger-ui.html
- Yahan se sab APIs test kar sakte ho

### Prometheus Metrics
- URL: http://localhost:9090
- Services ke metrics dekh sakte ho

### Grafana Dashboards
- URL: http://localhost:3000
- Username: admin
- Password: admin
- Pre-built dashboards dekh sakte ho

### Jaeger Distributed Tracing
- URL: http://localhost:16686
- Request traces dekh sakte ho

### Neo4j Graph Database
- URL: http://localhost:7474
- Username: neo4j
- Password: neo4jpass
- Follow relationships visualize kar sakte ho

### Kibana Logs
- URL: http://localhost:5601
- Centralized logs dekh sakte ho

---

## Step 6: Load Testing (Optional)

Agar project ko stress test karna ho:

\`\`\`bash
cd backend/socialgraphai/load-tests
mvn gatling:test
\`\`\`

Ye 100+ concurrent users simulate karega aur performance report generate karega.

---

## Troubleshooting

### Problem 1: Kafka Connection Error
\`\`\`
ERROR: Connection refused to kafka:9092
\`\`\`

**Solution:**
\`\`\`bash
# Docker logs check karo
docker logs kafka

# Kafka ko restart karo
docker restart kafka

# 10 seconds wait karo
sleep 10

# Services ko restart karo
\`\`\`

### Problem 2: Postgres Connection Error
\`\`\`
ERROR: FATAL: remaining connection slots are reserved
\`\`\`

**Solution:**
\`\`\`bash
docker restart postgres
sleep 5
# Services ko restart karo
\`\`\`

### Problem 3: Port Already in Use
\`\`\`
ERROR: Address already in use: 8080
\`\`\`

**Solution:**
\`\`\`bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Mac/Linux
lsof -i :8080
kill -9 <PID>
\`\`\`

### Problem 4: Maven Build Failure
\`\`\`
ERROR: Failed to execute goal
\`\`\`

**Solution:**
\`\`\`bash
# Maven cache clear karo
mvn clean

# Dependencies download karo
mvn dependency:resolve

# Phir se try karo
mvn spring-boot:run
\`\`\`

### Problem 5: Docker Out of Memory
\`\`\`
ERROR: Cannot allocate memory
\`\`\`

**Solution:**
- Docker Desktop settings mein memory increase karo (minimum 4GB)
- Restart Docker

---

## Quick Reference - All URLs

| Service | URL | Purpose |
|---------|-----|---------|
| User Service | http://localhost:8080 | Auth, Users, Follow |
| Feed API | http://localhost:8081 | Feed retrieval |
| Feed Ingestor | http://localhost:8090 | Post creation |
| Feed Ranker | http://localhost:8100 | Ranking (internal) |
| Swagger Docs | http://localhost:8080/swagger-ui.html | API Documentation |
| Prometheus | http://localhost:9090 | Metrics |
| Grafana | http://localhost:3000 | Dashboards |
| Jaeger | http://localhost:16686 | Tracing |
| Neo4j | http://localhost:7474 | Graph DB |
| Kibana | http://localhost:5601 | Logs |
| Redis | localhost:6379 | Cache (CLI) |
| Postgres | localhost:5432 | Database |

---

## Next Steps

1. **Sample Data Load Karo:**
   \`\`\`bash
   cd backend/socialgraphai
   node scripts/load-sample-data.js
   \`\`\`

2. **Load Testing Run Karo:**
   \`\`\`bash
   cd load-tests
   mvn gatling:test
   \`\`\`

3. **Monitoring Setup Karo:**
   - Grafana mein alerts configure karo
   - Jaeger traces analyze karo

4. **Production Deployment:**
   - Kubernetes setup karo
   - CI/CD pipeline activate karo (GitHub Actions)

---

## Support

Agar koi error aaye:
1. Docker logs check karo: `docker logs <container-name>`
2. Service logs check karo: Terminal mein error dekho
3. Health check run karo: `bash scripts/health-check.sh`
4. Restart everything: `docker-compose down && docker-compose up -d`
