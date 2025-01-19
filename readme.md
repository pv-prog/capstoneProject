
## Docker Compose Build and Start Services

To build and start the services using Docker Compose, follow the steps below:

1. Navigate to the project directory:

   cd capstoneProject
   
2. Run the docker compose build:
   
   docker-compose up --build -d

   
# Microservices Architecture Contract

**Version**: 1.0  
**Date**: January 15, 2025

## 1. Overview

This document defines the architecture and interactions of the microservices-based system. It includes details about each service, its responsibilities, endpoints, and communication patterns between the services.

### Components:
- Backend Services
- Kafka-related Services
- Eureka Server
- Logging & Monitoring Services
- Grafana Visualization
- Zookeeper
- Frontend Service

---

## 2. Service Definitions

### 2.1. Backend Services

#### `ccm-backend`
- **Purpose**: Provides core business logic and data access layers for the system.
- **Image**: `ccms-application-backend`
- **Command**: `mvn spring-boot:run`
- **Port**: 8091
- **API Endpoints**: 
    - Response format: JSON

#### `ccm-ccmscustomer`
- **Purpose**: Manages customer-related information and provides API for customer operations.
- **Image**: `ccms-application-ccmscustomer`
- **Command**: `mvn spring-boot:run`
- **Port**: 8082
- **API Endpoints**:
    - Response format: JSON

### 2.2. Kafka-related Services

#### `ccm-kafka-broker`
- **Purpose**: Kafka message broker for event-driven communication between services.
- **Image**: `ccms-application-kafka`
- **Port**: 9092 (public), 29092 (internal)
- **Topics**:
    - `Access-logs`: Data related to API/URL access
    - `Creditcard-log-topic`: Data related to Creditcard Operations
    - **Consumer Groups**:
        - `credit-card-consumer-group` (Topic: Access-logs,Creditcard-log-topic)
        - `connect-elasticsearch-sink` (Topic: Access-logs,Creditcard-log-topic)
- **Message Format**: JSON

#### `ccm-kafka-connect`
- **Purpose**: Kafka Connect service for connecting Kafka with external systems.
- **Image**: `ccms-application-kafka-connect`
- **Port**: 8083
- **Connectors**:
    - Elasticsearch Sink Connector
  
#### `ccm-kafka-ui`
- **Purpose**: Web UI for managing Kafka topics and consumer groups.
- **Image**: `provectuslabs/kafka-ui:latest`
- **Port**: 8090
- **Access URL**: [http://localhost:8090](http://localhost:8090)

### 2.3. Service Discovery

#### `ccm-eureka-server`
- **Purpose**: Service discovery for all microservices.
- **Image**: `ccms-application-eureka-server`
- **Command**: `mvn spring-boot:run`
- **Port**: 8761
- **API Endpoints**:
    - `/eureka/apps/`: List of registered services
- **Authentication**: None or basic authentication (to be specified)

### 2.4. Logging & Monitoring Services

#### `ccm-elasticsearch`
- **Purpose**: Stores logs and provides search functionality.
- **Image**: `docker.elastic.co/elasticsearch/elasticsearch:8.4.3`
- **Port**: 9200 (HTTP), 9300 (Transport)
- **Index Patterns**:
    - `logs-*` - Logs index
- **Response format**: JSON

#### `ccm-elasticsearch-prometheus-exporter`
- **Purpose**: Exports metrics from Elasticsearch to Prometheus.
- **Image**: `bitnami/elasticsearch-exporter`
- **Port**: 9114 , 9108 

#### `ccm-kibana`
- **Purpose**: Visualizes data in Elasticsearch.
- **Image**: `docker.elastic.co/kibana/kibana:8.4.3`
- **Port**: 5601
- **Access URL**: [http://localhost:5601](http://localhost:5601)
- **Dashboards**:
    - Default system dashboard
    - Custom dashboards for various metrics
- **Authentication**: None or OAuth

#### `ccm-prometheus`
- **Purpose**: Metrics collection and monitoring.
- **Image**: `prom/prometheus`
- **Command**: `/bin/prometheus --config.file=/etc/prometheus/prometheus.yml`
- **Port**: 9090
- **Access URL**: [http://localhost:9090](http://localhost:9090)
- **Metrics Endpoints**:
    - `/metrics` - Prometheus-compatible metrics

### 2.5. Monitoring Dashboard

#### `ccm-grafana`
- **Purpose**: Provides visualization of Prometheus metrics.
- **Image**: `grafana/grafana:latest`
- **Port**: 3000 (web UI)
- **Access URL**: [http://localhost:3000](http://localhost:3000)
- **Authentication**: None or OAuth
- **Data Sources**:
    - Prometheus (configured via Grafana UI)

### 2.6. Zookeeper

#### `ccm-zookeeper`
- **Purpose**: Zookeeper for distributed coordination and management of Kafka.
- **Image**: `ccms-application-zookeeper`
- **Port**: 2181

### 2.7. Frontend Service

#### `ccm-frontend`
- **Purpose**: Provides the user interface for interacting with the system.
- **Image**: `ccms-application-frontend`
- **Port**: 3000
- **Access URL**: [http://localhost:3000](http://localhost:3000)
- **Authentication**: OAuth2, JWT (Token-based Authentication)

---

## 3. Service Interaction & Communication

- **Backend and Frontend Communication**:
    - The frontend communicates with backend services (`ccm-backend` and `ccm-ccmscustomer`) via REST APIs.
    - All API responses are in JSON format.
  
- **Kafka Messaging**:
    - The backend and customer services interact with Kafka (`ccm-kafka-broker`) to send messages/logs.
    - Kafka Connect (`ccm-kafka-connect`) facilitates the integration with external systems.

- **Logging & Monitoring Integration**:
    - Logs are pushed to Elasticsearch, which can be queried via Kibana for real-time insights.
    - Metrics from various services are collected by Prometheus and visualized using Grafana.

- **Service Discovery**:
    - All services register with Eureka (`ccm-eureka-server`) for dynamic service discovery.

---

## 4. Security

- **Authentication**:
    - API services such as `ccm-backend`, `ccm-ccmscustomer`, and others may require OAuth2 or Basic Auth for access.
    - JWT Tokens may be used for authentication.
    - No authentication on Grafana and Kibana (consider securing them in production).

- **Encryption**:
    - Data exchanged between services (such as REST API calls) should be encrypted using HTTPS.
    - Kafka communication should be secured with SSL.

---

## 5. Service Health Checks

- **Healthy Services**:
    - All services are running and healthy.
    - Regular health checks are performed via Docker's `docker ps` and container logs.

---

## 6. Troubleshooting & Debugging

- **Logs**:
    - Elasticsearch provides the primary storage for logs. These logs can be accessed via Kibana.
    - Prometheus and Grafana provide metrics for system health.

---

## 7. Conclusion

This document serves as a contract for the interaction between the various services in the microservices architecture. The services are designed to be loosely coupled, and each service is responsible for a specific domain within the system. Monitoring, logging, and service discovery play key roles in ensuring the system operates smoothly and is scalable.



