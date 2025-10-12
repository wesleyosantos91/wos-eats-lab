# ☕ Microservices Cloud-Native Roadmap (Java 25 + Spring Boot 3.5.6)

![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3.5.6-6DB33F?logo=springboot)
![Kubernetes](https://img.shields.io/badge/Kubernetes-CloudNative-326CE5?logo=kubernetes)
![ArgoCD](https://img.shields.io/badge/GitOps-ArgoCD-EF7B4D?logo=argo)
![Prometheus](https://img.shields.io/badge/Observability-Prometheus-E6522C?logo=prometheus)
![Grafana](https://img.shields.io/badge/Dashboards-Grafana-F46800?logo=grafana)
![Terraform](https://img.shields.io/badge/IaC-Terraform-7B42BC?logo=terraform)
![AWS](https://img.shields.io/badge/Cloud-AWS-FF9900?logo=amazon-aws)
![SQS](https://img.shields.io/badge/Messaging-SQS%2FSNS-232F3E?logo=amazonaws)

---

> 📘 **Objetivo:** Este roadmap descreve a jornada prática e teórica para dominar **microsserviços cloud-native corporativos** com **Java 25 + Spring Boot 3.5.6**, aplicando **DDD**, **observabilidade 360°**, **GitOps**, **resiliência**, **mensageria** e **Kubernetes avançado**.

---

## ☁️ Contexto Arquitetural

Este ecossistema segue o paradigma **Container-First & Cloud-Native**, com ênfase em:

- **Kubernetes Avançado:** Pods, Deployments, HPA, KEDA, CRDs, Operators, Gateway API, SecurityContext, Service Mesh (Istio), Progressive Delivery (Argo Rollouts) e Network Policies.  
- **Mensageria corporativa (AWS-native):**
  - **Amazon SNS:** fan-out, event broadcasting, versionamento de tópicos.  
  - **Amazon SQS:** filas Standard e FIFO, DLQ, retries com backoff exponencial, idempotência e deduplicação.  
  - **Integração híbrida:** SNS → SQS → Lambda (Java/Quarkus) com fallback e DLQ observável.  
- **Observabilidade & Telemetria Unificada:** tracing distribuído (OTel), logs estruturados (JSON), métricas RED/USE/VALET, dashboards unificados.  
- **Infraestrutura como Código:** Terraform + Argo CD para provisionamento declarativo multi-cloud (EKS/AKS).  
- **Boas práticas corporativas:** 12-Factor Apps, DDD + Hexagonal Architecture, CI/CD, SLOs e rollback automático baseado em métricas.

---

## 🗓️ Cronograma de Sprints

### 🏁 Sprint 0 — Bootstrap & Plataforma Base
**Desafio:** Provisionar o ecossistema local completo.

- **Infra:** kind + Argo CD + Helm; Kong + Istio; Prometheus/Grafana + Loki/Tempo + KEDA; blackbox-exporter.  
- **Stack Local:** Docker Compose com Postgres, Redpanda, MinIO, Keycloak, LocalStack Pro, OTel Collector.  
- **IaC:** Terraform para S3, SNS, SQS, Lambda, API Gateway.  
- **Automação:** Makefile idempotente (`make all`).  
- **DoD:** `/hello` responde via Kong; probe_success no Prometheus.

---

### 🥗 Sprint 1 — Catalog Service + API Gateway
**Desafio:** Primeiro microserviço real via Gateway.

- **Stack:** Spring Boot + SpringDoc OpenAPI (Code-First).  
- **Arquitetura:** Controller → Service → Repository (domain-driven).  
- **Banco:** Postgres (Spring Data JPA).  
- **Storage:** MinIO.  
- **Gateway:** Kong ingress + Istio sidecar.  
- **Observabilidade:** Micrometer + OTel tracing.  
- **Testes:** JUnit 5, Testcontainers, REST-assured, Spectral lint.  
- **DoD:** `/actuator/health` OK via Kong; dashboard RED visível.

---

### 🔑 Sprint 2 — Customer/Auth Service + JWT/OIDC + RBAC
**Desafio:** Autenticação e autorização centralizadas com identidade federada.

- **Stack:** Keycloak + Spring Security + Kong OIDC plugin.  
- **Padrões:** OIDC (Authorization Code / Client Credentials), JWT (RS256), RBAC, mTLS permissive.  
- **Integração:** Kong ↔ Keycloak (realm `dev`, clients `kong`, `customer-service`).  
- **Propagação:** Headers `X-Userinfo` e `X-Access-Token` para upstream.  
- **Segurança:** Roles e scopes aplicados a endpoints; propagação de claims.  
- **Observabilidade:** métricas 2xx/401/403, logs de autenticação no Loki, tracing de login no Tempo/Jaeger.  
- **Testes:** tokens inválidos, expirados e roles incorretas.  
- **DoD:** `/hello` protegido; login via Keycloak; RBAC e auditoria habilitados.

---

### ⚡ Sprint 2A — Serverless Edge + Fan-out
**Desafio:** Padrões serverless e fan-out SNS/SQS.

- **Stack:** AWS Lambda (Quarkus), API Gateway, SNS, SQS, S3, LocalStack Pro.  
- **Padrões:** fan-out, DLQ, idempotência, retries.  
- **IaC:** Terraform para filas e lambdas.  
- **DoD:** mensagem SNS → 3 filas SQS → Lambda processa → sucesso.

---

### 🧾 Sprint 3 — Order Service + Kafka (Redpanda) + Outbox
**Desafio:** Publicar eventos confiáveis e consistentes.

- **Stack:** Spring Boot + Redpanda (Kafka) + Spring Kafka.  
- **Padrões:** Transactional Outbox, Idempotent Consumer, Event-Carried State Transfer.  
- **Testes:** atomicidade e consistência DB-evento.  
- **Observabilidade:** métricas de lag e throughput.  
- **DoD:** evento publicado sem duplicação.

---

### ⚡ Sprint 3B — Kafka Deep Dive
**Desafio:** Domínio avançado de mensageria e operação.

- **Tópicos:** Partições, offsets, rebalance, EoS, DLQ, schema evolution (Avro/JSON Schema).  
- **Ferramentas:** Redpanda Console, Schema Registry, k6/Gatling.  
- **Escalabilidade:** KEDA autoscaling baseado em lag.  
- **DoD:** replay seguro e autoscale validado.

---

### 💳 Sprint 4 — Payment Service + Saga Orchestration
**Desafio:** Orquestração de pagamentos com consistência distribuída.

- **Padrões:** Saga (orchestrated), Circuit Breaker, Retry, Timeout, Bulkhead.  
- **Stack:** Spring Boot + Resilience4j.  
- **Testes:** fluxos felizes e compensatórios.  
- **Observabilidade:** tracing completo e métricas resilientes.  
- **DoD:** rollback automático validado.

---

### 🚚 Sprint 5 — Delivery & Notification
**Desafio:** Entregas e notificações multicanal.

- **Serviços:** `delivery-service` e `notification-service`.  
- **Padrões:** Event Choreography, Fan-out SNS/SQS, DLQ.  
- **Testes:** reprocessamento, fallback.  
- **DoD:** DLQ monitorado; fan-out validado.

---

### 📊 Sprint 6 — Observabilidade & Autoscale (RED, USE, VALET & Golden Signals)
**Desafio:** Implementar observabilidade completa e visão centralizada.

- **Stack:** Micrometer + Prometheus + Grafana + Loki + Tempo + Alertmanager.  
- **Modelos de Métricas:**  
  - 🟥 **RED** → *Rate, Errors, Duration* (serviços e APIs)  
  - 🟦 **USE** → *Utilization, Saturation, Errors* (recursos infra)  
  - 🟩 **VALET** → *Value, Availability, Latency, Errors, Throughput* (métricas de negócio)  
  - 🟨 **Golden Signals** → *Latency, Traffic, Errors, Saturation* (SRE Core)  
- **Dashboards:**  
  - *Single Pane of Glass* — visão consolidada de APIs, filas (SQS/SNS), Kafka, storage e métricas de negócio.  
  - Drill-down por serviço e namespace.  
- **Escalabilidade:** HPA + KEDA baseados em consumo, lag e latency.  
- **Alertas:** thresholds e SLOs definidos no Prometheus.  
- **DoD:** dashboards RED/USE/VALET visíveis; autoscale validado.

---

### 🚀 Sprint 7 — Progressive Delivery (Argo Rollouts)
**Desafio:** Deploy seguro e automatizado com rollback.

- **Estratégias:** Canary, Blue-Green, AnalysisTemplate (Prometheus).  
- **Testes:** smoke + `k6`.  
- **DoD:** rollback automático validado.

---

### ☁️ Sprint 8 — CD Corporativo & Multi-cloud
**Desafio:** Pipelines enterprise e suporte multi-cloud.

- **Stack:** GitHub Actions + Argo CD + Terraform.  
- **Recursos:** cert-manager, ExternalDNS, Karpenter, overlays EKS/AKS.  
- **DoD:** PR → dev, tag → stg, approval → prod.

---

### ☠️ Sprint 9 — Chaos & Hardening
**Desafio:** Testar resiliência e reforçar segurança.

- **Ferramentas:** LitmusChaos, Kyverno, Vault/ASM.  
- **Padrões:** chaos injection, OPA policies, secrets mgmt.  
- **DoD:** rollback por SLO validado.

---

### 🔀 Sprint 10 — Istio Avançado (Mesh)
**Desafio:** Tráfego leste-oeste e governança mesh.

- **Recursos:** VirtualServices, DestinationRules, PeerAuth, mTLS STRICT.  
- **Padrões:** Retries, Circuit Breaker, Shadow Deployment, A/B Testing.  
- **DoD:** tráfego shadow validado; métricas comparativas visíveis.

---

### 📚 Sprint 11 — Data Patterns Avançados
**Desafio:** Padrões de dados corporativos.

- **Stack:** Debezium + Kafka + Redis/Elastic.  
- **Padrões:** CQRS, Event Sourcing, Outbox + CDC.  
- **DoD:** replicação CDC validada; schema compatível.

---

### 🔒 Sprint 12 — Security Deep Dive
**Desafio:** Segurança de supply chain e feature flags.

- **Ferramentas:** Unleash, Cosign, Syft/Grype, Trivy, CodeQL.  
- **Padrões:** SBOM, Dark Launch, imagens assinadas.  
- **DoD:** validação completa de segurança na pipeline.

---

### 🧪 Sprint 13 — SRE Avançado
**Desafio:** Confiabilidade contínua.

- **Padrões:** SLIs, SLOs, SLAs, eBPF observability.  
- **Ferramentas:** Grafana SLO, Pixie, Cilium Hubble.  
- **DoD:** SLOs versionados; runbooks e drills realizados.

---

### 🧭 Sprint 14 — DevEx & Governança
**Desafio:** Experiência de desenvolvedor e governança.

- **Ferramentas:** Backstage (Service Catalog), ADRs, C4 Model, templates corporativos.  
- **DoD:** novos serviços com observabilidade, testes e CD nativo.

---

## 🔍 Checklist Transversal
✔️ Config via env (12-Factor)  
✔️ Logs estruturados (JSON)  
✔️ Probes, métricas e tracing OTel  
✔️ Dashboards RED/USE/VALET  
✔️ Monitoramento SQS/SNS + Kafka  
✔️ Autenticação OIDC (Keycloak ↔ Kong)  
✔️ RBAC por roles e claims  
✔️ Logs de autenticação e auditoria  
✔️ Testes (unit, integração, contrato, E2E, performance)  
✔️ Helm + Argo Rollouts  
✔️ Rollback automatizado (AnalysisTemplate)  
✔️ README + ADR + Diagramas C4  

---

## 📚 Referências
- 📘 *Building Microservices* — Sam Newman  
- 📗 *Implementing Domain-Driven Design* — Vaughn Vernon  
- 🧾 *Site Reliability Engineering* — Google  
- ☁️ [Spring Boot Docs](https://spring.io/projects/spring-boot)  
- ⚙️ [Argo CD](https://argo-cd.readthedocs.io) · [Istio](https://istio.io) · [KEDA](https://keda.sh)  
- 🔑 [Keycloak Docs](https://www.keycloak.org/docs/latest/)  
- 🌐 [Kong OIDC Plugin](https://docs.konghq.com/hub/kong-inc/openid-connect/)  
- 🐳 [AWS SQS/SNS SDK](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/home.html)

---

## ✅ Resultado Esperado
Ao final:
- Aplicar **autenticação OIDC corporativa** com Keycloak e Kong.  
- Proteger APIs via JWT e SSO federado.  
- Propagar identidade entre serviços.  
- Auditar e observar métricas de login e autorização.  
- Entregar uma plataforma **cloud-native, resiliente e segura**.  

> 💡 **Dica:** Cada sprint gera um módulo independente (ex: `catalog-service`, `customer-service`), com ADRs, diagramas e README próprios.