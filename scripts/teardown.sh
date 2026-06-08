#!/bin/bash

set -e

echo "Eliminando recursos..."

kubectl delete -f k8s/monitoring/library-alerts.yaml \
  --ignore-not-found=true

kubectl delete -f k8s/monitoring/catalog-servicemonitor.yaml \
  --ignore-not-found=true

kubectl delete -f k8s/monitoring/loan-servicemonitor.yaml \
  --ignore-not-found=true

kubectl delete -f k8s/ \
  --ignore-not-found=true

echo ""
echo "Desinstalando stack de monitoreo..."

helm uninstall monitoring \
  -n monitoring || true

kubectl delete namespace monitoring \
  --ignore-not-found=true

if [ "$1" = "--delete-cluster" ]; then

  echo ""
  echo "Eliminando cluster Minikube..."

  minikube delete

fi

echo ""
echo "Teardown completado."