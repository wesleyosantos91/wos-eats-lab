# Release 0.0.0 - Release Fundacional

## Informações da Release
- **Tag**: 0.0.0
- **Branch Alvo**: main
- **Commit SHA**: c4b00be4011fb867dd9cbf7629d090afb4d42a1a
- **Data de Release**: 12/10/2025
- **Tipo**: Release Inicial

## 🎯 Visão Geral

Esta é a **release fundacional** do projeto WOS Eats Lab - uma plataforma abrangente de aprendizado de microsserviços Cloud-Native. Esta release estabelece a infraestrutura completa e documentação para um ecossistema de microsserviços em nível de produção, abrangendo três pilares tecnológicos: **Go**, **Java (Spring Boot)** e **Python (Engenharia de Dados)**.

## 🚀 O que está Incluído

### Roadmap de Documentação Completo
- **Roadmap Unificado** (`README.md`) - Guia abrangente consolidando todos os pilares tecnológicos
- **Microsserviços Go** (`README-GO.md`) - 15 sprints cobrindo desenvolvimento cloud-native em Go
- **Microsserviços Java** (`README-JAVA.md`) - 15 sprints com Spring Boot 3.5.6
- **Engenharia de Dados Python** (`README-PYTHON.md`) - 6 sprints para camada de dados com serviços AWS

### Sprint 0 - Bootstrap & Infraestrutura de Plataforma
Ambiente de desenvolvimento local completo com ferramental de nível empresarial:

#### Serviços Core (Docker Compose)
- **PostgreSQL 17** - Suporte multi-banco com scripts de inicialização
- **Kong Gateway 3.8** - API Gateway com suporte a plugin OIDC
- **Redpanda 24.2** - Message broker compatível com Kafka com console de gerenciamento
- **MinIO** - Object storage compatível com S3
- **Keycloak 26** - Gerenciamento de Identidade e Acesso (IAM)
- **LocalStack Pro** - Emulação de serviços AWS para desenvolvimento local

#### Stack de Observabilidade (Cobertura 360°)
- **OpenTelemetry Collector** - Receptor OTLP para traces, métricas e logs
- **Prometheus** - Coleta de métricas e alertas
- **Grafana** - Dashboards unificados e visualização
- **Loki** - Agregação e consulta de logs
- **Tempo** - Backend de rastreamento distribuído
- **Blackbox Exporter** - Monitoramento de saúde de endpoints

#### Kubernetes & GitOps
- **kind** - Configuração de cluster Kubernetes local
- **ArgoCD** - Entrega contínua GitOps
- **Helm** - Gerenciamento de pacotes
- Aplicação "Hello World" de exemplo para validação

#### Infraestrutura como Código
- **Terraform** - Configuração de provider AWS e LocalStack
- Base para provisionamento de S3, SNS, SQS, Lambda e API Gateway

#### Automação
- **Makefile Abrangente** - Mais de 30 targets para:
  - Ciclo de vida do ambiente (`setup`, `teardown`, `restart`)
  - Operações Docker (`docker-up`, `docker-down`, `docker-logs`)
  - Gerenciamento Kubernetes (`k8s-up`, `k8s-down`, `k8s-status`)
  - Operações ArgoCD (`argocd-install`, `argocd-login`, `argocd-ui`)
  - Workflows Terraform (`terraform-init`, `terraform-plan`, `terraform-apply`)
  - Validação e verificações de saúde (`validate-all`, `health-check`)
  - Utilitários de limpeza (`clean-all`)

## 🎓 Roadmap de Aprendizado

