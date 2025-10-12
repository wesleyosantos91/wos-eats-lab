# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.0.0] - 2025-10-12

### Added
- Initial project structure for Cloud-Native Microservices Roadmap
- Complete documentation roadmap for three technology stacks:
  - **README-GO.md**: Golang microservices roadmap (Sprints 0-14)
  - **README-JAVA.md**: Java Spring Boot microservices roadmap (Sprints 0-14)
  - **README-PYTHON.md**: Python Data Engineering roadmap (Sprints 15-20)
  - **README.md**: Unified roadmap consolidating all three stacks

#### Platform Infrastructure (Sprint 0 - Bootstrap & Plataforma Base)
- **Kubernetes Setup**:
  - kind cluster configuration (`platform-infra/k8s/kind/kind-config.yaml`)
  - ArgoCD application definitions for GitOps
  - Hello world app deployment and service for validation
  
- **Docker Compose Stack** (`platform-infra/docker/docker-compose.yml`):
  - PostgreSQL database with multi-database initialization script
  - Kong API Gateway with declarative configuration
  - Redpanda (Kafka-compatible) message broker
  - MinIO object storage (S3-compatible)
  - Keycloak for authentication/authorization
  - LocalStack for AWS service emulation
  - OpenTelemetry Collector for observability
  - Prometheus for metrics collection
  - Grafana for dashboards and visualization
  - Loki for log aggregation
  - Tempo for distributed tracing
  - Blackbox exporter for endpoint monitoring

- **Observability Configuration**:
  - Prometheus scraping configuration with blackbox exporter
  - Grafana datasource provisioning (Prometheus, Loki, Tempo)
  - Grafana dashboard provisioning for blackbox monitoring
  - Loki configuration for log storage and querying
  - Tempo configuration for trace storage
  - OpenTelemetry Collector configuration for OTLP ingestion

- **Infrastructure as Code**:
  - Terraform providers setup (AWS, LocalStack)
  - Terraform variables and outputs structure
  - Foundation for S3, SNS, SQS, Lambda provisioning

- **Automation**:
  - Comprehensive Makefile with 30+ targets for:
    - Environment setup and teardown
    - Docker Compose lifecycle management
    - Kubernetes cluster management
    - Terraform operations
    - Validation and health checks
    - Cleanup utilities
  - Platform infrastructure README with detailed setup instructions

- **Development Environment**:
  - `.gitignore` file with comprehensive exclusions for:
    - IDE configurations (VSCode, IntelliJ, etc.)
    - OS-specific files (macOS, Windows, Linux)
    - Language-specific artifacts (Java, Go, Python, Node.js)
    - Infrastructure state (Terraform, Kubernetes)
    - Build outputs and dependencies
  - `.gitattributes` file for consistent line endings

### Architecture Highlights
- **12-Factor App** principles implementation
- **Cloud-Native** design with container-first approach
- **DDD/Hexagonal Architecture** for service structure
- **Observability 360°**: Metrics (RED/USE/VALET), Logs, Traces
- **GitOps** with ArgoCD for declarative deployments
- **Progressive Delivery** support with Argo Rollouts
- **Multi-cloud** readiness with Terraform
- **Security**: OIDC/JWT, RBAC, mTLS support

### Sprint Roadmap Overview
The project defines a comprehensive 20-sprint roadmap:
- **Sprints 0-14**: Microservices (Go & Java) covering API development, auth, messaging, observability, chaos engineering, and SRE practices
- **Sprints 15-20**: Data Engineering (Python) covering ETL, data lake, serverless processing, and DataOps

### Technology Stack
- **Languages**: Go 1.23+, Java 25, Python 3.12
- **Frameworks**: Spring Boot 3.5.6, go-chi/gin-gonic, FastAPI
- **Container Orchestration**: Kubernetes, Helm, ArgoCD
- **API Gateway**: Kong
- **Service Mesh**: Istio/Linkerd
- **Messaging**: Kafka/Redpanda, AWS SNS/SQS
- **Databases**: PostgreSQL
- **Observability**: Prometheus, Grafana, Loki, Tempo, OpenTelemetry
- **Cloud**: AWS (LocalStack for local development)
- **IaC**: Terraform
- **Data**: AWS Glue, Athena, Redshift, Step Functions

### Definition of Done (Sprint 0)
✅ Complete local development environment  
✅ Docker Compose stack running all platform services  
✅ Kubernetes cluster (kind) provisioned  
✅ ArgoCD installed and configured  
✅ Observability stack operational (Prometheus, Grafana, Loki, Tempo)  
✅ `/healthz` endpoint responding via Kong Gateway  
✅ Infrastructure automation via Makefile  
✅ Documentation complete for all roadmaps

[0.0.0]: https://github.com/wesleyosantos91/wos-eats-lab/releases/tag/0.0.0
