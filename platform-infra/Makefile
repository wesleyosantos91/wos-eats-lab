# Default shell
SHELL := /bin/bash

# -----------------------------
# Variables
# -----------------------------
# Docker Compose
COMPOSE_DIR := docker
COMPOSE_FILE := $(COMPOSE_DIR)/docker-compose.yml

# Terraform directory
TF_DIR := terraform

# Kubernetes / Kind
KIND_CLUSTER := platform
K8S_CONTEXT := kind-$(KIND_CLUSTER)
KUBECTL := kubectl --context $(K8S_CONTEXT)

# Pods cleanup
NS ?= default

# Namespaces
ARGOCD_NS := argocd
MON_NS := monitoring

# Helm values
HELM_VALUES_DIR := k8s/argo
ARGO_VALUES := $(HELM_VALUES_DIR)/values.yaml

# URLs and Endpoints
KONG_ENDPOINT := http://localhost:8000
PROMETHEUS_ENDPOINT := http://localhost:9090
ARGOCD_ENDPOINT := http://localhost:8080

# -----------------------------
# Help
# -----------------------------
.PHONY: help
help: ## Show this help message
	@echo 'Usage: make [target]'
	@echo ''
	@echo 'Infrastructure Commands:'
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z0-9_.-]+:.*?## / {printf "  \033[36m%-24s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

# -----------------------------
# Docker Compose Commands
# -----------------------------
.PHONY: compose-up compose-down compose-logs compose-ps
compose-up: ## Start all infrastructure services
	cd $(COMPOSE_DIR) && docker compose up -d

compose-down: ## Stop all infrastructure services
	cd $(COMPOSE_DIR) && docker compose down -v

compose-logs: ## Show logs from all services
	cd $(COMPOSE_DIR) && docker compose logs -f

compose-ps: ## Show status of all services
	cd $(COMPOSE_DIR) && docker compose ps

# -----------------------------
# Kubernetes Cluster Management
# -----------------------------
.PHONY: kind-up kind-down kind-info
kind-up: ## Create Kind cluster
	kind create cluster --name $(KIND_CLUSTER) --config k8s/kind/kind-config.yaml || true
	$(KUBECTL) cluster-info
	$(KUBECTL) get nodes

kind-down: ## Delete Kind cluster
	kind delete cluster --name $(KIND_CLUSTER)

kind-info: ## Show Kind cluster information
	$(KUBECTL) cluster-info
	$(KUBECTL) get nodes

# -----------------------------
# Application Deployment
# -----------------------------
.PHONY: app-deploy app-status
app-deploy: ## Deploy hello application to Kind
	$(KUBECTL) apply -f k8s/kind/apps/hello
	$(KUBECTL) rollout status deploy/hello --timeout=300s

app-status: ## Show application status
	$(KUBECTL) get deploy,pods,svc -l app=hello

# -----------------------------
# Pod Management
# -----------------------------
.PHONY: pods-delete pods-delete-force pods-list pods-logs pods-describe pods-restart pods-scale pods-exec

pods-list: ## Lista todos os pods do namespace (padrão: default). Use NS=<namespace> para mudar.
	@echo "==> Listando pods no namespace: $(NS)"
	$(KUBECTL) get pods -n $(NS) -o wide

pods-delete: ## Deleta todos os pods do namespace (padrão: default). Use NS=<namespace> para mudar.
	@echo "==> Deletando todos os pods no namespace: $(NS)"
	-$(KUBECTL) delete pods --all -n $(NS)

pods-delete-force: ## Força a deleção (para pods presos em Terminating). Use NS=<namespace>.
	@echo "==> Forçando deleção de todos os pods no namespace: $(NS)"
	-$(KUBECTL) delete pods --all -n $(NS) --force --grace-period=0

pods-logs: ## Mostra logs de um pod específico. Use POD=<nome-do-pod> NS=<namespace>
ifndef POD
	@echo "Usage: make pods-logs POD=<pod-name> [NS=namespace]"
	@echo "Available pods in namespace $(NS):"
	@$(KUBECTL) get pods -n $(NS) --no-headers -o custom-columns=":metadata.name"
else
	@echo "==> Logs do pod $(POD) no namespace $(NS)"
	$(KUBECTL) logs $(POD) -n $(NS) --tail=100 -f
endif

pods-describe: ## Descreve um pod específico. Use POD=<nome-do-pod> NS=<namespace>
ifndef POD
	@echo "Usage: make pods-describe POD=<pod-name> [NS=namespace]"
	@echo "Available pods in namespace $(NS):"
	@$(KUBECTL) get pods -n $(NS) --no-headers -o custom-columns=":metadata.name"
