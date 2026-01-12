# Performance Tuning Guide

## Database Optimization

### PostgreSQL
- Connection pooling: HikariCP with max 20 connections
- Batch operations: 20 items per batch
- Query optimization: Indexes on frequently queried columns
- Vacuum and analyze regularly

### Neo4j
- Heap memory: 1GB
- Page cache: 512MB
- Connection pooling for driver

### Redis
- Max memory: 2GB
- Eviction policy: allkeys-lru
- Persistence: AOF enabled
- Key expiration: Set TTLs on feed keys

## Application Tuning

### Tomcat
- Max threads: 200
- Min spare threads: 10
- Max connections: 10,000
- Accept count: 100

### Kafka
- Batch size: 20
- Compression: snappy
- Retention: 7 days
- Partitions: 3 for post-created topic

## Monitoring & Metrics

### Key Metrics to Track
- Request latency (p50, p95, p99)
- Throughput (requests/sec)
- Error rate
- Database connection pool utilization
- Redis memory usage
- Kafka consumer lag

### Dashboards
- Grafana: http://localhost:3001 (admin/admin)
- Prometheus: http://localhost:9090
- Jaeger: http://localhost:16686
- Kibana: http://localhost:5601

## Load Testing

Run Gatling load tests:
\`\`\`bash
cd backend/socialgraphai/load-tests
mvn gatling:test
\`\`\`

Expected performance targets:
- Register endpoint: < 500ms p95
- Login endpoint: < 300ms p95
- Feed endpoint: < 1000ms p95
- Success rate: > 99%
