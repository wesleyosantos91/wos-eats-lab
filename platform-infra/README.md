# 🏗️ Platform Infrastructure

![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-Kind-326CE5?logo=kubernetes&logoColor=white)
![ArgoCD](https://img.shields.io/badge/GitOps-ArgoCD-EF7B4D?logo=argo&logoColor=white)
![Terraform](https://img.shields.io/badge/IaC-Terraform-7B42BC?logo=terraform&logoColor=white)

![Prometheus](https://img.shields.io/badge/Metrics-Prometheus-E6522C?logo=prometheus&logoColor=white)
![Grafana](https://img.shields.io/badge/Dashboards-Grafana-F46800?logo=grafana&logoColor=white)
![Jaeger](https://img.shields.io/badge/Tracing-Jaeger-B19AB8?logo=jaegertracing&logoColor=white)
![Kong](https://img.shields.io/badge/Gateway-Kong-00695C?logo=kong&logoColor=white)

![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-336791?logo=postgresql&logoColor=white)
![Keycloak](https://img.shields.io/badge/Auth-Keycloak-FF6B35?logo=keycloak&logoColor=white)
![LocalStack](https://img.shields.io/badge/AWS-LocalStack-FF9900?logo=amazon-aws&logoColor=white)

---

> 🎯 **Objetivo:** Infraestrutura completa cloud-native para desenvolvimento e testes locais, incluindo observabilidade 360°, API Gateway, autenticação, mensageria e emulação AWS. Base para os microsserviços Go/Java e pipelines de dados Python.

---

## 🏗️ Arquitetura da Plataforma

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Developer     │    │   Kong Gateway  │    │  Microservices  │
│   Experience    │────▶│   (Port 8000)   │────▶│   (Go/Java)     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   ArgoCD UI     │    │   Keycloak      │    │  Observability  │
│   (Port 8080)   │    │   (Port 8085)   │    │     Stack       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │                       │
                                ▼                       ▼
                    ┌─────────────────┐    ┌─────────────────┐
                    │   PostgreSQL    │    │   Grafana UI    │
                    │ (Ports 5432/33) │    │   (Port 3000)   │
                    └─────────────────┘    └─────────────────┘
                                │                       │
                                ▼                       ▼
                    ┌─────────────────┐    ┌─────────────────┐
                    │   LocalStack    │    │   Prometheus    │
                    │   (Port 4566)   │    │   (Port 9090)   │
                    └─────────────────┘    └─────────────────┘
```

---

## 🚀 Quick Start

### Pré-requisitos
- Docker & Docker Compose
- Make
- Kind (para Kubernetes local)
- kubectl
- Helm
- Terraform

### 🐳 Subir Infraestrutura Local (Docker Compose)

```bash
# Subir todos os serviços
make compose-up

# Verificar status
make compose-ps

# Ver logs em tempo real
make compose-logs

# Parar tudo
make compose-down
```

### ☸️ Subir Cluster Kubernetes Local (Kind)

```bash
# Criar cluster Kind
make kind-up

# Deploy da aplicação hello
make app-deploy

# Instalar ArgoCD
make argo-install

# Verificar saúde completa
make check
```

### 🔄 Setup Completo (Docker + K8s + Terraform)

```bash
# Setup completo da plataforma
make full-up

# Setup apenas para desenvolvimento
make dev-up
```

---

## 🌐 Links de Acesso

### 🎛️ **Observabilidade Stack**

| Serviço | URL | Credenciais | Descrição |
|---------|-----|-------------|-----------|
| **Grafana** | [http://localhost:3000](http://localhost:3000) | `admin/admin` | Dashboards & Visualizações |
| **Prometheus** | [http://localhost:9090](http://localhost:9090) | - | Métricas & Alertas |
| **Jaeger** | [http://localhost:16686](http://localhost:16686) | - | Distributed Tracing |
| **Loki** | [http://localhost:3100](http://localhost:3100) | - | Log Aggregation |
| **Tempo** | [http://localhost:3200](http://localhost:3200) | - | Tracing Backend |
| **Blackbox Exporter** | [http://localhost:9115](http://localhost:9115) | - | Synthetic Monitoring |
| **OTel Collector** | `localhost:4317/4318` | - | Telemetry Collection |

### 🚪 **API Gateway & Auth**

| Serviço | URL | Credenciais | Descrição |
|---------|-----|-------------|-----------|
| **Kong Gateway** | [http://localhost:8000](http://localhost:8000) | - | API Gateway Principal |
| **Kong Admin** | [http://localhost:8001](http://localhost:8001) | - | Kong Admin API |
| **Kong Manager** | [http://localhost:8002](http://localhost:8002) | - | Kong Web UI |
| **Keycloak** | [http://localhost:8085](http://localhost:8085) | `admin/admin` | Identity & Access Mgmt |

### 💾 **Databases & Storage**

| Serviço | URL | Credenciais | Descrição |
|---------|-----|-------------|-----------|
| **PostgreSQL (main)** | `localhost:5432` | `postgres/postgres` | Database Principal |
| **PostgreSQL (infra)** | `localhost:5433` | `infra/infra` | Database Infraestrutura |
| **LocalStack** | [http://localhost:4566](http://localhost:4566) | - | AWS Services Emulation |

### ☸️ **Kubernetes & GitOps**

| Serviço | Comando | Credenciais | Descrição |
|---------|---------|-------------|-----------|
| **ArgoCD UI** | `make argo-port` → [http://localhost:8080](http://localhost:8080) | `admin` + `make argo-pwd` | GitOps Dashboard |
| **Hello App (NodePort)** | [http://localhost:30080](http://localhost:30080) | - | Sample Application |
| **Hello App (via Kong)** | [http://localhost:8000/hello](http://localhost:8000/hello) | - | App via Gateway |

---

## 📊 Stack de Observabilidade

### 🎯 **Métricas (RED/USE/VALET)**
- **Prometheus** coleta métricas de todos os serviços
- **Grafana** dashboards para visualização
- **Blackbox Exporter** para synthetic monitoring
- **OTel Collector** para agregação de telemetria

### 📝 **Logs Centralizados**
- **Loki** para agregação de logs
- **Grafana** para consulta e visualização
- Logs estruturados em JSON

### 🔍 **Distributed Tracing**
- **Jaeger** para traces distribuídos
- **Tempo** como backend alternativo
- **OpenTelemetry** para instrumentação

### 🚨 **Alerting**
- **Prometheus Alertmanager** (configurável)
- Integração com Grafana Alerts
- Health checks automáticos

---

## 🛠️ Comandos Make Disponíveis

### 🐳 **Docker Compose**
```bash
make compose-up          # Subir serviços
make compose-down        # Parar serviços
make compose-logs        # Ver logs
make compose-ps          # Status dos serviços
make compose-health      # Health check dos serviços
```

### ☸️ **Kubernetes (Kind)**
```bash
make kind-up             # Criar cluster
make kind-down           # Deletar cluster
make kind-info           # Info do cluster
make app-deploy          # Deploy hello app
make app-status          # Status da aplicação
```

### 🧠 **Pod Management**
```bash
make pods-list [NS=namespace]                    # Listar pods
make pods-logs POD=<name> [NS=namespace]         # Logs de um pod
make pods-exec POD=<name> [NS=namespace]         # Shell em um pod
make pods-restart DEPLOY=<name> [NS=namespace]   # Reiniciar deployment
make pods-scale DEPLOY=<name> REPLICAS=<num>     # Escalar deployment
make pods-delete [NS=namespace]                  # Deletar todos os pods
make pods-status [NS=namespace]                  # Status detalhado
make pods-events [NS=namespace]                  # Eventos dos pods
```

### 🔄 **ArgoCD**
```bash
make argo-install        # Instalar ArgoCD
make argo-app-deploy     # Deploy ArgoCD Application
make argo-app-delete     # Remover ArgoCD Application
make argo-pwd            # Senha do admin
make argo-port           # Port forward UI
```

### 🏗️ **Terraform**
```bash
make tf-init             # Inicializar Terraform
make tf-plan             # Planejar mudanças
make tf-apply            # Aplicar mudanças
make tf-destroy          # Destruir recursos
make tf-output           # Ver outputs
```

### 🩺 **Health Checks**
```bash
make check               # Smoke tests completos
make status              # Status da infraestrutura
make health              # Alias para check
make compose-health      # Health dos containers
make k8s-health          # Health do K8s
make kong-check          # Health do Kong
make prom-targets        # Targets do Prometheus
make argo-health         # Health do ArgoCD
make localstack-health   # Health do LocalStack
make tf-state            # Estado do Terraform
```

### 🔄 **Workflows Completos**
```bash
make up                  # = compose-up
make full-up             # Setup completo (Docker + K8s + Terraform)
make dev-up              # Setup desenvolvimento (sem ArgoCD/TF)
make down                # Parar serviços
make clean               # = down
make nuke                # Cleanup completo + Docker cleanup
```

### 🛠️ **Helpers**
```bash
make logs                                        # = compose-logs
make restart-service SERVICE=<name>             # Reiniciar serviço específico
make help                                        # Ver todos os comandos
```

---

## 📁 Estrutura do Projeto

```
platform-infra/
├── docker/                    # Docker Compose stack
│   ├── docker-compose.yml     # Serviços principais
│   ├── prometheus/            # Config Prometheus
│   ├── grafana/               # Dashboards & datasources
│   ├── loki/                  # Config Loki
│   ├── tempo/                 # Config Tempo
│   ├── otel/                  # OpenTelemetry Collector
│   ├── kong/                  # Kong Gateway config
│   ├── blackbox/              # Blackbox exporter config
│   └── postgres/              # Scripts de inicialização
├── k8s/                       # Kubernetes manifests
│   ├── kind/                  # Kind cluster config
│   │   ├── kind-config.yaml   # Cluster configuration
│   │   └── apps/              # Sample applications
│   │       └── hello/         # Hello world app
│   └── argo/                  # ArgoCD configurations
│       ├── values.yaml        # ArgoCD Helm values
│       └── apps/              # ArgoCD Applications
├── terraform/                 # Infrastructure as Code
│   ├── main.tf               # Terraform main config
│   ├── providers.tf          # Provider configurations
│   ├── variables.tf          # Input variables
│   └── outputs.tf            # Output values
├── Makefile                  # Automation commands
└── README.md                 # Este arquivo
```

---

## 🔧 Configurações Principais

### 🗄️ **Databases**
- **PostgreSQL 18.0** (main): porta 5432, user/pass: `postgres/postgres`
- **PostgreSQL 18.0** (infra): porta 5433, user/pass: `infra/infra`
- Databases automáticos: `kong`, `keycloak` via script de inicialização

### 🚪 **Kong Gateway**
- **Proxy**: porta 8000 (HTTP) / 8443 (HTTPS)
- **Admin API**: porta 8001 (HTTP) / 8444 (HTTPS)
- **Manager**: porta 8002
- Database: PostgreSQL (infra)

### 🔐 **Keycloak**
- **UI**: porta 8085
- **Health**: porta 9000
- Admin: `admin/admin`
- Database: PostgreSQL (infra)

### ☁️ **LocalStack**
- **Gateway**: porta 4566
- **Services**: porta range 4510-4530
- Emula: S3, SQS, SNS, Lambda, API Gateway, etc.

### 📊 **Observability Ports**
- **Grafana**: 3000
- **Prometheus**: 9090
- **Jaeger**: 16686
- **Loki**: 3100
- **Tempo**: 3200
- **Blackbox**: 9115
- **OTel Collector**: 4317 (gRPC), 4318 (HTTP)

---

## 🧪 Testes e Validação

### ✅ **Health Checks Automáticos**
Todos os serviços possuem health checks configurados:

```bash
# Verificar saúde completa
make check

# Output esperado:
==> Running comprehensive smoke tests...
==> Checking Docker Compose services...
==> Checking Kubernetes cluster...
== K8s ==
==> Checking Kong API Gateway...
== Service hello via NodePort ==
✓ NodePort OK
== Kong /hello ==
✓ Kong OK
==> Checking Prometheus targets...
✓ Prometheus targets UP
==> Checking ArgoCD...
✓ ArgoCD OK
==> Checking LocalStack...
✓ LocalStack OK
==> Checking Terraform state...
Tudo certo ✅
```

### 🎯 **Smoke Tests**
- **Kong Gateway**: `/hello` endpoint via gateway
- **NodePort**: acesso direto à aplicação
- **Prometheus**: targets UP
- **ArgoCD**: deployment ativo
- **LocalStack**: health endpoint
- **Terraform**: state consistente

---

## 🔒 Segurança

### 🛡️ **Autenticação & Autorização**
- **Keycloak** OIDC/JWT provider
- **Kong** JWT plugin integration
- **RBAC** configurável via Keycloak

### 🔐 **Secrets Management**
- Environment variables para desenvolvimento
- Kubernetes Secrets para produção
- Vault integration (futuro)

### 🚪 **Network Security**
- Service mesh ready (Istio/Linkerd)
- mTLS configurável
- Network policies (futuro)

---

## 📈 Monitoramento e Alertas

### 📊 **Dashboards Disponíveis**
- **Infrastructure Overview**: visão geral da plataforma
- **Kong Gateway**: métricas de API Gateway
- **Application Metrics**: RED/USE por serviço
- **Kubernetes**: pods, nodes, deployments
- **Database**: PostgreSQL performance

### 🚨 **Alertas Configurados**
- **High Error Rate**: taxa de erro > 5%
- **High Latency**: P99 > 1s
- **Service Down**: health check failed
- **Resource Usage**: CPU/Memory > 80%

### 🎯 **SLIs/SLOs**
- **Availability**: 99.9% uptime
- **Latency**: P99 < 1s
- **Error Rate**: < 0.1%
- **Throughput**: capacity planning

---

## 🌟 Próximos Passos

### 🚀 **Expansões Planejadas**
1. **Service Mesh**: Istio/Linkerd integration
2. **Certificate Management**: cert-manager + Let's Encrypt
3. **External DNS**: automatização de DNS
4. **Vault**: secrets management
5. **Backup/Restore**: estratégias de backup
6. **Multi-cluster**: federação de clusters

### 🔄 **CI/CD Integration**
1. **GitHub Actions**: automation workflows
2. **ArgoCD Image Updater**: automatic image updates
3. **Progressive Delivery**: Argo Rollouts
4. **Policy as Code**: OPA/Gatekeeper

---

## 📚 Referências

- [Kong Gateway Documentation](https://docs.konghq.com/)
- [ArgoCD Documentation](https://argo-cd.readthedocs.io/)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [LocalStack Documentation](https://docs.localstack.cloud/)
- [OpenTelemetry Documentation](https://opentelemetry.io/docs/)
- [Kind Documentation](https://kind.sigs.k8s.io/)

---

## 🤝 Contribuição

Para contribuir com melhorias na infraestrutura:

1. Fork o repositório
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Add nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

---

## 📋 Troubleshooting

### 🚨 **Problemas Comuns**

**Kong não sobe:**
```bash
# Verificar logs
make compose-logs kong

# Reiniciar dependências
make restart-service SERVICE=postgres_infra
make restart-service SERVICE=kong
```

**ArgoCD password:**
```bash
# Obter senha do admin
make argo-pwd
```

**Pods em CrashLoopBackOff:**
```bash
# Verificar logs
make pods-logs POD=<pod-name>

# Descrever pod
make pods-describe POD=<pod-name>

# Ver eventos
make pods-events
```

**Cleanup completo:**
```bash
# Remove tudo incluindo volumes e imagens
make nuke
```

---

*Este README faz parte do projeto [wos-eats-lab](../README.md) - Cloud-Native Roadmap Unificado*
