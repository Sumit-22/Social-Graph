#!/bin/bash

echo "=========================================="
echo "Health Check - SocialGraphAI Services"
echo "=========================================="

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

check_service() {
    local name=$1
    local port=$2
    local cmd=$3
    
    if eval "$cmd" > /dev/null 2>&1; then
        echo -e "${GREEN}✓ $name (port $port)${NC}"
        return 0
    else
        echo -e "${RED}✗ $name (port $port) - NOT RESPONDING${NC}"
        return 1
    fi
}

echo ""
echo "Infrastructure Services:"
check_service "Zookeeper" "2181" "echo ruok | nc localhost 2181"
check_service "Kafka" "9092" "docker-compose exec -T kafka kafka-broker-api-versions.sh --bootstrap-server localhost:9092"
check_service "Redis" "6379" "redis-cli -p 6379 ping"
check_service "Postgres" "5432" "pg_isready -h localhost -p 5432 -U pguser"
check_service "Neo4j" "7474" "curl -s http://localhost:7474 > /dev/null"

echo ""
echo "Observability Services:"
check_service "Prometheus" "9090" "curl -s http://localhost:9090/-/healthy > /dev/null"
check_service "Grafana" "3001" "curl -s http://localhost:3001/api/health > /dev/null"
check_service "Jaeger" "16686" "curl -s http://localhost:16686/api/services > /dev/null"
check_service "Elasticsearch" "9200" "curl -s http://localhost:9200/_cluster/health > /dev/null"
check_service "Kibana" "5601" "curl -s http://localhost:5601/api/status > /dev/null"

echo ""
echo "Spring Boot Services:"
check_service "User Service" "8080" "curl -s http://localhost:8080/actuator/health > /dev/null"
check_service "Feed API" "8081" "curl -s http://localhost:8081/actuator/health > /dev/null"
check_service "Feed Ingestor" "8090" "curl -s http://localhost:8090/actuator/health > /dev/null"
check_service "Feed Ranker" "8100" "curl -s http://localhost:8100/actuator/health > /dev/null"

echo ""
echo "=========================================="
