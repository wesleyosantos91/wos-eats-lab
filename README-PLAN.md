# Plano de 20 Sprints — Ecossistema de Microserviços e Dados Cloud-Native

**Stack Técnica:** Go 1.23+ e/ou Java 25 + Spring Boot 3.5.6; Python 3.12 + AWS (Glue/EMR, Glue Catalog, Athena, Redshift, Lambda, Step Functions)  
**Paradigma:** Container-First (12-Factor App), DDD + Arquitetura Hexagonal  
**Infraestrutura:** Kubernetes (kind/EKS), GitOps (Argo CD), Kong (API Gateway) + Istio/Linkerd (Service Mesh), Redpanda (Kafka), SNS/SQS (LocalStack), Terraform (IaC), Glue/Glue Catalog, Athena/Redshift (dados)  
**Qualidade & Resiliência:** Observabilidade 360° (RED/USE/VALET/Golden Signals), Testes (unit/integração/contrato/E2E/sintéticos/performance), Progressive Delivery (Argo Rollouts) e rollback automatizado por métricas

---

## 🏁 Sprint 0 — Bootstrap & Plataforma Base
**Objetivo:** Provisionar todo o ecossistema local para desenvolvimento.

**Entregas Principais:**
- Cluster kind + Argo CD + Helm; Kong + Istio/Linkerd; Prometheus/Grafana + Loki/Tempo + KEDA; blackbox-exporter.
- Docker Compose com Postgres, Redpanda + Console, MinIO, Keycloak, LocalStack, OTel Collector.
- Terraform (LocalStack): S3/SNS/SQS/Lambda/API GW/ALB/NLB.
- Makefile idempotente.

**DoD:** `make all` sobe tudo; probe_success em Prometheus; `/hello` do API GW do LocalStack responde.

---

## 🥗 Sprint 1 — Catalog Service + Gateway
**Objetivo:** Primeiro microserviço em produção interna via Kong.

**Escopo:** `wos-eats-catalog-service` — Recursos: Restaurante, Cozinha, Produto, FotoProduto, FormaPagamento.  
**Arquitetura:** Controller → Service → Repository (domain-driven); DI (Spring) no Java ou DI manual no Go; OpenAPI “in-code” (SpringDoc) no Java.  
**Integrações:** Postgres (JPA no Java; GORM/SQLC no Go), MinIO (foto), métricas/tracing (Micrometer/OTel), Kong (Ingress), Istio sidecar.  
**Padrões:** API Gateway, 12-Factor, health/readiness probes, métricas RED.  
**Testes:** JUnit5/AssertJ, Testcontainers, REST-assured, spectral lint.  
**Observabilidade:** ServiceMonitor + Grafana base.  
**Deploy:** Helm chart + Argo CD app; rolling update.

**DoD:** GET `/actuator/health` ok via Kong; dashboard RED visível; pipeline CI com testes unit, IT, lint e helm lint.

---

## 🔑 Sprint 2 — Customer/Auth Service + JWT
**Objetivo:** Autenticação/autorização central.

**Escopo:** `wos-eats-auth-service` (Usuário, Grupo, Permissão) + Keycloak (OIDC).  
**Integrações:** Postgres, Keycloak realm + clients.  
**Padrões:** OIDC, JWT, RBAC, mTLS permissive.  
**Gateway:** Kong com plugin JWT.  
**Testes:** IT com Keycloak, tokens inválidos/expirados, contrato OAS.  
**Observabilidade:** métricas 2xx/401/403 + logs auditoria.  
**DoD:** endpoints protegidos exigem JWT; dashboards atualizados.

---

## ⚡ Sprint 2A — Serverless Edge + Fan-out (SNS/SQS/Lambda)
**Objetivo:** Exercitar fan-out e webhooks serverless.

**Escopo:** Lambda (Java/Quarkus ou Go), API GW (LocalStack), SNS/SQS/S3.  
**Padrões:** Fan-out SNS→SQS, DLQ, idempotência por message key.  
**Infra:** Terraform para API GW, Lambda e filas.  
**DoD:** Mensagem SNS gera 3 filas; Lambda responde 200; KEDA scaler configurável.

---

## 🧾 Sprint 3 — Order Service + Kafka + Outbox
**Objetivo:** Criar pedidos e garantir publicação de eventos confiáveis.