### Track de Microsserviços (Sprints 0-14)
Implementações em Go e Java cobrindo:
1. **Sprint 0**: Bootstrap & Plataforma Base ✅ (Esta Release)
2. **Sprint 1**: Catalog Service + API Gateway
3. **Sprint 2**: Customer/Auth + OIDC/JWT + RBAC (Kong ↔ Keycloak)
4. **Sprint 2A**: Serverless Edge + Fan-out (SNS/SQS/Lambda)
5. **Sprint 3**: Order Service + Kafka (Redpanda) + Padrão Outbox
6. **Sprint 3B**: Kafka Deep Dive
7. **Sprint 4**: Payment Service + Orquestração Saga
8. **Sprint 5**: Serviços de Entrega & Notificação
9. **Sprint 6**: Observabilidade & Autoscale (RED/USE/VALET)
10. **Sprint 7**: Progressive Delivery (Argo Rollouts)
11. **Sprint 8**: Pipeline CD & Multi-cloud
12. **Sprint 9**: Engenharia de Caos & Hardening
13. **Sprint 10**: Service Mesh Avançado (Istio/Linkerd)
14. **Sprint 11-14**: Padrões de Dados, Segurança, SRE, DevEx

### Track de Engenharia de Dados (Sprints 15-20)
Camada de dados baseada em Python com AWS:
1. **Sprint 15**: Fundamentos Python + Essenciais de Engenharia de Dados
2. **Sprint 16**: Big Data & ETL (Glue + PySpark + Catalog)
3. **Sprint 17**: Orquestração (Step Functions + EventBridge)
4. **Sprint 18**: Processamento Serverless de Dados (Lambda + Athena + FastAPI)
5. **Sprint 19**: Lakehouse & Analytics (Glue Catalog + Athena + Redshift)
6. **Sprint 20**: DataOps, Observabilidade & Governança

## 🏗️ Destaques da Arquitetura

### Princípios de Design
✅ **12-Factor App** - Configuração via variáveis de ambiente  
✅ **Cloud-Native** - Container-first, Kubernetes-native  
✅ **DDD/Arquitetura Hexagonal** - Design orientado a domínio  
✅ **Event-Driven** - Padrões de comunicação assíncrona  
✅ **GitOps** - Infraestrutura e deploys declarativos  
✅ **Observabilidade 360°** - Métricas, logs e traces completos  

### Stack Tecnológica
- **Linguagens**: Go 1.23+, Java 25, Python 3.12
- **Frameworks**: Spring Boot 3.5.6, go-chi/gin-gonic, FastAPI
- **Orquestração de Containers**: Kubernetes, Helm, ArgoCD
- **API Gateway**: Kong (com suporte OIDC)
- **Service Mesh**: Istio/Linkerd
- **Mensageria**: Kafka/Redpanda, AWS SNS/SQS
- **Bancos de Dados**: PostgreSQL
- **Storage**: MinIO (compatível com S3)
- **Identidade**: Keycloak (OIDC/JWT)
- **Observabilidade**: Prometheus, Grafana, Loki, Tempo, OpenTelemetry
- **Cloud**: AWS (LocalStack para desenvolvimento local)
- **IaC**: Terraform
- **Processamento de Dados**: AWS Glue, Athena, Redshift, Step Functions

### Padrões Cobertos
- Transactional Outbox
- Event-Carried State Transfer
- Orquestração & Coreografia Saga
- Circuit Breaker, Retry, Timeout, Bulkhead
- CQRS (Command Query Responsibility Segregation)
- CDC (Change Data Capture)
- Event Sourcing
- Padrão API Gateway
- Service Mesh
- Progressive Delivery (Canary, Blue-Green)
- Dark Launch & Feature Flags

## 📦 Início Rápido

### Pré-requisitos
- Docker & Docker Compose
- kind (Kubernetes in Docker)
- kubectl
- Terraform
- Make

### Instalação
```bash
# Clone o repositório
git clone https://github.com/wesleyosantos91/wos-eats-lab.git
cd wos-eats-lab

# Configure o ambiente completo
cd platform-infra
make setup

# Verifique se os serviços estão rodando
make status
make health-check

# Acesse os serviços
# - Grafana: http://localhost:3001 (admin/admin)
# - Prometheus: http://localhost:9090
# - Kong Gateway: http://localhost:8000
# - Redpanda Console: http://localhost:8080
# - MinIO Console: http://localhost:9001
# - Keycloak: http://localhost:8081
```

### Validação
```bash
# Teste o endpoint de saúde do Kong Gateway
curl http://localhost:8000/healthz

# Verifique todos os serviços
make validate-all

# Visualize os logs
make docker-logs

# Acesse os dashboards Grafana
open http://localhost:3001
```

