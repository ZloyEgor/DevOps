# CvetOchey Complete Deployment Guide

## 📋 Overview

This guide contains all essential commands for deploying the CvetOchey application to Yandex Cloud with Kubernetes, monitoring, and CI/CD.

## 🔧 Prerequisites

1. **Yandex Cloud CLI** installed and configured
2. **Docker** with buildx support
3. **kubectl** installed
4. **Docker Compose** for local Grafana

## 📦 Container Registry Setup

### 1. Install and Configure Yandex Cloud CLI

```bash
# Install Yandex Cloud CLI
curl -sSL https://storage.yandexcloud.net/yandexcloud-yc/install.sh | bash
source ~/.zshrc

# Initialize configuration (interactive)
yc init

# Verify configuration
yc config list
```

### 2. Create Container Registry

```bash
# Create registry
yc container registry create --name cvetochey-registry

# Get registry ID
yc container registry list

# Configure Docker authentication
yc container registry configure-docker
```

**Registry ID:** `crpqt390b8gk59ipqid8`

## 🐳 Docker Image Management

### Build and Push Images

#### Backend (Multi-Architecture)
```bash
# Build and push backend with multi-arch support
cd backend
docker buildx build \
    --platform linux/amd64,linux/arm64 \
    --tag cr.yandex/crpqt390b8gk59ipqid8/cvetochey-backend:latest \
    --push \
    .
```

#### Frontend (Single Architecture - Due to Memory Constraints)
```bash
# Build and push frontend
cd frontend
docker build -t cr.yandex/crpqt390b8gk59ipqid8/cvetochey-frontend:latest .
docker push cr.yandex/crpqt390b8gk59ipqid8/cvetochey-frontend:latest
```

#### Automated Multi-Architecture Build Script
```bash
# Use the automated script for both services
chmod +x build-and-push.sh
./build-and-push.sh
```

### Verify Images
```bash
# List all images in registry
yc container image list --folder-id b1gs0cg1voiht42pp513

# Inspect multi-arch manifest
docker buildx imagetools inspect cr.yandex/crpqt390b8gk59ipqid8/cvetochey-backend:latest
```

## ☸️ Kubernetes Deployment

### 1. Create Kubernetes Cluster

```bash
# Make script executable
chmod +x k8s/cluster-setup.sh

# Create cluster (takes ~10-15 minutes)
./k8s/cluster-setup.sh

# Verify cluster
kubectl cluster-info
kubectl get nodes
```

### 2. Deploy Applications

```bash
# Make deployment script executable
chmod +x k8s/deploy.sh

# Deploy all services
cd k8s
./deploy.sh
```

#### Manual Deployment Steps
```bash
# 1. Create namespace
kubectl apply -f namespace.yaml

# 2. Deploy PostgreSQL
kubectl apply -f postgresql.yaml
kubectl wait --for=condition=ready pod -l app=postgres -n cvetochey --timeout=300s

# 3. Deploy backend
kubectl apply -f backend.yaml
kubectl wait --for=condition=ready pod -l app=backend -n cvetochey --timeout=300s

# 4. Deploy frontend
kubectl apply -f frontend.yaml
kubectl wait --for=condition=ready pod -l app=frontend -n cvetochey --timeout=300s

# 5. Deploy ingress
kubectl apply -f ingress.yaml
```

### 3. Verify Deployment

```bash
# Check all resources
kubectl get all -n cvetochey

# Check HPA status
kubectl get hpa -n cvetochey

# Check pod logs
kubectl logs -f deployment/backend -n cvetochey
kubectl logs -f deployment/frontend -n cvetochey

# Check services
kubectl get services -n cvetochey
```

## 📊 Monitoring Setup

### 1. Deploy Prometheus in Kubernetes

```bash
# Deploy Prometheus
kubectl apply -f prometheus.yaml

# Wait for Prometheus to be ready
kubectl wait --for=condition=ready pod -l app=prometheus -n monitoring --timeout=300s

# Get Prometheus external IP
kubectl get service prometheus-service -n monitoring
```

### 2. Setup Grafana Locally

```bash
# Get Prometheus external IP
PROMETHEUS_IP=$(kubectl get service prometheus-service -n monitoring -o jsonpath='{.status.loadBalancer.ingress[0].ip}')

# Update Grafana datasource configuration
sed -i "s/YOUR_PROMETHEUS_EXTERNAL_IP/$PROMETHEUS_IP/g" k8s/grafana/provisioning/datasources/prometheus.yml

# Start Grafana locally
docker-compose -f k8s/grafana-local.yaml up -d

# Access Grafana at http://localhost:3001
# Login: admin / admin123
```

### 3. Monitoring Setup Script

```bash
# Make monitoring script executable
chmod +x k8s/setup-monitoring.sh

# Run automated monitoring setup
./k8s/setup-monitoring.sh
```

## 🔄 Horizontal Pod Autoscaler (HPA)

### HPA Configuration
- **Target CPU**: 15%
- **Min Replicas**: 2
- **Max Replicas**: 10
- **Scale Up**: 100% increase every 15 seconds
- **Scale Down**: 10% decrease every 60 seconds

### HPA Commands
```bash
# Check HPA status
kubectl get hpa -n cvetochey

# Watch HPA scaling in real-time
kubectl get hpa -n cvetochey -w

# Describe HPA for detailed information
kubectl describe hpa backend-hpa -n cvetochey

# Check current metrics
kubectl top pods -n cvetochey
```

## 🧪 Load Testing