**Escopo:** `wos-eats-order-service` (Pedido, ItemPedido, StatusPedido).  
**Padrões:** Transactional Outbox, Event-Carried State, Idempotent Consumer.  
**Testes:** IT com Postgres + Kafka, atomicidade, contrato JSON Schema.  
**Observabilidade:** métricas de lag/throughput, tracing saga local.  
**DoD:** pedido criado → evento emitido → sem duplicação.

---

## ⚡ Sprint 3B — Kafka Deep Dive
**Objetivo:** Domínio de operação Kafka/Redpanda.

**Tópicos:** Partições, rebalance, EoS v2, retries/DLQ, KEDA por lag, schema evolution.  
**Ferramentas:** Redpanda Console, k6, rpk, Schema Registry.  
**DoD:** autoscale dinâmico, reprocesso seguro, runbook de replay.

---

## 💳 Sprint 4 — Payment Service + Saga Orchestration
**Objetivo:** Orquestrar pagamento com consistência distribuída.

**Escopo:** `wos-eats-payment-service` — orquestrador in-process.  
**Padrões:** Saga (orchestrated), Circuit Breaker, Retry, Timeout, Bulkhead.  
**Testes:** compensações (estorno), caos leve (delay/abort).  
**DoD:** saga ponta-a-ponta funcional; rollback testado; métricas resilientes.

---

## 🚚 Sprint 5 — Delivery & Notification
**Objetivo:** Simular logística e notificações.

**Serviços:**
- `wos-eats-delivery-service` (entrega).
- `wos-eats-notification-service` (e-mail/SMS/WhatsApp).  
  **Padrões:** Event Choreography, Fan-out SNS→SQS, DLQ.  
  **DoD:** eventos disparam notificações; DLQ monitorado.

---

## 📊 Sprint 6 — Observabilidade & Autoscale
**Objetivo:** Visibilidade + escalabilidade elástica.

**Escopo:** Micrometer/Prometheus, OTel→Tempo, logs JSON→Loki, HPA/KEDA; dashboards “single pane of glass”.  
**Padrões:** RED/USE/VALET, SLOs p95/erro.  
**Ferramentas:** Grafana dashboards, Alertmanager, k6 baseline.  
**DoD:** alertas configurados; autoscale validado.

---

## 🚀 Sprint 7 — Progressive Delivery (Argo Rollouts)
**Objetivo:** Canary/Blue-Green com rollback automático.

**Escopo:** Rollouts (Catalog/Order/Payment); AnalysisTemplates Prometheus.  
**DoD:** métricas violadas → rollback automático; histórico visível.

---

## ☁️ Sprint 8 — CD Corporativo & Multi-cloud
**Objetivo:** Pipelines enterprise + overlays cloud.

**Escopo:** GitHub Actions, GitOps promotion (dev→stg→prod), Karpenter, cert-manager.  
**Padrões:** semantic versioning, SBOM, DNS/TLS automáticos.  
**DoD:** PR → dev; tag → stg; approval → prod.

---

## ☠️ Sprint 9 — Chaos & Hardening
**Objetivo:** Resiliência e segurança.

**Escopo:** LitmusChaos, Istio fault-injection, OPA/Kyverno, Vault/ASM.  
**DoD:** caos controlado; rollback validado; SLO mantido.

---

## 🔀 Sprint 10 — Istio Avançado (Mesh)
**Objetivo:** Domínio de tráfego leste-oeste.

**Escopo:** VirtualServices, CircuitBreaker, Shadow Deployment.  
**DoD:** shadow tráfego espelhado; mTLS STRICT ativo.

---

## 📚 Sprint 11 — Data Patterns Avançados
**Objetivo:** Padrões de dados modernos.

**Escopo:** CQRS, Outbox + CDC (Debezium), Schema Registry, Redis/Elastic.  
**DoD:** CQRS funcional; CDC replicando outbox; schema compatível.

---

## 🔒 Sprint 12 — Security Deep Dive
**Objetivo:** Cadeia de supply e feature flags.

**Escopo:** Unleash, Cosign, SBOM, Trivy, CodeQL, dependabot.  
**DoD:** imagens assinadas; dark-launch validado; relatórios de segurança nos releases.

---

## 🧪 Sprint 13 — SRE Avançado
**Objetivo:** Confiabilidade contínua.

**Escopo:** SLIs/SLOs/SLAs formais, dashboards SLO, eBPF (Pixie/Cilium).  
**DoD:** SLOs versionados; runbooks e drills realizados; MTTR medido.

---

## 🧭 Sprint 14 — DevEx & Governança
**Objetivo:** Experiência dev + governança.

