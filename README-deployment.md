# CvetOchey Kubernetes Deployment Guide

This guide will help you deploy the CvetOchey application to Yandex Cloud Kubernetes.

## Prerequisites

1. Yandex Cloud CLI installed and configured
2. Docker installed
3. kubectl installed
4. Access to Yandex Container Registry

## Step 1: Create Container Registry

```bash
# Create container registry
yc container registry create --name cvetochey-registry

# Get registry ID
yc container registry list
```

## Step 2: Build and Push Docker Images

```bash
# Build and push backend
cd backend
docker build -t cr.yandex/YOUR_REGISTRY_ID/cvetochey-backend:latest .
docker push cr.yandex/YOUR_REGISTRY_ID/cvetochey-backend:latest

# Build and push frontend
cd ../frontend
docker build -t cr.yandex/YOUR_REGISTRY_ID/cvetochey-frontend:latest .
docker push cr.yandex/YOUR_REGISTRY_ID/cvetochey-frontend:latest
```

## Step 3: Create Kubernetes Cluster

```bash
# Make the cluster setup script executable
chmod +x k8s/cluster-setup.sh

# Run the cluster setup
./k8s/cluster-setup.sh
```

## Step 4: Update Image References

Update the image references in `k8s/backend.yaml` and `k8s/frontend.yaml` with your registry ID:

```yaml
image: cr.yandex/YOUR_REGISTRY_ID/cvetochey-backend:latest
```

## Step 5: Deploy Applications

```bash
# Make deployment script executable
chmod +x k8s/deploy.sh

# Deploy everything
cd k8s
./deploy.sh
```

## Step 6: Set up Grafana Locally

```bash
# Start Grafana
docker-compose -f k8s/grafana-local.yaml up -d

# Access Grafana at http://localhost:3001
# Login: admin / admin123
```

## Step 7: Run Load Tests

```bash
# Apply load test configuration
kubectl apply -f k8s/load-test.yaml

# Monitor the test
kubectl logs -f pod/load-test -n cvetochey

# Check HPA scaling
kubectl get hpa -n cvetochey -w
```

## Step 8: Set up CI/CD

1. Create a service account in Yandex Cloud:
```bash
yc iam service-account create --name github-actions
yc resource-manager folder add-access-binding YOUR_FOLDER_ID \
  --role container-registry.images.pusher \
  --subject serviceAccount:YOUR_SERVICE_ACCOUNT_ID
```

2. Create a key for the service account:
```bash
yc iam key create --service-account-id YOUR_SERVICE_ACCOUNT_ID --output key.json
```

3. Add these secrets to your GitHub repository:
   - `YC_SERVICE_ACCOUNT_KEY`: Content of key.json (base64 encoded)
   - `YC_REGISTRY_ID`: Your container registry ID
   - `YC_CLOUD_ID`: Your cloud ID
   - `YC_FOLDER_ID`: Your folder ID

## Monitoring Endpoints

- **Prometheus**: `http://PROMETHEUS_EXTERNAL_IP:9090`
- **Grafana**: `http://localhost:3001` (admin/admin123)
- **Backend Metrics**: `http://BACKEND_IP:8080/actuator/prometheus`

## Horizontal Pod Autoscaler

The HPA is configured with:
- **Target CPU**: 15%
- **Min Replicas**: 2
- **Max Replicas**: 10
- **Scale Up**: 100% increase every 15 seconds
- **Scale Down**: 10% decrease every 60 seconds

## Load Testing

The load test simulates:
- Gradual ramp-up to 20 concurrent users
- Tests health, catalogs, and products endpoints
- Monitors response times and error rates
- Triggers HPA scaling when CPU > 15%

## Troubleshooting

```bash
# Check pod status
kubectl get pods -n cvetochey

# Check logs
kubectl logs -f deployment/backend -n cvetochey
kubectl logs -f deployment/frontend -n cvetochey

# Check HPA status
kubectl describe hpa backend-hpa -n cvetochey

# Check metrics
kubectl top pods -n cvetochey
```
