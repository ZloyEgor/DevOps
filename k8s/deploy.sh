#!/bin/bash

set -e

echo "Starting deployment process..."

# Check if kubectl is configured
if ! kubectl cluster-info &> /dev/null; then
    echo "Error: kubectl is not configured or cluster is not accessible"
    exit 1
fi

# Create namespace
echo "Creating namespace..."
kubectl apply -f namespace.yaml

# Deploy PostgreSQL
echo "Deploying PostgreSQL..."
kubectl apply -f postgresql.yaml

# Wait for PostgreSQL to be ready
echo "Waiting for PostgreSQL to be ready..."
kubectl wait --for=condition=ready pod -l app=postgres -n cvetochey --timeout=300s

# Deploy backend
echo "Deploying backend..."
kubectl apply -f backend.yaml

# Wait for backend to be ready
echo "Waiting for backend to be ready..."
kubectl wait --for=condition=ready pod -l app=backend -n cvetochey --timeout=300s

# Deploy frontend
echo "Deploying frontend..."
kubectl apply -f frontend.yaml

# Wait for frontend to be ready
echo "Waiting for frontend to be ready..."
kubectl wait --for=condition=ready pod -l app=frontend -n cvetochey --timeout=300s

# Deploy ingress
echo "Deploying ingress..."
kubectl apply -f ingress.yaml

# Setup monitoring
echo "Setting up monitoring..."
bash setup-monitoring.sh

echo "Deployment complete!"
echo "Services status:"
kubectl get pods -n cvetochey
echo ""
kubectl get services -n cvetochey
echo ""
echo "HPA status:"
kubectl get hpa -n cvetochey
