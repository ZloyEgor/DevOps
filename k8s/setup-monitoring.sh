#!/bin/bash

echo "Setting up monitoring stack..."

# Create monitoring namespace
kubectl apply -f prometheus.yaml

# Wait for Prometheus to be ready
echo "Waiting for Prometheus to be ready..."
kubectl wait --for=condition=ready pod -l app=prometheus -n monitoring --timeout=300s

# Get Prometheus external IP
PROMETHEUS_IP=$(kubectl get service prometheus-service -n monitoring -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
echo "Prometheus will be available at: http://$PROMETHEUS_IP:9090"

# Update Grafana datasource with Prometheus IP
sed -i "s/YOUR_PROMETHEUS_EXTERNAL_IP/$PROMETHEUS_IP/g" k8s/grafana/provisioning/datasources/prometheus.yml

echo "Monitoring setup complete!"
echo "You can now start Grafana locally with: docker-compose -f k8s/grafana-local.yaml up -d"