else
	@echo "==> Descrevendo pod $(POD) no namespace $(NS)"
	$(KUBECTL) describe pod $(POD) -n $(NS)
endif

pods-restart: ## Reinicia pods de um deployment específico. Use DEPLOY=<deployment-name> NS=<namespace>
ifndef DEPLOY
	@echo "Usage: make pods-restart DEPLOY=<deployment-name> [NS=namespace]"
	@echo "Available deployments in namespace $(NS):"
	@$(KUBECTL) get deployments -n $(NS) --no-headers -o custom-columns=":metadata.name"
else
	@echo "==> Reiniciando pods do deployment $(DEPLOY) no namespace $(NS)"
	$(KUBECTL) rollout restart deployment/$(DEPLOY) -n $(NS)
	$(KUBECTL) rollout status deployment/$(DEPLOY) -n $(NS) --timeout=300s
endif

pods-scale: ## Escala um deployment. Use DEPLOY=<deployment-name> REPLICAS=<number> NS=<namespace>
ifndef DEPLOY
	@echo "Usage: make pods-scale DEPLOY=<deployment-name> REPLICAS=<number> [NS=namespace]"
	@echo "Available deployments in namespace $(NS):"
	@$(KUBECTL) get deployments -n $(NS) --no-headers -o custom-columns=":metadata.name"
else ifndef REPLICAS
	@echo "Usage: make pods-scale DEPLOY=<deployment-name> REPLICAS=<number> [NS=namespace]"
	@echo "Current replicas for $(DEPLOY):"
	@$(KUBECTL) get deployment $(DEPLOY) -n $(NS) -o jsonpath='{.spec.replicas}'
	@echo
else
	@echo "==> Escalando deployment $(DEPLOY) para $(REPLICAS) réplicas no namespace $(NS)"
	$(KUBECTL) scale deployment/$(DEPLOY) --replicas=$(REPLICAS) -n $(NS)
	$(KUBECTL) rollout status deployment/$(DEPLOY) -n $(NS) --timeout=300s
endif

pods-exec: ## Executa bash em um pod específico. Use POD=<nome-do-pod> NS=<namespace>
ifndef POD
	@echo "Usage: make pods-exec POD=<pod-name> [NS=namespace]"
	@echo "Available pods in namespace $(NS):"
	@$(KUBECTL) get pods -n $(NS) --no-headers -o custom-columns=":metadata.name"
else
	@echo "==> Executando bash no pod $(POD) no namespace $(NS)"
	$(KUBECTL) exec -it $(POD) -n $(NS) -- /bin/bash || $(KUBECTL) exec -it $(POD) -n $(NS) -- /bin/sh
endif

pods-status: ## Mostra status detalhado de todos os pods do namespace
	@echo "==> Status dos pods no namespace: $(NS)"
	@echo "Pods por status:"
	@$(KUBECTL) get pods -n $(NS) --no-headers | awk '{print $$3}' | sort | uniq -c
	@echo
	@echo "Pods detalhados:"
	@$(KUBECTL) get pods -n $(NS) -o custom-columns="NAME:.metadata.name,STATUS:.status.phase,READY:.status.containerStatuses[*].ready,RESTARTS:.status.containerStatuses[*].restartCount,AGE:.metadata.creationTimestamp"

pods-events: ## Mostra eventos relacionados aos pods do namespace
	@echo "==> Eventos dos pods no namespace: $(NS)"
	$(KUBECTL) get events -n $(NS) --sort-by='.lastTimestamp' | grep -i pod || echo "Nenhum evento de pod encontrado"

# -----------------------------
# ArgoCD Installation and Management
# -----------------------------
.PHONY: argo-install argo-pwd argo-port argo-repos argo-app-deploy argo-app-delete
argo-repos: ## Add ArgoCD Helm repository
	helm repo add argo https://argoproj.github.io/argo-helm
	helm repo update

argo-install: argo-repos ## Install ArgoCD
	$(KUBECTL) create namespace $(ARGOCD_NS) --dry-run=client -o yaml | $(KUBECTL) apply -f -
	helm upgrade --install argocd argo/argo-cd -n $(ARGOCD_NS) -f $(ARGO_VALUES) --wait --timeout=10m
	$(KUBECTL) -n $(ARGOCD_NS) rollout status deploy/argocd-server --timeout=300s

argo-app-deploy: ## Deploy hello-app ArgoCD Application
	@echo "==> Deploying ArgoCD hello-app..."
	$(KUBECTL) apply -f k8s/argo/apps/hello-app.yaml
	@echo "✓ ArgoCD hello-app deployed"

