SHELL := /bin/bash
KIND_CLUSTER_NAME := algaeats
KIND_CFG := k8s/kind/cluster.yaml

.PHONY: all
all: kind-up argocd install-addons compose-up tf-apply info

.PHONY: kind-up
kind-up:
	kind create cluster --name $(KIND_CLUSTER_NAME) --config $(KIND_CFG)

.PHONY: kind-down
kind-down:
	kind delete cluster --name $(KIND_CLUSTER_NAME)

.PHONY: argocd
argocd:
	kubectl create ns argocd || true
	kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
	kubectl apply -f k8s/argocd/bootstrap.yaml
	kubectl apply -f k8s/argocd/apps/infra-placeholder.yaml

.PHONY: argocd-pwd
argocd-pwd:
	@echo "Argo CD admin password:"
	kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath='{.data.password}' | base64 -d; echo

.PHONY: argocd-port
argocd-port:
	kubectl -n argocd port-forward svc/argocd-server 8088:443

.PHONY: install-addons
install-addons:
	helm repo add kong https://charts.konghq.com
	helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
	helm repo add grafana https://grafana.github.io/helm-charts
	helm repo add kedacore https://kedacore.github.io/charts
	helm repo add istio https://istio-release.storage.googleapis.com/charts
	helm repo update
	helm upgrade --install kong kong/kong -n kong --create-namespace -f k8s/addons/helmfile/values/kong-values.yaml
	helm upgrade --install kube-prometheus-stack prometheus-community/kube-prometheus-stack -n monitoring --create-namespace -f k8s/addons/helmfile/values/prometheus-values.yaml
	helm upgrade --install loki grafana/loki-stack -n monitoring -f k8s/addons/helmfile/values/loki-values.yaml
	helm upgrade --install tempo grafana/tempo -n monitoring -f k8s/addons/helmfile/values/tempo-values.yaml
	helm upgrade --install keda kedacore/keda -n keda --create-namespace -f k8s/addons/helmfile/values/keda-values.yaml
	helm upgrade --install istio-base istio/base -n istio-system --create-namespace
	helm upgrade --install istiod istio/istiod -n istio-system
	helm upgrade --install istio-ingress istio/gateway -n istio-ingress --create-namespace

.PHONY: compose-up
compose-up:
	cd compose && docker compose -f docker-compose.local.yml up -d

.PHONY: compose-down
compose-down:
	cd compose && docker compose -f docker-compose.local.yml down -v

.PHONY: tf-apply
tf-apply:
	cd terraform/localstack && terraform init && terraform apply -auto-approve

.PHONY: info
info:
	@echo "ArgoCD UI: https://localhost:8088 (use 'make argocd-port')"
	@echo "Grafana (NodePort): kubectl -n monitoring get svc kube-prometheus-stack-grafana"
	@echo "Kong (NodePort 80 => 30080 via kind): http://api.local"
	@echo "Redpanda Console: http://localhost:8081"
	@echo "MinIO Console: http://localhost:9001 (minio/minio123)"
	@echo "Keycloak: http://localhost:8080 (admin/admin)"
	@echo "LocalStack: http://localhost:4566"
