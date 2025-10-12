# Default shell
SHELL := /bin/zsh

# -----------------------------
# Variables
# -----------------------------
# Docker Compose files
COMPOSE_DIR := compose
COMPOSE_FILE := $(COMPOSE_DIR)/docker-compose.yml
COMPOSE_FILE_OBS := $(COMPOSE_DIR)/docker-compose-observability.yml
COMPOSE_FILE_TOOLS := $(COMPOSE_DIR)/docker-compose-tools.yml
COMPOSE_FILE_OTEL := $(COMPOSE_DIR)/docker-compose-otel.yml
COMPOSE_FILE_COLLECTOR := $(COMPOSE_DIR)/docker-compose-collector.yml

# Terraform directory
TF_DIR := terraform/localstack

# Kubernetes / Kind
KIND_CLUSTER := wos-eats
K8S_CONTEXT := kind-$(KIND_CLUSTER)
KUBECTL := kubectl --context $(K8S_CONTEXT)

# Namespaces
ARGOCD_NS := argocd
MON_NS := monitoring
KONG_NS := kong
ISTIO_SYS_NS := istio-system
ISTIO_ING_NS := istio-ingress
KEDA_NS := keda

# Helm values dirs
HELM_VALUES_DIR := k8s/addons/helm-values
KONG_VALUES := $(HELM_VALUES_DIR)/kong-values.yaml
PROM_VALUES := $(HELM_VALUES_DIR)/prometheus-values.yaml
LOKI_VALUES := $(HELM_VALUES_DIR)/loki-values.yaml
TEMPO_VALUES := $(HELM_VALUES_DIR)/tempo-values.yaml
KEDA_VALUES := $(HELM_VALUES_DIR)/keda-values.yaml

# URLs and Endpoints
ARGOCD_URL := localhost:8088

# -----------------------------
# Help
# -----------------------------
.PHONY: help
help: ## Show this help message
	@echo 'Usage: make [target]'
	@echo ''
	@echo 'Targets:'
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z0-9_.-]+:.*?## / {printf "  \033[36m%-24s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

# -----------------------------
# Docker Compose Commands
# -----------------------------
.PHONY: compose-up compose-down compose-obs-up compose-tools-up compose-full-up compose-otel-up compose-collector-up
compose-up: ## Start base infrastructure services
	docker compose -f $(COMPOSE_FILE) up -d

compose-down: ## Stop all infrastructure services
	docker compose -f $(COMPOSE_FILE) -f $(COMPOSE_FILE_OBS) -f $(COMPOSE_FILE_TOOLS) -f $(COMPOSE_FILE_OTEL) -f $(COMPOSE_FILE_COLLECTOR) down -v

compose-obs-up: ## Start observability stack
	docker compose -f $(COMPOSE_FILE) -f $(COMPOSE_FILE_OBS) up -d

compose-tools-up: ## Start development tools
	docker compose -f $(COMPOSE_FILE) -f $(COMPOSE_FILE_TOOLS) up -d

compose-otel-up: ## Start local monitoring (node-exporter, cadvisor)
	docker compose -f $(COMPOSE_FILE_OTEL) up -d

compose-collector-up: ## Start standalone OTel collector (when not using K8s)
	docker compose -f $(COMPOSE_FILE_COLLECTOR) up -d

compose-full-up: ## Start all services (base + observability + tools + local monitoring)
	docker compose -f $(COMPOSE_FILE) -f $(COMPOSE_FILE_OBS) -f $(COMPOSE_FILE_TOOLS) -f $(COMPOSE_FILE_OTEL) up -d

# -----------------------------
# Kubernetes Cluster Management
# -----------------------------
.PHONY: kind-up kind-down
kind-up: ## Create Kind cluster
	kind create cluster --name $(KIND_CLUSTER) --config k8s/kind/cluster.yaml
	$(KUBECTL) cluster-info
	@echo "Enabling default namespace for Istio injection..."
	$(KUBECTL) label namespace default istio-injection=enabled --overwrite

kind-down: ## Delete Kind cluster
	kind delete cluster --name $(KIND_CLUSTER)

# -----------------------------
# ArgoCD Installation and Management
# -----------------------------
.PHONY: argocd argocd-pwd argocd-port
argocd: ## Install ArgoCD and apply bootstrap
	$(KUBECTL) create namespace $(ARGOCD_NS) --dry-run=client -o yaml | $(KUBECTL) apply -f -
	$(KUBECTL) apply -n $(ARGOCD_NS) -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
	@echo "Waiting for ArgoCD pods to be ready..."
	$(KUBECTL) wait --for=condition=Ready pods --all -n $(ARGOCD_NS) --timeout=600s || true
	# bootstrap da sua árvore (idempotente)
	$(KUBECTL) apply -f k8s/argocd/bootstrap.yaml

argocd-pwd: ## Get ArgoCD admin password
	@echo "ArgoCD Password:"
	@$(KUBECTL) -n $(ARGOCD_NS) get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d; echo

