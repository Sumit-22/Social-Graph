
# **Social Graph – Feed Ranking Microservices System**

A production-grade, distributed social-media feed system designed using **Kafka, Redis, Neo4j, PostgreSQL** and **Spring Boot microservices**—similar to Instagram/Twitter feed ranking pipelines.

---

## **📌 Table of Contents**

* [Overview](#overview)
* [Architecture Diagram](#architecture-diagram)
* [Functional Requirements](#functional-requirements)
* [Non-Functional Requirements](#non-functional-requirements)
* [Tech Stack](#tech-stack)
* [ER Diagram](#er-diagram)
* [Neo4j Graph Schema](#neo4j-graph-schema)
* [Data Flow Diagram](#data-flow-diagram)
* [Service Component Diagram (LLD)](#service-component-diagram-lld)
* [Ranking Algorithm](#ranking-algorithm)
* [Deployment Architecture](#deployment-architecture)
* [Feed Retrieval Flow](#feed-retrieval-flow)
* [Authentication Flow](#authentication-flow)
* [Resilience & Error Handling](#resilience--error-handling)
* [Performance & SLOs](#performance--slos)

---

# **Overview**

This system provides a **high-performance social media feed**, using:

* Event-driven architecture
* Real-time ranking
* Distributed caching
* Graph-based follower modeling
* Scalable microservices

It mimics real-world systems used by Instagram, Facebook, Twitter.

---

# **Architecture Diagram**

```mermaid
graph TB
    Client["🖥️ Client"]
    APIGateway["API Gateway"]
    
    subgraph "Microservices"
        US["👤 User Service<br/>Port 8080"]
        FI["📥 Feed Ingestor<br/>Port 8090"]
        FR["⚙️ Feed Ranker<br/>Port 8100"]
        FA["📊 Feed API<br/>Port 8081"]
    end
    
    subgraph "Message Queue"
        Kafka["Kafka Broker<br/>Port 9092"]
        ZK["Zookeeper<br/>Port 2181"]
    end
    
    subgraph "Databases"
        Postgres["PostgreSQL"]
        Neo4j["Neo4j"]
        Redis["Redis Cache"]
    end
    
    subgraph "Observability"
        Prom["Prometheus"]
        Grafana["Grafana"]
        Jaeger["Jaeger"]
        ELK["ELK Stack"]
    end
    
    Client -->|HTTP| APIGateway
    APIGateway -->|Auth| US
    APIGateway -->|Create Post| FI
    APIGateway -->|Get Feed| FA

    US --> Neo4j
    FI --> Postgres
    FI --> Kafka

    Kafka --> FR
    FR --> Redis
    FR --> Neo4j

    FA --> Redis
    FA --> Postgres
```

---

# **Functional Requirements**

### ✔️ Users

* Register / Login
* Follow / Unfollow
* View other users

### ✔️ Posts

* Create post
* Store post in DB
* Publish event to Kafka

### ✔️ Feed System

* Rank posts per follower
* Store ranked feed in Redis
* Retrieve feed with pagination

### ✔️ Observability

* Metrics → Prometheus
* Logs → ELK
* Tracing → Jaeger

---