**Escopo:** Backstage (Service Catalog), ADRs, C4, templates corporativos.  
**DoD:** portal com serviços; novos nascem prontos (obs, testes, CD).

---

## 🐍 Sprint 15 — Fundamentos Python + Data Engineering Essentials
**Objetivo:** Estabelecer ambiente Python e primeiro ETL local integrado ao lake.

**Escopo:** Poetry/virtualenv, pytest, boto3, pandas/polars; ETL local CSV→Parquet (particionado por data); upload para S3/bronze; criação inicial de tabela `orders_raw` no Glue Catalog; seed via `/orders/export`; métricas locais (linhas, duração).  
**DoD:** dataset `orders_raw` visível no Glue Catalog e consultável no Athena.

---

## 🏗️ Sprint 16 — Big Data em Escala (Glue/EMR + PySpark + Catalog)
**Objetivo:** Transformar dados bronze em silver com schema e partições governadas.

**Escopo:** AWS Glue (Jobs PySpark) **ou** AWS EMR/EMR Serverless (Spark), S3, Glue Catalog, Terraform; normalização/enriquecimento, schema evolution e partições por `yyyymmdd`; registro automático no Catalog; testes locais `glue-local` + `pytest`.  
**DoD:** tabelas `orders_silver` e `payments_silver` registradas no Catalog e válidas no Athena.

---

## 🔄 Sprint 17 — Orquestração e IaC Programável (Step Functions + Terraform)
**Objetivo:** Compor pipelines declarativos e automatizar deploys.

**Escopo:** Step Functions com DAG bronze→silver→gold (Glue/EMR/Lambda); triggers por EventBridge (S3 PUT/cron); módulos Terraform para Glue/EMR/Lambda/SFN; DLQ e rollback automáticos.  
**DoD:** state machines visíveis e monitoradas com alarmes e histórico.

---

## 📊 Sprint 18 — Serverless Data & KPIs (Lambda + Athena + FastAPI)
**Objetivo:** Ingestão serverless e APIs de dados consumíveis por serviços.

**Escopo:** Lambda SQS→S3 (idempotência + DLQ + KMS); Lambda Athena→API (KPIs: GMV, pedidos/dia, conversão); FastAPI Data Service integrado a Keycloak/Kong; IaC com Terraform (Lambda, API GW, roles).  
**DoD:** API `/kpi/*` ativa, autenticada e retornando métricas.

---

## 🧮 Sprint 19 — Lakehouse & Analytics (Glue Catalog + Athena + Redshift)
**Objetivo:** Modelar dados gold e expor análises OLAP.

**Escopo:** Tabelas gold (`fct_orders`, `fct_payments`, `dim_customers`), views (`kpi_orders_daily`, `kpi_gmv_daily`), Redshift Serverless + Spectrum; export para dashboards.  
**DoD:** KPIs gold presentes no Catalog e consultas performáticas.

---

## 🛡️ Sprint 20 — DataOps, Observabilidade e Governança
**Objetivo:** Garantir confiabilidade, qualidade e segurança ponta a ponta.

**Escopo:** Instrumentação Glue/EMR/Lambda/SFN (RED/USE); dashboards DataOps (SLOs: sucesso, duração, throughput); Data Quality as Code (Great Expectations); Lake Formation (masking, RLS/CLS); versionamento de contratos de dados; alertas CloudWatch/Prometheus; CI com `terraform validate`.  
**DoD:** SLOs definidos e monitorados; qualidade validada; governança ativa.

---

## 📌 Checklist Transversal (toda sprint)
- Config via env, logs estruturados (JSON), probes, métricas (RED/USE/VALET), tracing (OTel), idempotência.  
- Testes unit + IT (Testcontainers); OAS lint; smoke via Gateway; performance baseline (k6).  
- ServiceMonitor + dashboards “single pane of glass”; logs JSON no Loki; traces no Tempo com contexto.  
- Deploy via Helm + Argo; estratégia declarada (rolling/canary/blue-green/shadow) com Argo Rollouts.  
- Rollback via AnalysisTemplate com métricas no Prometheus.  
- README com endpoints, tópicos, diagramas, ADRs curtos.  
- Quando aplicável a dados: Glue Catalog central; Data Quality (pytest + Great Expectations); gates de schema; orquestração Step Functions com DLQ/rollback; observabilidade Glue/EMR/Lambda/SFN (CloudWatch + OTel); governança Lake Formation; validações Terraform para módulos de dados.

---