argo-app-delete: ## Delete hello-app ArgoCD Application
	@echo "==> Deleting ArgoCD hello-app..."
	-$(KUBECTL) delete -f k8s/argo/apps/hello-app.yaml
	@echo "✓ ArgoCD hello-app deleted"

argo-pwd: ## Get ArgoCD admin password
	@echo "ArgoCD admin password:"
	@$(KUBECTL) -n $(ARGOCD_NS) get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d; echo

argo-port: ## Port forward ArgoCD UI
	@echo "Access ArgoCD UI at $(ARGOCD_ENDPOINT)"
	$(KUBECTL) -n $(ARGOCD_NS) port-forward svc/argocd-server 8080:80

# -----------------------------
# Terraform Commands
# -----------------------------
.PHONY: tf-init tf-plan tf-apply tf-destroy tf-output
tf-init: ## Initialize Terraform
	cd $(TF_DIR) && terraform init

tf-plan: ## Plan Terraform changes
	cd $(TF_DIR) && terraform plan

tf-apply: ## Apply Terraform configuration
	cd $(TF_DIR) && terraform apply -auto-approve

tf-destroy: ## Destroy Terraform resources
	cd $(TF_DIR) && terraform destroy -auto-approve

tf-output: ## Show Terraform outputs
	cd $(TF_DIR) && terraform output

# -----------------------------
# Health Checks and Status
# -----------------------------
.PHONY: check status health compose-health k8s-health kong-check prom-targets argo-health tf-state localstack-health

compose-health: ## Check Docker Compose services health
	@echo "==> Checking Docker Compose services..."
	@cd $(COMPOSE_DIR) && docker compose ps --format "table {{.Name}}\t{{.State}}\t{{.Status}}" || echo "Docker Compose not running"

k8s-health: ## Check Kubernetes cluster health
	@echo "==> Checking Kubernetes cluster..."
	@echo "== K8s =="
	@$(KUBECTL) get nodes
	@$(KUBECTL) rollout status deploy/hello -n default --timeout=30s || echo "⚠ Hello app not deployed"

kong-check: ## Check Kong API Gateway
	@echo "==> Checking Kong API Gateway..."
	@echo "== Service hello via NodePort =="
	@curl -sf http://localhost:30080 2>/dev/null | grep -q "hello-world" && echo "✓ NodePort OK" || echo "⚠ NodePort not available"
	@echo "== Kong /hello =="
	@curl -sf $(KONG_ENDPOINT)/hello 2>/dev/null | grep -q "hello-world" && echo "✓ Kong OK" || echo "⚠ Kong not available"

prom-targets: ## Check Prometheus targets
	@echo "==> Checking Prometheus targets..."
	@curl -sf $(PROMETHEUS_ENDPOINT)/api/v1/targets 2>/dev/null | grep -q '"health":"up"' && echo "✓ Prometheus targets UP" || echo "⚠ Some Prometheus targets may be down"

argo-health: ## Check ArgoCD deployment
	@echo "==> Checking ArgoCD..."
	@echo "== Argo CD deploy =="
	@$(KUBECTL) -n $(ARGOCD_NS) get deploy argocd-server >/dev/null 2>&1 && echo "✓ ArgoCD OK" || echo "⚠ ArgoCD not deployed"

tf-state: ## Check Terraform state
	@echo "==> Checking Terraform state..."
	@echo "== Terraform state =="
	@cd $(TF_DIR) && terraform state list 2>/dev/null || echo "⚠ Terraform not initialized or no resources"

localstack-health: ## Check LocalStack health
	@echo "==> Checking LocalStack..."
	@echo "== LocalStack health =="
	@curl -sf http://localhost:4566/_localstack/health >/dev/null 2>&1 && echo "✓ LocalStack OK" || echo "⚠ LocalStack not available"

