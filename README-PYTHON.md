# ☁️ Data Engineering Cloud-Native Roadmap — Sprints 15–20
### *(Python 3.12 + AWS Glue + Glue Catalog + Step Functions + Athena/Redshift + DataOps)*

![Python](https://img.shields.io/badge/Python-3.12-blue?logo=python)
![AWS Glue](https://img.shields.io/badge/ETL-AWS%20Glue-FF9900?logo=amazon-aws)
![Glue Catalog](https://img.shields.io/badge/Metadata-Glue%20Catalog-232F3E?logo=amazonaws)
![Athena](https://img.shields.io/badge/Analytics-Athena-232F3E?logo=amazonaws)
![Redshift](https://img.shields.io/badge/DataWarehouse-Redshift-007DBC?logo=amazon-aws)
![Step Functions](https://img.shields.io/badge/Orchestration-Step%20Functions-FF4F00?logo=aws)
![Lambda](https://img.shields.io/badge/Serverless-Lambda-F7A80D?logo=awslambda)
![Terraform](https://img.shields.io/badge/IaC-Terraform-7B42BC?logo=terraform)
![Prometheus](https://img.shields.io/badge/Observability-Prometheus-E6522C?logo=prometheus)
![OpenTelemetry](https://img.shields.io/badge/Tracing-OpenTelemetry-563D7C?logo=opentelemetry)

---

> 📘 **Objetivo**  
> Expandir o ecossistema **Java/Go Cloud-Native** para a **Camada de Dados** com **Python 3.12+** e serviços AWS, criando pipelines **governados, observáveis e integrados** (eventos → S3 → Glue → Athena/Redshift → APIs/KPIs) tendo o **AWS Glue Catalog** como fonte única de metadados.

---

## ☁️ Contexto Arquitetural

```
+--------------------------------------------------------------+
|                     Java / Go Services                       |
|--------------------------------------------------------------|
| - Publicam eventos: SNS / SQS / Kafka                        |
|   (OrderCreated, PaymentSettled, CustomerUpdated)            |
| - Disponibilizam export REST (/orders/export)                |
+--------------------------------------------------------------+
                           │
                           ▼
+--------------------------------------------------------------+
|                AWS Lambda (Python - Ingestão)                |
|  - Consome eventos e grava no S3 (camada bronze)             |
|  - Garante idempotência, DLQ e criptografia KMS              |
+--------------------------------------------------------------+
                           │
                           ▼
+--------------------------------------------------------------+
|                AWS Glue (PySpark - Transformação)            |
|  - ETL bronze → silver → gold                                |
|  - Limpeza, schema, enriquecimento, deduplicação             |
|  - Atualiza metadados no Glue Catalog                        |
+--------------------------------------------------------------+
                           │
                           ▼
+--------------------------------------------------------------+
|                AWS Glue Catalog (Metadados)                  |
|  - Fonte única de verdade dos schemas                        |
|  - Referência para Athena e Redshift Spectrum                |
|  - Versionamento e lineage integrado                         |
+--------------------------------------------------------------+
                           │
                           ▼
+--------------------------------------------------------------+
|         Athena / Redshift / FastAPI Data Services            |
|  - KPIs e relatórios (GMV, pedidos/dia, churn, etc.)         |
|  - APIs analíticas para apps Java/Go e dashboards            |
+--------------------------------------------------------------+

Orquestração: Step Functions + EventBridge  
Governança: Lake Formation + KMS  
Observabilidade: CloudWatch + Prometheus + OpenTelemetry  
IaC: **Terraform** (módulos, workspaces e automação)
```

---

## 🗓️ Cronograma de Sprints

### 🧮 Sprint 15 — Fundamentos Python + Data Engineering Essentials
**Desafio:** estabelecer ambiente Python e primeiro ETL local integrado ao lake.

- **Stack:** Poetry, virtualenv, pytest, boto3, pandas, polars  
- **Objetivos:**  
  - Setup reprodutível (`Makefile`, `.env`, `pyproject.toml`, `ruff/mypy`).  
  - ETL local: CSV → Parquet (particionado por data).  
  - Upload para **S3/bronze** via `boto3`.  
  - Criação inicial de **tabelas no Glue Catalog** (`orders_raw`).  
- **Integração Java/Go:** consumo do endpoint `/orders/export` para seed inicial.  
- **Observabilidade:** logs estruturados e métricas locais (linhas, duração).  
- **DoD:** ambiente ok; dataset `orders_raw` visível no Glue Catalog e consultável no Athena.

---

### 🚰 Sprint 16 — Big Data e ETL em Escala (Glue + PySpark + Catalog)
**Desafio:** transformar dados brutos em silver com schema e partições governadas.

- **Stack:** AWS Glue (Jobs), PySpark, S3, **Glue Catalog**, Terraform  
- **Objetivos:**  
  - Jobs **bronze → silver** (normalização, tipos, enriquecimento).  
  - **Schema evolution** e partições (`partition_yyyymmdd`).  
  - Registro automático no **Glue Catalog**.  
  - Testes locais com `glue-local` + `pytest`.  
  - Provisionamento via Terraform (S3, Glue, IAM).  
- **Integração Java/Go:** eventos alimentam bronze; Glue consolida em silver.  
- **Observabilidade:** métricas Glue (linhas, bytes, duração, sucesso).  
- **DoD:** tabelas `orders_silver` e `payments_silver` registradas; queries Athena válidas.

---

### ⚙️ Sprint 17 — Orquestração e IaC Programável (Step Functions + Terraform)
**Desafio:** compor pipelines declarativos e automatizar deploys.

- **Stack:** AWS Step Functions, EventBridge, Terraform  
- **Objetivos:**  
  - State Machines **bronze→silver→gold** com Glue/Lambda.  
  - Triggers EventBridge (S3 PUT/cron).  
  - Implementar módulos Terraform para Glue, Lambda e SFN.  
  - DLQ e rollback automáticos.  
- **Integração Java/Go:** publicação de eventos dispara pipeline.  
- **Observabilidade:** CloudWatch Alarms + rastreabilidade por domínio.  
- **DoD:** DAGs visíveis e monitoradas com alarmes e histórico.

---

### 🧠 Sprint 18 — Serverless Data Processing e KPIs (Lambda + Athena + FastAPI)
**Desafio:** criar ingestão serverless e APIs de dados consumíveis por serviços.

- **Stack:** Lambda, S3, Athena, EventBridge, boto3, **FastAPI**, Terraform  
- **Objetivos:**  
  - Lambda **SQS→S3 (bronze)** (idempotência + DLQ + KMS).  
  - Lambda **Athena→API** (KPIs: GMV, pedidos/dia, conversão).  
  - **FastAPI Data Service** integrado a Keycloak e Kong.  
  - Infra provisionada com Terraform (Lambda, API Gateway, roles).  
- **Integração Java/Go:** apps consomem `/kpi/*`; dados refletem eventos transacionais.  
- **Observabilidade:** tracing OTel + métricas RED (RPS, erros, p95).  
- **DoD:** ingestão automática e API `/kpi/orders/daily` ativa e autenticada.

---

### 📊 Sprint 19 — Data Lakehouse & Analytics (Glue Catalog + Athena + Redshift)
**Desafio:** modelar dados de negócio (gold) e expor análises OLAP.

- **Stack:** Glue Catalog, Athena SQL, **Redshift Serverless**, pandas, boto3, Terraform  
- **Objetivos:**  
  - Tabelas **gold** (`fct_orders`, `fct_payments`, `dim_customers`).  
  - Views analíticas (`kpi_orders_daily`, `kpi_gmv_daily`).  
  - Integração Redshift Spectrum.  
  - Export KPIs para Grafana/QuickSight.  
  - Infra redshift e Athena gerenciada por Terraform.  
- **Integração Java/Go:** métricas consumidas por relatórios e auditorias.  
- **Observabilidade:** logs Athena; custos e tempos por query.  
- **DoD:** KPIs gold no Catalog; consultas rápidas e consistentes.

---

### 🧩 Sprint 20 — DataOps, Observabilidade e Governança
**Desafio:** garantir confiabilidade, qualidade e segurança ponta a ponta.

- **Stack:** CloudWatch, **Prometheus**, **OpenTelemetry**, **Lake Formation**, Great Expectations, Terraform  
- **Objetivos:**  
  - Instrumentar Glue/Lambda/Step Functions (RED/USE).  
  - Dashboards DataOps (SLOs: sucesso, duração, throughput).  
  - **Data Quality as Code** (Great Expectations).  
  - Governança Lake Formation (masking, RLS, CLS).  
  - Versionar metadados Glue Catalog (`data-contracts/`).  
  - Implementar monitoramento e alertas via Terraform (CloudWatch/Prometheus).  
- **Integração Java/Go:** correlação entre eventos e dados analíticos; runbooks de replay.  
- **Observabilidade:** painéis centralizados DataOps + alertas.  
- **DoD:** SLOs definidos, governança ativa, qualidade validada em CI/CD.

---

## 🔍 Checklist Transversal

✔️ Ambiente Python padronizado (Poetry + Makefile)  
✔️ **Glue Catalog** centralizado e versionado (`data-contracts/`)  
✔️ Schemas compatíveis com entidades Java/Go (Order, Payment, Customer)  
✔️ Pipelines **Glue → Step Functions → Athena** automatizados via **Terraform**  
✔️ Lambdas observáveis (OTel + CloudWatch)  
✔️ Data Quality (pytest + Great Expectations)  
✔️ Governança Lake Formation (masking, RLS/CLS)  
✔️ Dashboards RED/USE/VALET  
✔️ CI/CD (pytest + `terraform validate` + schema gates)

---

## ✅ Resultado Esperado

- Integração total entre **microsserviços Java/Go** e pipelines Python/AWS.  
- **Glue Catalog** central com lineage e versionamento de schemas.  
- Lakehouse corporativo com KPIs consumíveis por API.  
- Pipelines **observáveis, seguros e governados** com SLOs e runbooks.  
- Base sólida para **ML/AI (SageMaker / Bedrock)** em fases futuras.
