# 🚀 CvetOchey Quick Start Guide

## Prerequisites Checklist
- [ ] Yandex Cloud CLI installed
- [ ] Docker with buildx support
- [ ] kubectl installed
- [ ] Yandex Cloud account configured

## 1. 🏗️ Setup Infrastructure (5 minutes)

```bash
# 1. Install Yandex Cloud CLI
curl -sSL https://storage.yandexcloud.net/yandexcloud-yc/install.sh | bash
source ~/.zshrc

# 2. Configure (interactive)
yc init

# 3. Create container registry
yc container registry create --name cvetochey-registry
yc container registry configure-docker
```

## 2. 🐳 Build & Push Images (10 minutes)

```bash
# Build and push both images
./build-and-push.sh

# OR manually:
# Backend
cd backend && docker build -t cr.yandex/crpqt390b8gk59ipqid8/cvetochey-backend:latest . && docker push cr.yandex/crpqt390b8gk59ipqid8/cvetochey-backend:latest

# Frontend  
cd frontend && docker build -t cr.yandex/crpqt390b8gk59ipqid8/cvetochey-frontend:latest . && docker push cr.yandex/crpqt390b8gk59ipqid8/cvetochey-frontend:latest
```

## 3. ☸️ Deploy Kubernetes (15 minutes)

```bash
# Create cluster
./k8s/cluster-setup.sh

# Deploy applications
cd k8s && ./deploy.sh
```

## 4. 📊 Setup Monitoring (5 minutes)

```bash
# Setup monitoring
./k8s/setup-monitoring.sh

# Start Grafana locally
docker-compose -f k8s/grafana-local.yaml up -d

# Access Grafana: http://localhost:3001 (admin/admin123)
```

## 5. 🧪 Test Everything (2 minutes)

```bash
# Check deployment
kubectl get all -n cvetochey

# Test load balancing
kubectl apply -f k8s/load-test.yaml

# Watch HPA scaling
kubectl get hpa -n cvetochey -w
```

## 🎯 Expected Results

- **Backend**: 2-10 pods (auto-scaling at 15% CPU)
- **Frontend**: 2 pods
- **Database**: 1 PostgreSQL pod
- **Monitoring**: Prometheus + Grafana with 8 dashboards
- **Load Testing**: K6 stress testing with HPA scaling

## 📊 Access Points

- **Grafana**: http://localhost:3001
- **Prometheus**: `kubectl get svc prometheus-service -n monitoring`
- **Application**: `kubectl get svc -n cvetochey`

## 🆘 Quick Troubleshooting

```bash
# Check pod status
kubectl get pods -n cvetochey

# Check logs
kubectl logs -f deployment/backend -n cvetochey

# Check HPA
kubectl describe hpa backend-hpa -n cvetochey
```

## 📝 Registry Info
- **Registry ID**: `crpqt390b8gk59ipqid8`
- **Cloud ID**: `b1g1hh4a73qpbdr1kbbf`  
- **Folder ID**: `b1gs0cg1voiht42pp513`

Total setup time: ~30 minutes
