#!/bin/bash

set -e

echo "========================================="
echo "Library Monitoring Project Setup"
echo "========================================="

echo ""
echo "[1/9] Verificando prerequisitos..."

command -v docker >/dev/null 2>&1 || {
  echo "Docker no instalado"
  exit 1
}

command -v kubectl >/dev/null 2>&1 || {
  echo "kubectl no instalado"
  exit 1
}

command -v helm >/dev/null 2>&1 || {
  echo "Helm no instalado"
  exit 1
}

command -v minikube >/dev/null 2>&1 || {
  echo "Minikube no instalado"
  exit 1
}

echo ""
echo "[2/9] Iniciando Minikube..."

minikube start \
  --cpus=2 \
  --memory=4096

echo ""
echo "[3/9] Habilitando metrics-server..."

minikube addons enable metrics-server

echo ""
echo "[4/9] Configurando Docker para Minikube..."

eval $(minikube docker-env)

echo ""
echo "[5/9] Construyendo Book Catalog Service..."

cd microservices/book-catalog-service

docker build \
  -t book-catalog-service:1.0 .

cd ../..

echo ""
echo "[6/9] Construyendo Loan Service..."

cd microservices/loan-service

docker build \
  -t loan-service:1.0 .

cd ../..

echo ""
echo "[7/9] Instalando kube-prometheus-stack..."

kubectl create namespace monitoring \
  --dry-run=client -o yaml | kubectl apply -f -

helm repo add prometheus-community \
  https://prometheus-community.github.io/helm-charts

helm repo update

helm upgrade --install monitoring \
  prometheus-community/kube-prometheus-stack \
  -n monitoring \
  -f k8s/monitoring/values.yaml

echo ""
echo "[8/9] Aplicando manifiestos Kubernetes..."

kubectl apply -f k8s/

echo ""
echo "[9/9] Aplicando monitoreo..."

kubectl apply -f k8s/monitoring/catalog-servicemonitor.yaml
kubectl apply -f k8s/monitoring/loan-servicemonitor.yaml
kubectl apply -f k8s/monitoring/library-alerts.yaml

echo ""
echo "Esperando pods..."

kubectl wait \
  --for=condition=Ready pod \
  --all \
  -n library-system \
  --timeout=300s

echo ""
echo "========================================="
echo "SISTEMA DESPLEGADO"
echo "========================================="

echo ""
echo "Catalog Service:"
minikube service \
  book-catalog-service \
  -n library-system \
  --url

echo ""
echo "Loan Service:"
minikube service \
  loan-service \
  -n library-system \
  --url

echo ""
echo "Grafana:"
minikube service \
  monitoring-grafana \
  -n monitoring \
  --url

echo ""
echo "Prometheus:"
minikube service \
  monitoring-kube-prometheus-prometheus \
  -n monitoring \
  --url