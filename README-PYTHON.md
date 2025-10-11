# ☁️ Data Engineering Cloud-Native Roadmap — Sprints 15–20
### *(Python 3.12 + AWS Glue + Glue Catalog + Step Functions + Athena/Redshift + DataOps)*

![Python](https://img.shields.io/badge/Python-3.12-blue?logo=python)
![AWS Glue](https://img.shields.io/badge/ETL-AWS%20Glue-FF9900?logo=amazon-aws)
![Glue Catalog](https://img.shields.io/badge/Metadata-Glue%20Catalog-232F3E?logo=amazonaws)
![Athena](https://img.shields.io/badge/Analytics-Athena-232F3E?logo=amazonaws)
![Redshift](https://img.shields.io/badge/DataWarehouse-Redshift-007DBC?logo=amazon-aws)
![Step Functions](https://img.shields.io/badge/Orchestration-Step%20Functions-FF4F00?logo=aws)
![Lambda](https://img.shields.io/badge/Serverless-Lambda-F7A80D?logo=awslambda)
![CDK](https://img.shields.io/badge/IaC-CDK%20Python-6DA55F?logo=python)
![Terraform](https://img.shields.io/badge/IaC-Terraform-7B42BC?logo=terraform)
![Prometheus](https://img.shields.io/badge/Observability-Prometheus-E6522C?logo=prometheus)
![OpenTelemetry](https://img.shields.io/badge/Tracing-OpenTelemetry-563D7C?logo=opentelemetry)

---

> 📘 **Objetivo**  
> Expandir o ecossistema **Java/Go Cloud-Native** para a **Camada de Dados** com **Python 3.12+** e serviços AWS, criando pipelines **governados, observáveis e integrados** (eventos → S3 → Glue → Athena/Redshift → APIs/KPIs) tendo o **AWS Glue Catalog** como fonte única de metadados.

---

## ☁️ Contexto Arquitetural

(Java/Go services)
   ├─► Eventos: SNS/SQS/Kafka (OrderCreated, PaymentSettled)
   ├─► Export REST (bulk/replay)
   ▼
[ Lambda (Python) ingest ] ─► S3 bronze
[ Glue Jobs (PySpark) ]    ─► S3 silver/gold  ─► Glue Catalog
                                       ├─► Athena / Redshift (views/KPIs)
                                       └─► FastAPI (APIs de dados para apps Java/Go)
Orquestração: Step Functions + EventBridge
Governança: Lake Formation + KMS
Observabilidade: CloudWatch + Prometheus + OpenTelemetry
IaC: Terraform (fundação) + AWS CDK (pipelines de dados)

---

## 🗓️ Cronograma de Sprints

### 🧮 Sprint 15 — Fundamentos Python + Data Engineering Essentials
**Desafio:** estabelecer ambiente Python e primeiro ETL local integrado ao lake.

- **Stack:** Poetry, virtualenv, pytest, boto3, pandas, polars  
- **Objetivos:**  
  - Setup reprodutível (`Makefile`, `.env`, `pyproject.toml`, `ruff/mypy`).  
  - ETL local: CSV → Parquet (particionando por data).  
  - Upload para **S3/bronze** via `boto3`.  
  - Criação inicial de **tabelas no Glue Catalog** (ex.: `orders_raw`).  
- **Integração Java/Go:** consumo do endpoint de export `/orders/export` (bulk/replay) para gerar base inicial.  
- **Observabilidade:** logs estruturados; métricas locais (linhas, duração).  
- **DoD:** ambiente ok; dataset `orders_raw` visível no Glue Catalog e consultável no Athena.

---

### 🚰 Sprint 16 — Big Data e ETL em Escala (Glue + PySpark + Catalog)
**Desafio:** transformar dados brutos em silver com schema/partições governados.

- **Stack:** AWS Glue (Jobs), PySpark, S3, **Glue Catalog**  
- **Objetivos:**  
  - Jobs **bronze → silver** (normalização, tipos, enriquecimento).  
  - **Schema evolution** e partições (`partition_yyyymmdd`).  
  - Registro/atualização automática no **Glue Catalog**.  
  - Testes locais com `glue-local` + `pytest`.  
- **Integração Java/Go:** eventos publicados pelos serviços alimentam `bronze`; Glue consolida em `silver` (Parquet).  
- **Observabilidade:** métricas Glue no CloudWatch (linhas, bytes, duração, sucesso).  
- **DoD:** tabelas `orders_silver` e `payments_silver` no Catalog; queries no Athena retornando corretamente.

---

### ⚙️ Sprint 17 — Orquestração e IaC Programável (CDK + Step Functions)
**Desafio:** compor pipelines declarativos e acioná-los por eventos/cron.

- **Stack:** AWS CDK (Python), Step Functions SDK, EventBridge, Terraform  
- **Objetivos:**  
  - State Machines **bronze→silver→gold** com tarefas Glue/Lambda.  
  - Triggers EventBridge (S3 PUT/cron).  
  - CDK para empacotar stacks (Jobs, Roles, Step Functions).  
  - Rollback e DLQ de execução integrados.  
- **Integração Java/Go:** quando o serviço publica, a pipeline correspondente é acionada (SLA de ingest definido).  
- **Observabilidade:** CloudWatch Alarms + histórico de execuções; tags para rastreabilidade por domínio.  
- **DoD:** DAGs visíveis, versionadas e com alarmes de falha/timeout.

---

### 🧠 Sprint 18 — Serverless Data Processing e KPIs (Lambda + Athena + FastAPI)
**Desafio:** construir ingestão serverless e APIs de dados consumíveis por serviços.

- **Stack:** Lambda, S3, Athena, EventBridge, boto3, **FastAPI**  
- **Objetivos:**  
  - Lambda **SQS→S3 (bronze)** (idempotência + DLQ + KMS).  
  - Lambda **Athena→API** (expor KPIs: GMV diário, pedidos/dia, conversão).  
  - **FastAPI** como Data Service p/ apps Java/Go e dashboards.  
- **Integração Java/Go:** serviços consomem `/kpi/*` para relatórios e automações; suporte a OIDC (Keycloak) + rate-limit via Kong.  
- **Observabilidade:** OTel tracing nas Lambdas e FastAPI; métricas RED (RPS, erros, p95).  
- **DoD:** ingestão automática + rota `/kpi/orders/daily` funcional sob autenticação.

---

### 📊 Sprint 19 — Data Lakehouse & Analytics (Glue Catalog + Athena + Redshift)
**Desafio:** modelar dados de negócio (gold) e expor análises OLAP.

- **Stack:** Glue Catalog, Athena SQL, **Redshift Serverless**, pandas, boto3  
- **Objetivos:**  
  - Tabelas **gold** (fatos/dimensões): `fct_orders`, `dim_customer`, `fct_payments`.  
  - Views analíticas (`kpi_orders_daily`, `kpi_gmv_daily`).  
  - Integração Redshift (Spectrum) para queries OLAP.  
  - Export para dashboards (Grafana/QuickSight).  
- **Integração Java/Go:** KPIs servem portais e serviços; contratos de métricas documentados (SLIs).  
- **Observabilidade:** logs/queries auditadas; custos por consulta monitorados.  
- **DoD:** consultas consistentes e performáticas; KPIs disponíveis aos consumidores.

---

### 🧩 Sprint 20 — DataOps, Observabilidade e Governança
**Desafio:** garantir confiabilidade, qualidade e segurança ponta a ponta.

- **Stack:** CloudWatch, **Prometheus**, **OpenTelemetry**, **Lake Formation**, Great Expectations  
- **Objetivos:**  
  - Instrumentar Glue/Lambda/Step Functions com métricas RED/USE.  
  - Dashboards DataOps (SLOs: taxa de sucesso, duração, throughput).  
  - **Data Quality as Code** (Great Expectations + pytest).  
  - Lake Formation (masking, row/column level security); chaves KMS e policies.  
  - Versionar **metadados do Glue Catalog** (DDL, schemas) em `data-contracts/`.  
- **Integração Java/Go:** correlação entre eventos de negócio e etapas do pipeline; runbooks de replay.  
- **Observabilidade:** Single Pane para pipelines (painel central DataOps).  
- **DoD:** SLOs definidos e monitorados; governança aplicada; qualidade validada em CI/CD.

---

## 🔍 Checklist Transversal (Data Layer)

✔️ Ambiente Python padronizado (Poetry + Makefile)  
✔️ **Glue Catalog** como fonte única de metadados (versionado no repo)  
✔️ Schemas compatíveis com entidades Java/Go (Order, Payment, Customer)  
✔️ Pipelines **Glue → Step Functions → Athena** automatizados (CDK/Terraform)  
✔️ Lambdas observáveis (OTel + CloudWatch), com DLQ e KMS  
✔️ **Data Quality** (pytest + Great Expectations) em silver/gold  
✔️ Governança Lake Formation (RLS/CLS, masking)  
✔️ Dashboards **RED/USE/VALET** para pipelines  
✔️ CI/CD (pytest, `cdk synth`, `terraform validate`, gates de compatibilidade de schema)

---

## ✅ Resultado Esperado

- Integração total entre microsserviços Java/Go e pipelines Python/AWS.  
- **Glue Catalog** central com lineage e versionamento de schemas.  
- Lakehouse com ingestão, transformação e **KPIs acessíveis por API**.  
- Pipelines **observáveis, seguros e governados** com SLOs e runbooks.  
- Base sólida para **ML/AI** (SageMaker/Bedrock) em fases futuras.
