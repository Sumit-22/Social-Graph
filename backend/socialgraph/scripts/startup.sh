#!/bin/bash

set -e

echo "=========================================="
echo "SocialGraphAI - Docker Startup Script"
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if Docker is running
echo -e "${YELLOW}[1/6] Checking Docker...${NC}"
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}ERROR: Docker is not running. Please start Docker Desktop.${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Docker is running${NC}"

# Stop existing containers
echo -e "${YELLOW}[2/6] Stopping existing containers...${NC}"
docker-compose down 2>/dev/null || true
sleep 2
echo -e "${GREEN}✓ Cleaned up${NC}"

# Start infrastructure
echo -e "${YELLOW}[3/6] Starting infrastructure (Zookeeper, Kafka, Redis, Postgres, Neo4j)...${NC}"
docker-compose up -d zookeeper kafka redis postgres neo4j
echo -e "${GREEN}✓ Infrastructure started${NC}"

# Wait for Zookeeper
echo -e "${YELLOW}[4/6] Waiting for Zookeeper to be healthy...${NC}"
for i in {1..30}; do
    if docker-compose exec -T zookeeper echo ruok | nc localhost 2181 > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Zookeeper is healthy${NC}"
        break
    fi
    echo "  Attempt $i/30..."
    sleep 2
done

# Wait for Kafka
echo -e "${YELLOW}[5/6] Waiting for Kafka to be healthy...${NC}"
for i in {1..60}; do
    if docker-compose exec -T kafka kafka-broker-api-versions.sh --bootstrap-server localhost:9092 > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Kafka is healthy${NC}"
        break
    fi
    echo "  Attempt $i/60..."
    sleep 2
done

# Start observability stack
echo -e "${YELLOW}[6/6] Starting observability stack (Prometheus, Grafana, Jaeger, ELK)...${NC}"
docker-compose up -d prometheus grafana jaeger elasticsearch kibana
echo -e "${GREEN}✓ Observability stack started${NC}"

echo ""
echo -e "${GREEN}=========================================="
echo "All services are starting up!"
echo "==========================================${NC}"
echo ""
echo "Service URLs:"
echo "  Kafka:        localhost:9092"
echo "  Redis:        localhost:6379"
echo "  Postgres:     localhost:5432 (pguser/pgpass)"
echo "  Neo4j:        http://localhost:7474 (neo4j/neo4jpass)"
echo "  Prometheus:   http://localhost:9090"
echo "  Grafana:      http://localhost:3001 (admin/admin)"
echo "  Jaeger:       http://localhost:16686"
echo "  Kibana:       http://localhost:5601"
echo ""
echo "Next steps:"
echo "  1. Open 4 terminal windows"
echo "  2. In each, run one of these commands:"
echo "     - cd backend/socialgraphai/user-service && mvn spring-boot:run"
echo "     - cd backend/socialgraphai/feed-ingestor && mvn spring-boot:run"
echo "     - cd backend/socialgraphai/feed-ranker && mvn spring-boot:run"
echo "     - cd backend/socialgraphai/feed-api && mvn spring-boot:run"
echo ""
echo "Service URLs (after starting):"
echo "  User Service:   http://localhost:8080/swagger-ui.html"
echo "  Feed API:       http://localhost:8081/swagger-ui.html"
echo "  Feed Ingestor:  http://localhost:8090/swagger-ui.html"
echo ""