argocd-port: ## Port forward ArgoCD UI
	@echo "Access ArgoCD UI at https://$(ARGOCD_URL)"
	$(KUBECTL) -n $(ARGOCD_NS) port-forward svc/argocd-server 8088:443

# -----------------------------
# Helm Repos (idempotente)
# -----------------------------
.PHONY: addons-repos
addons-repos: ## Add/Update Helm repositories
	helm repo add kong https://charts.konghq.com
	helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
	helm repo add grafana https://grafana.github.io/helm-charts
	helm repo add kedacore https://kedacore.github.io/charts
	helm repo add istio https://istio-release.storage.googleapis.com/charts
	helm repo update

# -----------------------------
# Kubernetes Addons
# -----------------------------
.PHONY: install-addons
install-addons: addons-repos ## Install Kubernetes addons (Monitoring, Kong, Istio, KEDA, etc)
	@echo "==> Installing Kong"
	helm upgrade --install kong kong/kong -n $(KONG_NS) --create-namespace -f $(KONG_VALUES)

	@echo "==> Installing kube-prometheus-stack"
	helm upgrade --install kube-prometheus-stack prometheus-community/kube-prometheus-stack -n $(MON_NS) --create-namespace -f $(PROM_VALUES)

	@echo "==> Installing Loki"
	helm upgrade --install loki grafana/loki-stack -n $(MON_NS) -f $(LOKI_VALUES)

	@echo "==> Installing Tempo"
	helm upgrade --install tempo grafana/tempo -n $(MON_NS) -f $(TEMPO_VALUES)

	@echo "==> Installing KEDA"
	helm upgrade --install keda kedacore/keda -n $(KEDA_NS) --create-namespace -f $(KEDA_VALUES)

	@echo "==> Installing Istio"
	helm upgrade --install istio-base istio/base -n $(ISTIO_SYS_NS) --create-namespace
	helm upgrade --install istiod istio/istiod -n $(ISTIO_SYS_NS)
	helm upgrade --install istio-ingress istio/gateway -n $(ISTIO_ING_NS) --create-namespace

	@echo "==> Applying monitoring manifests (blackbox & Kong ServiceMonitor)"
	$(KUBECTL) apply -f k8s/monitoring/blackbox.yaml
	$(KUBECTL) apply -f k8s/monitoring/kong-servicemonitor.yaml || true

	@echo "==> Waiting rollouts (best-effort)"
	- $(KUBECTL) rollout status deploy/kong-kong -n $(KONG_NS) --timeout=600s
	- $(KUBECTL) rollout status deploy/kube-prometheus-stack-operator -n $(MON_NS) --timeout=600s
	- $(KUBECTL) rollout status deploy/kube-prometheus-stack-grafana  -n $(MON_NS) --timeout=600s
	- $(KUBECTL) rollout status statefulset/alertmanager-kube-prometheus-stack-alertmanager -n $(MON_NS) --timeout=600s
	- $(KUBECTL) rollout status statefulset/prometheus-kube-prometheus-stack-prometheus     -n $(MON_NS) --timeout=600s
	- $(KUBECTL) rollout status deploy/loki -n $(MON_NS) --timeout=600s
	- $(KUBECTL) rollout status deploy/tempo -n $(MON_NS) --timeout=600s
	- $(KUBECTL) rollout status deploy/keda-operator -n $(KEDA_NS) --timeout=600s
	- $(KUBECTL) rollout status deploy/istiod -n $(ISTIO_SYS_NS) --timeout=600s

# -----------------------------
# Terraform Commands
# -----------------------------
.PHONY: tf-init tf-apply tf-destroy
tf-init: ## Initialize Terraform
	cd $(TF_DIR) && terraform init

tf-apply: ## Apply Terraform configuration
	cd $(TF_DIR) && terraform apply -auto-approve

tf-destroy: ## Destroy Terraform resources
	cd $(TF_DIR) && terraform destroy -auto-approve

# -----------------------------
# Host Configuration
# -----------------------------
.PHONY: hosts-hint
hosts-hint: ## Show required /etc/hosts entries
	@echo "Add the following entries to /etc/hosts:"
	@echo "127.0.0.1 api.local"

# -----------------------------
# Status Commands
# -----------------------------
.PHONY: status
status: ## Show infrastructure status
	@echo "\nKubernetes Pods Status:"
	@$(KUBECTL) get pods -A
	@echo "\nDocker Compose Services:"
	@docker compose -f $(COMPOSE_FILE) -f $(COMPOSE_FILE_OBS) -f $(COMPOSE_FILE_TOOLS) -f $(COMPOSE_FILE_OTEL) -f $(COMPOSE_FILE_COLLECTOR) ps
	@echo "\nLocalStack Status:"
	@curl -s http://localhost:4566/_localstack/health | jq || echo "LocalStack health endpoint not available"
