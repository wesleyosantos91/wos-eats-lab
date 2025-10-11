# 🐹 Microservices Cloud-Native Roadmap (Golang Edition)

![Go](https://img.shields.io/badge/Go-1.23+-00ADD8?logo=go&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-CloudNative-326CE5?logo=kubernetes)
![ArgoCD](https://img.shields.io/badge/GitOps-ArgoCD-EF7B4D?logo=argo)
![Prometheus](https://img.shields.io/badge/Observability-Prometheus-E6522C?logo=prometheus)
![Grafana](https://img.shields.io/badge/Dashboards-Grafana-F46800?logo=grafana)
![Terraform](https://img.shields.io/badge/IaC-Terraform-7B42BC?logo=terraform)
![AWS](https://img.shields.io/badge/Cloud-AWS-FF9900?logo=amazon-aws)
![SQS](https://img.shields.io/badge/Messaging-SQS%2FSNS-232F3E?logo=amazonaws)
![Docker](https://img.shields.io/badge/Containers-Docker-2496ED?logo=docker)

---

> 📘 **Objetivo:** Este repositório descreve o **roadmap completo de estudo e prática** para dominar **microsserviços cloud-native com Go**, aplicando fundamentos de **Kubernetes, Observabilidade, Mensageria (SNS/SQS/Kafka)** e **GitOps**, em um ambiente corporativo de alta disponibilidade e governança.

---

## ☁️ Contexto Arquitetural

Este ecossistema é **100% container-first e Kubernetes-native**, com foco em:

- **Kubernetes Avançado:** Pods, Deployments, Services, HPA, KEDA, CRDs, Operators, Security Context, Service Mesh (Linkerd/Istio), Gateway API e Progressive Delivery (Argo Rollouts).  
- **Mensageria corporativa (AWS-native):**
  - **Amazon SNS:** fan-out, event broadcasting, topic versioning.  
  - **Amazon SQS:** filas padrão e FIFO, DLQ, backoff exponencial, retries e idempotência.  
  - **Integração híbrida:** SNS → SQS → Lambda (Go), com fallback e DLQ monitorado.  
- **Observabilidade 360°:** logs estruturados, tracing, métricas e dashboards centralizados (“single pane of glass”).  
- **Infraestrutura as Code:** Terraform + Argo CD para pipelines declarativos e multi-cloud overlays.  
- **12-Factor App e DDD/Hexagonal Architecture:** configuração via env, acoplamento fraco, alta testabilidade.  

---

## 🗓️ Cronograma de Sprints

### 🏁 Sprint 0 — Bootstrap & Plataforma Base
**Desafio:** Subir o cluster local e provisionar a base de plataforma.

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

### 📊 Sprint 6 — Observabilidade & Autoscale (RED, USE, VALET & Golden Signals)
**Desafio:** Visibilidade completa, autoescalonamento e centralização de monitoramento.

- **Stack:** Prometheus, Grafana, Loki, Tempo, Alertmanager, Tempo, OTel Collector.  
- **Modelos de Métricas:**  
  - 🟥 **RED** → *Rate, Errors, Duration* (foco em APIs)  
  - 🟦 **USE** → *Utilization, Saturation, Errors* (foco em recursos)  
  - 🟩 **VALET** → *Value, Availability, Latency, Errors, Throughput* (foco em negócio)  
  - 🟨 **Golden Signals** → *Latency, Traffic, Errors, Saturation* (SRE Core)  
- **Dashboards:**  
  - “Single Pane of Glass” — visão unificada de serviços, filas (SQS/SNS), tópicos Kafka e métricas de negócio.  
  - Drill-down para APIs, Workers e DB.  
- **Autoscaling:** HPA + KEDA baseados em métricas de consumo e lag.  
- **Alertas:** SLOs e thresholds configurados no Prometheus.  
- **DoD:** painéis RED/USE/VALET ativos e autoscale validado.

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
✔️ Métricas RED/USE/VALET + tracing  
✔️ Monitoramento SQS/SNS + Kafka + DB  
✔️ Testes (unit, integração, contrato, e2e)  
✔️ Deploy Helm + Argo  
✔️ Rollback automatizado (AnalysisTemplate)  
✔️ README + ADRs + Diagramas C4  

---

## 📚 Referências
- 📘 [Go Patterns](https://github.com/tmrts/go-patterns)  
- 🏗️ [Microservices.io](https://microservices.io) — Chris Richardson  
- ☁️ [CNCF Landscape](https://landscape.cncf.io)  
- 🔎 [OpenTelemetry for Go](https://opentelemetry.io/docs/instrumentation/go/)  
- 🐳 [AWS SDK v2 for Go](https://aws.github.io/aws-sdk-go-v2/docs/)  
- ⚙️ [KEDA Scalers](https://keda.sh/docs/latest/scalers/aws-sqs-queue/)  

---

## ✅ Resultado Esperado
Ao final:
- Aplicar **todos os patterns essenciais** de microsserviços em Go.  
- Dominar **Kubernetes, Kafka/Redpanda, SNS/SQS, observabilidade e GitOps**.  
- Entregar um ecossistema **cloud-native corporativo, multi-cloud, resiliente e mensurável**.  
- Estar preparado para atuar como **Staff/Principal Engineer em Go Microservices**.

---

> 💡 **Dica:** Cada sprint deve gerar um repositório modular (`catalog-service`, `order-service`, etc.), com README e diagramas próprios.  
> Use ADRs curtos (Architecture Decision Records) para justificar decisões técnicas.