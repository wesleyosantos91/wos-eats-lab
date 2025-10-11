# 🐹 Microservices Cloud-Native Roadmap (Golang Edition)

![Go](https://img.shields.io/badge/Go-1.23+-00ADD8?logo=go&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-CloudNative-326CE5?logo=kubernetes)
![ArgoCD](https://img.shields.io/badge/GitOps-ArgoCD-EF7B4D?logo=argo)
![Prometheus](https://img.shields.io/badge/Observability-Prometheus-E6522C?logo=prometheus)
![Grafana](https://img.shields.io/badge/Dashboards-Grafana-F46800?logo=grafana)
![Terraform](https://img.shields.io/badge/IaC-Terraform-7B42BC?logo=terraform)
![Docker](https://img.shields.io/badge/Containers-Docker-2496ED?logo=docker)

---

> 📘 **Objetivo:** Este repositório documenta o **cronograma de estudos e práticas** para construir um ecossistema de **microsserviços modernos** com **Golang**, orientado a padrões corporativos **cloud-native**, observabilidade, GitOps e boas práticas de engenharia de software.

---

## 🗓️ Cronograma de Sprints

### 🏁 Sprint 0 — Bootstrap & Plataforma Base
**Desafio:** Subir o cluster local e provisionar a base da plataforma.

- **Infra:** kind + Argo CD + Helm; Kong + Linkerd; Prometheus/Grafana + Loki/Tempo + KEDA.  
- **Stack Local:** MinIO, LocalStack Pro, Redpanda, Postgres, Keycloak.  
- **IaC:** Terraform (S3, SQS, SNS, Lambda, API Gateway).  
- **Dev Tools:** `Taskfile.yml`, Tilt, GoReleaser.  
- **DoD:** `task up` inicializa todo o ambiente e `/healthz` responde via Kong Gateway.

---

### 🥗 Sprint 1 — Catalog Service + Gateway
**Desafio:** Primeiro microserviço publicado via API Gateway.

- **API:** REST + gRPC (`go-chi` / `gin-gonic` + `buf.build`).  
- **Arquitetura:** Clean Architecture (domain, usecase, infra).  
- **Banco:** Postgres (GORM ou SQLC).  
- **Observabilidade:** OpenTelemetry + Prometheus metrics.  
- **Testes:** `testify`, Testcontainers-Go, REST-assured (Go).  
- **Deploy:** Helm chart + Argo CD rollout.

---

### 🔑 Sprint 2 — Customer/Auth + JWT
**Desafio:** Autenticação/autorização corporativa.

- **Auth:** Keycloak + `go-keycloak`.  
- **Padrões:** OIDC, JWT, RBAC, mTLS permissive (Linkerd/Istio).  
- **Gateway:** Kong com plugin JWT.  
- **Testes:** tokens inválidos, expirados, roles incorretas.  
- **DoD:** endpoints protegidos exigem JWT.

---

### ⚡ Sprint 2A — Serverless Edge + Fan-out
**Desafio:** Integrar padrões serverless com fan-out.

- **Infra:** AWS LocalStack Pro.  
- **Padrões:** SNS → SQS → Lambda (Go).  
- **Libs:** AWS SDK v2 for Go.  
- **Testes:** integração LocalStack, DLQ, idempotência.

---

### 🧾 Sprint 3 — Order Service + Kafka (Redpanda) + Outbox
**Desafio:** Criar pedidos e publicar eventos confiáveis.

- **Mensageria:** Redpanda (Kafka) + Sarama client.  
- **Padrões:** Transactional Outbox, Event-Carried State, Idempotent Consumer.  
- **Testes:** consistência DB + evento; contratos JSON Schema.  
- **Observabilidade:** lag e throughput de tópicos.

---

### ⚡ Sprint 3B — Kafka Deep Dive
**Desafio:** Aprofundar no funcionamento de Kafka/Redpanda.

- **Tópicos:** Exactly-Once, Partições, DLQ, Schema Evolution.  
- **Ferramentas:** Redpanda Console, k6, KEDA autoscale.  
- **DoD:** autoscaling validado e replay seguro de eventos.

---

### 💳 Sprint 4 — Payment Service + Saga Orchestration
**Desafio:** Orquestrar pagamentos com consistência distribuída.

- **Padrões:** Saga Orchestration, Circuit Breaker, Retry, Timeout, Bulkhead.  
- **Libs:** `resilience-go`, `retry-go`, `context.WithTimeout`.  
- **Testes:** fluxos compensatórios e fault injection.  
- **DoD:** rollback validado; métricas resilientes.

---

### 🚚 Sprint 5 — Delivery & Notification
**Desafio:** Entregas e notificações multicanal.

- **Serviços:** `delivery-service` e `notification-service`.  
- **Padrões:** Event Choreography, Fan-out SNS/SQS, DLQ.  
- **DoD:** DLQ monitorado e reprocesso automático.

---

### 📊 Sprint 6 — Observabilidade & Autoscale
**Desafio:** Visibilidade completa e autoescalonamento.

- **Stack:** Prometheus, Grafana, Loki, Tempo.  
- **Libs:** `prometheus/client_golang`, `zap`, `otel-go`.  
- **Escalabilidade:** HPA + KEDA.  
- **DoD:** alertas RED/USE configurados; dashboards validados.

---

### 🚀 Sprint 7 — Progressive Delivery (Argo Rollouts)
**Desafio:** Deploy seguro e automatizado.

- **Estratégias:** Canary, Blue-Green, AnalysisTemplate (Prometheus).  
- **Testes:** `k6` ou `vegeta`.  
- **DoD:** rollback automático baseado em métricas.

---

### ☁️ Sprint 8 — CD Corporativo & Multi-cloud
**Desafio:** Pipelines GitOps corporativos e overlays multi-cloud.

- **CI/CD:** GitHub Actions + Argo CD promotion (dev→stg→prod).  
- **Infra:** Terraform (EKS/AKS), cert-manager, ExternalDNS, Karpenter.  
- **Versionamento:** semantic releases via `GoReleaser`.

---

### ☠️ Sprint 9 — Chaos & Hardening
**Desafio:** Testes de resiliência e endurecimento de segurança.

- **Ferramentas:** LitmusChaos, Kyverno, Vault.  
- **Padrões:** Chaos Injection, Secrets Mgmt, Rollback by SLO.  
- **DoD:** caos controlado, rollback seguro.

---

### 🔀 Sprint 10 — Service Mesh Avançado
**Desafio:** Explorar tráfego leste-oeste com Mesh.

- **Ferramentas:** Linkerd/Istio.  
- **Padrões:** Retries, Circuit Breaker, Shadow Deployment, mTLS STRICT.  
- **DoD:** shadow test validado; segurança mTLS total.

---

### 📚 Sprint 11 — Data Patterns Avançados
**Desafio:** Padrões de dados corporativos.

- **Stack:** Debezium + Kafka + Redis/Elastic.  
- **Padrões:** CQRS, Outbox + CDC.  
- **DoD:** replicação CDC validada; schema compatível.

---

### 🔒 Sprint 12 — Security Deep Dive
**Desafio:** Supply chain e feature flags.

- **Ferramentas:** Unleash, Cosign, Trivy, Syft/Grype, CodeQL.  
- **Padrões:** SBOM, Dark Launch, assinaturas de imagem.  
- **DoD:** imagens assinadas e dark-launch validado.

---

### 🧪 Sprint 13 — SRE Avançado
**Desafio:** Confiabilidade e incident response.

- **SLIs/SLOs:** dashboards SLO no Grafana.  
- **Ferramentas:** Pixie, Cilium Hubble.  
- **Runbooks:** drills de incidentes e MTTR medido.

---

### 🧭 Sprint 14 — DevEx & Governança
**Desafio:** Melhorar experiência dev e governança técnica.

- **Ferramentas:** Backstage, ADRs em Markdown, C4 Model.  
- **Templates:** `go init service` com observabilidade e CD padrão.  
- **DoD:** novos serviços nascem prontos com observabilidade e GitOps.

---

## 🔍 Checklist Transversal
✔️ Configuração via env (12-Factor)  
✔️ Logs estruturados (JSON + `zap`)  
✔️ Health/Readiness Probes  
✔️ Métricas + tracing (RED/USE)  
✔️ Testes (unit, integração, contrato, e2e)  
✔️ Deploy Helm + Argo  
✔️ Rollback automatizado (AnalysisTemplate)  
✔️ Documentação: README, ADRs, diagramas C4  

---

## 📚 Referências
- 📘 [Go Patterns](https://github.com/tmrts/go-patterns)  
- 🏗️ [Microservices.io](https://microservices.io) — Chris Richardson  
- ☁️ [CNCF Landscape](https://landscape.cncf.io)  
- 🔎 [OpenTelemetry for Go](https://opentelemetry.io/docs/instrumentation/go/)  
- 🐳 [Docker Official Go Images](https://hub.docker.com/_/golang)  

---

## ✅ Resultado Esperado
Ao final:
- Aplicar **todos os patterns essenciais** de microsserviços em Go.  
- Dominar **Kafka/Redpanda, observabilidade, GitOps e resiliência**.  
- Entregar um ecossistema **cloud-native corporativo, multi-cloud e 12-Factor compliant**.  
- Estar preparado para atuar como **Staff/Principal Engineer em Go Microservices**.

---

> 💡 **Dica:** Cada sprint deve gerar um repositório modular (`catalog-service`, `order-service`, etc.), com README e diagramas próprios.  
> Use ADRs curtos (Architecture Decision Records) para justificar decisões técnicas.