## 📊 Métricas & Estatísticas

### Infraestrutura
- **28 arquivos** criados/modificados
- **~2.047 linhas** de configuração de infraestrutura
- **12 serviços containerizados** no Docker Compose
- **Mais de 30 targets do Makefile** para automação
- **6 componentes de observabilidade** totalmente integrados

### Documentação
- **4 arquivos README abrangentes** (mais de 700 linhas)
- **Roadmap de 20 sprints** cobrindo 3 pilares tecnológicos
- **Guia de plataforma de 487 linhas** com troubleshooting
- **Changelog completo** seguindo versionamento semântico

## ✅ Definition of Done (Sprint 0)

Todos os objetivos do Sprint 0 foram concluídos:
- ✅ Ambiente de desenvolvimento local completo configurado
- ✅ Stack Docker Compose operacional com mais de 12 serviços
- ✅ Cluster Kubernetes (kind) provisionado com ingress
- ✅ Plataforma GitOps ArgoCD instalada
- ✅ Stack completa de observabilidade (Prometheus, Grafana, Loki, Tempo)
- ✅ API Gateway (Kong) com suporte a plugin OIDC
- ✅ Message broker (Redpanda) com console de gerenciamento
- ✅ Object storage (MinIO) compatível com S3
- ✅ Gerenciamento de identidade (Keycloak) configurado
- ✅ Emulação AWS (LocalStack Pro) pronta
- ✅ Automação de infraestrutura via Makefile
- ✅ Documentação abrangente (mais de 1.500 linhas)
- ✅ Base Terraform para provisionamento cloud
- ✅ Endpoint `/healthz` respondendo via Kong Gateway

## 🎯 Próximos Passos

### Sprint 1 - Catalog Service
- Implementar primeiro microsserviço (Go ou Java)
- Suporte a REST API + gRPC
- Integração PostgreSQL
- Instrumentação OpenTelemetry
- Deploy de Helm chart via ArgoCD
- Testes de integração com Testcontainers

### Melhorias Contínuas
- Adicionar dashboards de saúde ao Grafana
- Configurar alertas Prometheus
- Configurar projetos ArgoCD
- Implementar módulos Terraform
- Criar templates de serviço

## 🔗 Recursos

### Documentação
- [README Principal](./README.md) - Roadmap unificado
- [Roadmap Go](./README-GO.md) - Guia de microsserviços Go
- [Roadmap Java](./README-JAVA.md) - Guia Spring Boot
- [Roadmap Python](./README-PYTHON.md) - Guia de engenharia de dados
- [Infraestrutura de Plataforma](./platform-infra/README.md) - Guia de setup
- [Changelog](./CHANGELOG.md) - Histórico de versões

### Referências
- [Microservices.io](https://microservices.io/) - Catálogo de padrões
- [12-Factor App](https://12factor.net/) - Metodologia
- [CNCF Landscape](https://landscape.cncf.io/) - Tecnologias cloud-native
- [Spring Boot Docs](https://spring.io/projects/spring-boot) - Framework Java
- [OpenTelemetry](https://opentelemetry.io/) - Padrão de observabilidade
- [ArgoCD](https://argo-cd.readthedocs.io/) - Ferramenta GitOps

## 🙏 Créditos

Este projeto consolida as melhores práticas de:
- "Building Microservices" por Sam Newman
- "Implementing Domain-Driven Design" por Vaughn Vernon
- "Site Reliability Engineering" por Google
- CNCF Cloud Native Patterns
- AWS Well-Architected Framework

## 📝 Notas

- Todos os serviços configurados para desenvolvimento local
- Observabilidade pronta para produção desde o primeiro dia
- Arquitetura pronta para multi-cloud
- Setup com comando único: `make setup`
- Teardown completo: `make teardown`

---

**Changelog Completo**: Release inicial  
**Público-Alvo**: Engenheiros Cloud-Native, SREs, Engenheiros de Plataforma  
**Licença**: Não especificada  
**Mantenedor**: @wesleyosantos91