### Deploy Load Test
```bash
# Apply load test configuration
kubectl apply -f load-test.yaml

# Monitor the test
kubectl logs -f pod/load-test -n cvetochey

# Watch HPA scaling during load test
kubectl get hpa -n cvetochey -w
```

### Manual Load Testing with curl
```bash
# Get backend service IP
BACKEND_IP=$(kubectl get service backend-service -n cvetochey -o jsonpath='{.status.loadBalancer.ingress[0].ip}')

# Generate load
for i in {1..1000}; do
  curl -s http://$BACKEND_IP:8080/actuator/health &
done
```

## 🚀 CI/CD Pipeline

### GitHub Secrets Required
Add these secrets to your GitHub repository:

```
YC_SERVICE_ACCOUNT_KEY: <base64-encoded-service-account-key>
YC_REGISTRY_ID: crpqt390b8gk59ipqid8
YC_CLOUD_ID: b1g1hh4a73qpbdr1kbbf
YC_FOLDER_ID: b1gs0cg1voiht42pp513
```

### Create Service Account for CI/CD
```bash
# Create service account
yc iam service-account create --name github-actions

# Get service account ID
SA_ID=$(yc iam service-account get github-actions --format json | jq -r '.id')

# Assign roles
yc resource-manager folder add-access-binding b1gs0cg1voiht42pp513 \
  --role container-registry.images.pusher \
  --subject serviceAccount:$SA_ID

yc resource-manager folder add-access-binding b1gs0cg1voiht42pp513 \
  --role k8s.cluster-api.cluster-admin \
  --subject serviceAccount:$SA_ID

# Create service account key
yc iam key create --service-account-id $SA_ID --output key.json

# Encode key for GitHub secret
base64 -i key.json
```

## 🔍 Troubleshooting Commands

### Cluster Issues
```bash
# Check cluster status
kubectl cluster-info

# Check node status
kubectl get nodes -o wide

# Check cluster events
kubectl get events --sort-by='.lastTimestamp' -A

# Check cluster resources
kubectl top nodes
kubectl top pods -A
```

### Application Issues
```bash
# Check pod status
kubectl get pods -n cvetochey -o wide

# Check pod logs
kubectl logs -f <pod-name> -n cvetochey

# Describe problematic pod
kubectl describe pod <pod-name> -n cvetochey

# Check service endpoints
kubectl get endpoints -n cvetochey

# Port forward for debugging
kubectl port-forward service/backend-service 8080:8080 -n cvetochey
kubectl port-forward service/frontend-service 3000:3000 -n cvetochey
```

### Database Issues
```bash
# Check PostgreSQL pod
kubectl logs -f deployment/postgres -n cvetochey

# Connect to PostgreSQL
kubectl exec -it deployment/postgres -n cvetochey -- psql -U postgres -d cvetochey

# Check database connection from backend
kubectl exec -it deployment/backend -n cvetochey -- curl localhost:8080/actuator/health
```

### Monitoring Issues
```bash
# Check Prometheus
kubectl logs -f deployment/prometheus -n monitoring
kubectl port-forward service/prometheus-service 9090:9090 -n monitoring

# Check metrics endpoint
kubectl exec -it deployment/backend -n cvetochey -- curl localhost:8080/actuator/prometheus
```

## 🔧 Maintenance Commands

### Update Images
```bash
# Update backend image
kubectl set image deployment/backend backend=cr.yandex/crpqt390b8gk59ipqid8/cvetochey-backend:latest -n cvetochey
kubectl rollout status deployment/backend -n cvetochey

# Update frontend image
kubectl set image deployment/frontend frontend=cr.yandex/crpqt390b8gk59ipqid8/cvetochey-frontend:latest -n cvetochey
kubectl rollout status deployment/frontend -n cvetochey
```

### Rollback Deployment
```bash
# Check rollout history
kubectl rollout history deployment/backend -n cvetochey

# Rollback to previous version
kubectl rollout undo deployment/backend -n cvetochey

# Rollback to specific revision
kubectl rollout undo deployment/backend --to-revision=2 -n cvetochey
```

### Scale Applications
```bash
# Manually scale backend
kubectl scale deployment backend --replicas=5 -n cvetochey

# Manually scale frontend
kubectl scale deployment frontend --replicas=3 -n cvetochey
```

### Clean Up Resources
```bash
# Delete specific deployment
kubectl delete deployment <deployment-name> -n cvetochey

# Delete entire namespace (careful!)
kubectl delete namespace cvetochey

# Delete cluster (careful!)
yc managed-kubernetes cluster delete <cluster-id>
```

## 📊 Monitoring Endpoints

- **Prometheus**: `http://<prometheus-external-ip>:9090`
- **Grafana**: `http://localhost:3001` (admin/admin123)
- **Backend Metrics**: `http://<backend-ip>:8080/actuator/prometheus`
- **Backend Health**: `http://<backend-ip>:8080/actuator/health`

## 📝 Important Notes

1. **Multi-Architecture**: Backend supports both AMD64 and ARM64, frontend currently AMD64 only
2. **Memory Constraints**: Frontend multi-arch builds may fail due to memory limits in buildx
3. **Database**: PostgreSQL data is persisted using PVC
4. **Security**: All services run as non-root users
5. **Health Checks**: All containers have proper health checks configured
6. **Monitoring**: Enhanced HTTP metrics for endpoint tracking in Grafana

## 🔗 Useful Links

- [Yandex Cloud Documentation](https://cloud.yandex.ru/docs/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
