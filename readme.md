
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

# **Non-Functional Requirements**

### **Scalability**

* Horizontal scaling via microservices
* Kafka-based decoupling

### **Low Latency**

* Feed read latency < 100ms
* Redis ranking fetch < 5ms

### **Availability**

* 99.9% uptime target

### **Consistency**

* Eventual consistency via Kafka + Redis

### **Security**

* JWT authentication
* Role-based access

---

# **Tech Stack**

* **Java 17 + Spring Boot**
* **Kafka + Zookeeper**
* **Redis**
* **PostgreSQL**
* **Neo4j Graph DB**
* **Docker / Docker Compose**
* **Prometheus + Grafana**
* **ELK Stack**
* **Jaeger Tracing**

---

# **ER Diagram**

```mermaid
erDiagram
    USERS ||--o{ POSTS : creates
    USERS ||--o{ FOLLOWS : follows
    POSTS ||--o{ LIKES : receives
    POSTS ||--o{ COMMENTS : receives
    USERS ||--o{ LIKES : gives
    USERS ||--o{ COMMENTS : writes

    USERS {
        string id PK
        string username
        string email
        string password
    }

    POSTS {
        string id PK
        string author_id FK
        string content
        int like_count
        timestamp created_at
    }

    FOLLOWS {
        long id PK
        string follower_id FK
        string following_id FK
    }
```

---

# **Neo4j Graph Schema**

```mermaid
graph LR
    U1["User A"] -->|FOLLOWS| U2["User B"]
    U1 -->|FOLLOWS| U3["User C"]
    U2 -->|FOLLOWS| U4["User D"]
    U3 -->|FOLLOWS| U4
    U4 -->|FOLLOWS| U1
```

---
