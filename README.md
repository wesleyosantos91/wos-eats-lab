# 🚀 Roadmap de Estudos & Projeto Microservices Cloud-Native

Este repositório descreve o **cronograma de estudo e prática** de um ecossistema de microsserviços moderno, 
usando **Java 25 + Spring Boot 3.5.6** e uma stack **container-first** com Kubernetes, GitOps, observabilidade, qualidade de software e boas práticas corporativas.

---

## 📅 Cronograma de Sprints (alto nível)

### 🏁 Sprint 0 — Bootstrap & Plataforma Base
- **Desafio**: Subir cluster local (kind) e instalar a base de plataforma.
- **Assuntos/Patterns**: 12-Factor Apps, Rolling Update, Probes, GitOps.
- **Tecnologias/Recursos**: kind, Argo CD, Helm, Kong, Istio, Prometheus/Grafana, Loki, Tempo, KEDA, LocalStack Pro, Redpanda, Keycloak, MinIO, blackbox-exporter.
- **Qualidade/Testes**: monitoramento sintético inicial (`blackbox-exporter` para health-check do gateway).

---

### 🥗 Sprint 1 — Catalog + Gateway
- **Desafio**: Primeiro microserviço real publicado via API Gateway.
- **Assuntos/Patterns**: API Gateway, OpenAPI Code-First, métricas RED.
- **Tecnologias/Recursos**: Spring Boot, SpringDoc, Spectral, Kong Ingress, Istio sidecar.
- **Qualidade/Testes**: unitários, integração (Testcontainers Postgres/MinIO), lint OpenAPI, smoke test REST-assured contra Kong.

---

### 🔑 Sprint 2 — Customer/Auth + JWT
- **Desafio**: Adicionar autenticação/autorização corporativa.
- **Assuntos/Patterns**: OIDC, JWT, AuthN/AuthZ, mTLS.
- **Tecnologias/Recursos**: Keycloak, Kong plugin JWT, Spring Security, Istio AuthPolicy.
- **Qualidade/Testes**: integração com Testcontainers Keycloak, testes de autorização (tokens inválidos/expirados).

---

### ⚡ Sprint 2A — Serverless Edge + Fan-out
- **Desafio**: Exercitar padrões serverless com fan-out.
- **Assuntos/Patterns**: Serverless, Fan-out, DLQ.
- **Tecnologias/Recursos**: LocalStack Pro, AWS API GW, S3, SNS, SQS, Lambda (Quarkus), Terraform.
- **Qualidade/Testes**: integração com LocalStack (SQS/S3/Lambda), smoke para webhooks, sintéticos de fila.

---

### 🧾 Sprint 3 — Order + Kafka (Redpanda) + Outbox
- **Desafio**: Criar pedidos e publicar eventos confiáveis.
- **Assuntos/Patterns**: Outbox, Idempotent Consumer, Event-Carried State Transfer.
- **Tecnologias/Recursos**: Redpanda (Kafka), Spring Kafka, Transactional Outbox.
- **Qualidade/Testes**: validação de outbox (consistência DB+evento), contratos de eventos (JSON Schema).

---

### ⚡ Sprint 3B — Kafka Deep Dive
- **Desafio**: Aprofundar em Kafka/Redpanda como backbone de eventos.
- **Assuntos/Patterns**:  
  - Partições, offsets, consumer groups, rebalance  
  - Idempotent producer, transactional producer, exactly-once  
  - DLQ e tópicos de retry  
  - Retention/compaction policies  
  - Schema evolution (JSON Schema/Avro + Schema Registry)  
  - Reprocessamento seguro e replay de tópicos  
  - Lag monitoring + autoscale com KEDA
- **Tecnologias/Recursos**: Redpanda, Redpanda Console, Spring Kafka, KEDA, Schema Registry, Grafana dashboards.
- **Qualidade/Testes**: testes de carga com k6/Gatling, simulação de lag e autoescalonamento com KEDA.

---

### 💳 Sprint 4 — Payment + Saga
- **Desafio**: Orquestrar pagamentos com consistência.
- **Assuntos/Patterns**: Saga Orchestration, Circuit Breaker, Retry, Timeout, Bulkhead.
- **Tecnologias/Recursos**: Spring Boot + Resilience4j, Kafka.
- **Qualidade/Testes**: testes de saga (fluxos felizes e compensatórios), fault injection com Istio.

---

### 🚚 Sprint 5 — Delivery + Notification
- **Desafio**: Integrar entrega e notificações multi-canal.
- **Assuntos/Patterns**: Event Choreography, Fan-out SNS/SQS, DLQ.
- **Tecnologias/Recursos**: Kafka (Redpanda), SNS, SQS, Workers Spring/Lambda.
- **Qualidade/Testes**: fan-out validado (email/sms/whatsapp), testes de DLQ/reprocesso.

---

### 📊 Sprint 6 — Observabilidade & Autoscale
- **Desafio**: Implementar observabilidade e autoescalonamento.
- **Assuntos/Patterns**: RED/USE, HPA, KEDA, Distributed Tracing.
- **Tecnologias/Recursos**: Micrometer, Prometheus, Grafana, Loki, Tempo, OTel, KEDA, ALB autoscale.
- **Qualidade/Testes**: baseline de performance com k6, dashboards RED/USE, alertas SLO.