check: ## Smoke tests (K8s, Kong, Prometheus, Argo, Terraform)
	@echo "==> Running comprehensive smoke tests..."
	@echo "==> Checking Docker Compose services..."
	@cd $(COMPOSE_DIR) && docker compose ps --format "table {{.Name}}\t{{.State}}\t{{.Status}}" || echo "Docker Compose not running"
	@echo "==> Checking Kubernetes cluster..."
	@echo "== K8s =="
	@$(KUBECTL) get nodes || echo "⚠ Kind cluster not available"
	@$(KUBECTL) rollout status deploy/hello -n default --timeout=30s || echo "⚠ Hello app not deployed"
	@echo "==> Checking Kong API Gateway..."
	@echo "== Service hello via NodePort =="
	@curl -sf http://localhost:30080 2>/dev/null | grep -q "hello-world" && echo "✓ NodePort OK" || echo "⚠ NodePort not available"
	@echo "== Kong /hello =="
	@curl -sf $(KONG_ENDPOINT)/hello 2>/dev/null | grep -q "hello-world" && echo "✓ Kong OK" || echo "⚠ Kong not available"
	@echo "==> Checking Prometheus targets..."
	@curl -sf $(PROMETHEUS_ENDPOINT)/api/v1/targets 2>/dev/null | grep -q '"health":"up"' && echo "✓ Prometheus targets UP" || echo "⚠ Some Prometheus targets may be down"
	@echo "==> Checking ArgoCD..."
	@echo "== Argo CD deploy =="
	@$(KUBECTL) -n $(ARGOCD_NS) get deploy argocd-server >/dev/null 2>&1 && echo "✓ ArgoCD OK" || echo "⚠ ArgoCD not deployed"
	@echo "==> Checking LocalStack..."
	@echo "== LocalStack health =="
	@curl -sf http://localhost:4566/_localstack/health >/dev/null 2>&1 && echo "✓ LocalStack OK" || echo "⚠ LocalStack not available"
	@echo "==> Checking Terraform state..."
	@echo "== Terraform state =="
	@cd $(TF_DIR) && terraform state list 2>/dev/null || echo "⚠ Terraform not initialized or no resources"
	@echo "Tudo certo ✅"

status: ## Show infrastructure status
	@echo "\n==> Docker Compose Services:"
	@cd $(COMPOSE_DIR) && docker compose ps || echo "Docker Compose not running"
	@echo "\n==> Kubernetes Cluster:"
	@$(KUBECTL) get nodes 2>/dev/null || echo "Kind cluster not available"
	@echo "\n==> Kubernetes Pods:"
	@$(KUBECTL) get pods -A 2>/dev/null || echo "No pods found"
	@echo "\n==> LocalStack Services:"
	@curl -s http://localhost:4566/_localstack/health 2>/dev/null | jq . 2>/dev/null || echo "LocalStack not available"

health: check ## Alias for check command

# -----------------------------
# Complete Workflows
# -----------------------------
.PHONY: up full-up dev-up
up: compose-up ## Start infrastructure (alias for compose-up)

full-up: compose-up kind-up app-deploy argo-install tf-apply check ## Full infrastructure setup
	@echo "\n==> Full infrastructure setup completed!"
	@echo "Services available:"
	@echo "  - Kong: $(KONG_ENDPOINT)"
	@echo "  - Prometheus: $(PROMETHEUS_ENDPOINT)"
	@echo "  - Grafana: http://localhost:3000 (admin/admin)"
	@echo "  - Jaeger: http://localhost:16686"
	@echo "  - Keycloak: http://localhost:8085"
	@echo "  - LocalStack: http://localhost:4566"
	@echo "\nRun 'make argo-pwd' to get ArgoCD password"
	@echo "Run 'make argo-port' to access ArgoCD UI"

dev-up: compose-up kind-up app-deploy ## Development setup (without ArgoCD and Terraform)
	@echo "\n==> Development setup completed!"

# -----------------------------
# Cleanup Commands
# -----------------------------
.PHONY: down clean nuke
down: ## Stop infrastructure services
	@echo "==> Stopping Docker services..."
	@cd $(COMPOSE_DIR) && docker compose down -v || true
	@echo "==> Deleting Kind cluster..."
	@kind delete cluster --name $(KIND_CLUSTER) || true

clean: down ## Clean infrastructure (alias for down)

nuke: down ## Complete cleanup including Terraform state
	@echo "==> Removing Terraform state..."
	@rm -rf $(TF_DIR)/.terraform $(TF_DIR)/terraform.tfstate*
	@echo "==> Removing Docker images..."
	@docker system prune -f
	@echo "==> Complete cleanup finished!"

# -----------------------------
# Development Helpers
# -----------------------------
.PHONY: logs restart-service
logs: compose-logs ## Show service logs (alias for compose-logs)

restart-service: ## Restart a specific service (usage: make restart-service SERVICE=postgres)
ifndef SERVICE
	@echo "Usage: make restart-service SERVICE=<service-name>"
	@echo "Available services:"
	@cd $(COMPOSE_DIR) && docker compose config --services
else
	cd $(COMPOSE_DIR) && docker compose restart $(SERVICE)
endif

# Default target
.DEFAULT_GOAL := help

# Mark all phony targets
.PHONY: all up kind app argo tf check down nuke