---

### 🚀 Sprint 7 — Progressive Delivery (Argo Rollouts)
- **Desafio**: Deploys avançados com segurança.
- **Assuntos/Patterns**: Blue-Green, Canary, AnalysisTemplate (metrics-based rollout).
- **Tecnologias/Recursos**: Argo Rollouts, Prometheus, Istio/Kong/ALB.
- **Qualidade/Testes**: rollback automático por métricas (error rate, p95 latency, consumer lag), smoke tests automatizados em cada step.

---

### ☁️ Sprint 8 — CD Corporativo & Multi-cloud
- **Desafio**: Estabelecer pipelines corporativos e suporte multi-cloud.
- **Assuntos/Patterns**: GitOps Promotion Flow, Overlays EKS/AKS.
- **Tecnologias/Recursos**: GitHub Actions, Argo CD, ExternalDNS, cert-manager, External Secrets, Karpenter.
- **Qualidade/Testes**: gates de pipeline (unit, integration, contract, perf smoke antes de promote).

---

### ☠️ Sprint 9 — Chaos & Hardening
- **Desafio**: Testar resiliência e reforçar segurança.
- **Assuntos/Patterns**: Chaos Testing, OPA/Kyverno policies, Secrets mgmt.
- **Tecnologias/Recursos**: LitmusChaos, Istio fault injection, Gatekeeper/Kyverno, External Secrets.
- **Qualidade/Testes**: caos controlado com asserts (latência/erro ≤ SLO); rollback automático validado.

---

### 🔀 Sprint 10 — Istio Avançado (Mesh)
- **Desafio**: Explorar service mesh completo.
- **Assuntos/Patterns**: Retries, CB, Fault Injection, **Shadow Deployment**, A/B Testing, mTLS STRICT.
- **Tecnologias/Recursos**: Istio (VirtualService, DestinationRule, PeerAuth).
- **Qualidade/Testes**: shadow + synthetics, comparação de métricas A/B.

---

### 📚 Sprint 11 — Data Patterns Avançados
- **Desafio**: Padrões corporativos de dados.
- **Assuntos/Patterns**: CQRS, Event Sourcing, Outbox + CDC, Schema Registry.
- **Tecnologias/Recursos**: Debezium, Redpanda Schema Registry, Spring Boot CQRS.
- **Qualidade/Testes**: consistência de leitura/escrita em CQRS, replays com Debezium.

---

### 🔒 Sprint 12 — Security Deep Dive
- **Desafio**: Segurança avançada e supply chain.
- **Assuntos/Patterns**: Feature Flags, Dark Launch, SBOM, Imagem assinada, SAST/DAST.
- **Tecnologias/Recursos**: Unleash, Cosign, Syft/Grype, Trivy, CodeQL.
- **Qualidade/Testes**: toggles validados, scans automatizados (SAST/DAST, SBOM).

---

### 🧪 Sprint 13 — SRE Avançado
- **Desafio**: Práticas avançadas de confiabilidade.
- **Assuntos/Patterns**: SLIs/SLOs/SLAs, eBPF observability, Incident Response.
- **Tecnologias/Recursos**: Grafana SLO, Pixie, Cilium Hubble, Oncall drills.
- **Qualidade/Testes**: runbooks + game days simulando incidentes.

---

### 🧭 Sprint 14 — DevEx & Governança
- **Desafio**: Melhorar experiência do dev e governança.
- **Assuntos/Patterns**: Service Catalog, ADRs + C4, Templates corporativos.
- **Tecnologias/Recursos**: Backstage, ADR + Mermaid, Spring Initializr corporativo.
- **Qualidade/Testes**: lint automático de ADRs/diagramas, templates de projeto com testes já inclusos.

---

## 📚 Referências de estudo
- **Books**: "Building Microservices" (Sam Newman), "Implementing Domain-Driven Design" (Vaughn Vernon), "Kafka: The Definitive Guide" (Narkhede et al.), "Site Reliability Engineering" (Google).  
- **Docs**: [Spring Boot](https://spring.io/projects/spring-boot), [Argo CD](https://argo-cd.readthedocs.io), [Istio](https://istio.io), [Kong](https://docs.konghq.com), [Redpanda](https://docs.redpanda.com).  
- **Patterns**: [Microservices.io](https://microservices.io), [12-Factor App](https://12factor.net), [Messaging Patterns](https://microservices.io/patterns/communication-style/messaging.html).  

---

## ✅ Resultado esperado
Ao final desse roadmap:
- Você terá aplicado **todos os patterns essenciais de microsserviços** (resiliência, dados, deploy, observabilidade, segurança, governança).  
- Terá feito um **deep dive completo em Kafka/Redpanda**, cobrindo desde fundamentos até operação avançada.  
- Terá integrado **qualidade contínua**: unitários, integração, contratos, E2E, sintéticos e performance.  
- Sua stack será **cloud-native corporativa**, container-first, multi-cloud-ready, **12-Factor compliant**, com **rollbacks automáticos baseados em métricas**.  
- Terá consolidado teoria + prática para atuar como **Staff/Principal Engineer** em microsserviços.