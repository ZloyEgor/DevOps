# 🚀 Production Deployment Commands - Complete Reference

## 📋 Overview
This document contains ALL essential commands used for successful CvetOchey deployment to Yandex Cloud Kubernetes with permanent public access.

## 🏗️ Infrastructure Setup (CRITICAL COMMANDS)

### 1. Yandex Cloud CLI Setup
```bash
# Install CLI
curl -sSL https://storage.yandexcloud.net/yandexcloud-yc/install.sh | bash
source ~/.zshrc

# Configure (interactive)
yc init

# Verify configuration
yc config list
```

### 2. Container Registry
```bash
# Create registry
yc container registry create --name cvetochey-registry
# Registry ID: crpqt390b8gk59ipqid8

# Configure Docker authentication
yc container registry configure-docker

# List images
yc container image list --folder-id b1gs0cg1voiht42pp513
```

### 3. Service Accounts (REQUIRED)
```bash
# Create cluster service account
yc iam service-account create --name k8s-cluster-sa --description "Service account for Kubernetes cluster"
# ID: ajecso143s72sdd36vnd

# Create node service account  
yc iam service-account create --name k8s-node-sa --description "Service account for Kubernetes nodes"
# ID: aje5ucsjf8d3s74tejva

# Assign roles
yc resource-manager folder add-access-binding b1gs0cg1voiht42pp513 --role k8s.clusters.agent --subject serviceAccount:ajecso143s72sdd36vnd
yc resource-manager folder add-access-binding b1gs0cg1voiht42pp513 --role vpc.publicAdmin --subject serviceAccount:ajecso143s72sdd36vnd
yc resource-manager folder add-access-binding b1gs0cg1voiht42pp513 --role container-registry.images.puller --subject serviceAccount:aje5ucsjf8d3s74tejva
```

### 4. Network Infrastructure (CRITICAL)
```bash
# Check existing network
yc vpc network list
# Network: cvetochey-net (ID: enp2depn82rq44b3jn77)

# Check subnet
yc vpc subnet list  
# Subnet: cvetochey-subnet (ID: e9bjq4eom3qadbhb9i87)

# Create NAT Gateway (ESSENTIAL for external registry access)
yc vpc gateway create --name nat-gateway
# Gateway ID: enpkq1on02qutv34epc8

# Create route table
yc vpc route-table create \
  --name nat-route-table \
  --network-name cvetochey-net \
  --route destination=0.0.0.0/0,gateway-id=enpkq1on02qutv34epc8

# Attach route table to subnet (CRITICAL)
yc vpc subnet update cvetochey-subnet --route-table-name nat-route-table
```

## ☸️ Kubernetes Cluster Creation

### Install kubectl
```bash
# Install via Homebrew (macOS)
brew install kubectl

# Verify installation
kubectl version --client
```

### Create Cluster
```bash
# Create Kubernetes cluster
yc managed-kubernetes cluster create \
  --name cvetochey-cluster \
  --network-name cvetochey-net \
  --zone ru-central1-a \
  --subnet-name cvetochey-subnet \
  --public-ip \
  --release-channel regular \
  --version 1.30 \
  --cluster-ipv4-range 10.96.0.0/16 \
  --service-ipv4-range 10.112.0.0/16 \
  --service-account-id ajecso143s72sdd36vnd \
  --node-service-account-id aje5ucsjf8d3s74tejva

# Cluster ID: catmmmt9splioud864ff

# Get cluster credentials
yc managed-kubernetes cluster get-credentials cvetochey-cluster --external

# Verify cluster
kubectl cluster-info
```

### Create Worker Nodes
```bash
# Create node group with adequate resources
yc managed-kubernetes node-group create worker-nodes \
  --cluster-name cvetochey-cluster \
  --platform standard-v3 \
  --cores 4 \
  --memory 8GB \
  --disk-type network-hdd \
  --disk-size 64GB \
  --fixed-size 3 \
  --network-interface subnets=e9bjq4eom3qadbhb9i87,ipv4-address=auto

# Verify nodes
kubectl get nodes
```

## 🐳 Docker Image Management

### Multi-Architecture Backend
```bash
# Build and push backend (multi-arch)
cd backend
docker buildx build \
    --platform linux/amd64,linux/arm64 \
    --tag cr.yandex/crpqt390b8gk59ipqid8/cvetochey-backend:latest \
    --push \
    .
```

### Frontend with Memory Optimization
```bash
# Increase Docker memory to 8GB+ in Docker Desktop first!
docker system info | grep "Total Memory"

# Build frontend (requires 8GB+ Docker memory)
cd frontend
./build-frontend-multiarch.sh
```

## 🌐 Public Access Setup (CRITICAL SOLUTION)

### Create Static Public IPs
```bash
# Frontend IP
yc vpc address create --name frontend-public-ip --external-ipv4 zone=ru-central1-a
# IP: 89.169.138.79

# Backend IP  
yc vpc address create --name backend-public-ip --external-ipv4 zone=ru-central1-a
# IP: 51.250.66.103
```

### Network Load Balancers (WORKING SOLUTION)
```bash
# Frontend Load Balancer
yc load-balancer network-load-balancer create \
  --name cvetochey-nlb \
  --region-id ru-central1 \
  --type external \
  --listener name=frontend,port=80,target-port=32743,protocol=tcp,external-ip-version=ipv4,external-address=89.169.138.79

# Backend Load Balancer
yc load-balancer network-load-balancer create \
  --name cvetochey-backend-nlb \
  --region-id ru-central1 \
  --type external \
  --listener name=backend,port=8080,target-port=32466,protocol=tcp,external-ip-version=ipv4,external-address=51.250.66.103

# Attach target group (automatic Kubernetes target group)
yc load-balancer target-group list
# Target Group ID: enpu7jlqke6fivucj2mg

# Attach to frontend LB
yc load-balancer network-load-balancer attach-target-group \
  --name cvetochey-nlb \
  --target-group target-group-id=enpu7jlqke6fivucj2mg,healthcheck-name=frontend-health,healthcheck-http-port=32743

# Attach to backend LB  
yc load-balancer network-load-balancer attach-target-group \
  --name cvetochey-backend-nlb \
  --target-group target-group-id=enpu7jlqke6fivucj2mg,healthcheck-name=backend-health,healthcheck-http-port=32466,healthcheck-http-path=/actuator/health
```

## 📦 Application Deployment

### Deploy Applications
```bash
cd k8s

# Create namespace
kubectl apply -f namespace.yaml

# Deploy PostgreSQL (with subPath fix)
kubectl apply -f postgresql.yaml
kubectl wait --for=condition=ready pod -l app=postgres -n cvetochey --timeout=300s

# Deploy backend
kubectl apply -f backend.yaml
kubectl wait --for=condition=ready pod -l app=backend -n cvetochey --timeout=300s

# Deploy frontend
kubectl apply -f frontend.yaml
kubectl wait --for=condition=ready pod -l app=frontend -n cvetochey --timeout=300s
```

### Fix PostgreSQL Volume Issue
```yaml
# In postgresql.yaml - CRITICAL FIX:
volumeMounts:
- name: postgres-storage
  mountPath: /var/lib/postgresql/data
  subPath: postgres  # <- This line prevents "directory not empty" error
```

## 🔧 Troubleshooting Commands

### Network Connectivity Issues
```bash
# Check if nodes can reach external registries
kubectl describe pod <pod-name> -n cvetochey

# Verify NAT gateway is working
yc vpc route-table list
yc vpc subnet get cvetochey-subnet

# Restart pods after network fixes
kubectl delete pods --all -n cvetochey
```

### Load Balancer Issues
```bash
# Check LoadBalancer services
kubectl get services -n cvetochey

# If external IPs are pending, use Network Load Balancers instead
yc load-balancer network-load-balancer list

# Check target groups
yc load-balancer target-group list
```

### Pod Issues
```bash
# Check pod status
kubectl get pods -n cvetochey

# Check pod logs
kubectl logs -f deployment/backend -n cvetochey
kubectl logs -f deployment/postgres -n cvetochey

# Describe problematic pods
kubectl describe pod <pod-name> -n cvetochey
```

## 🎯 Current Working Configuration

### **Cluster Details:**
- **Cluster ID**: catmmmt9splioud864ff
- **Cluster Name**: cvetochey-cluster
- **Kubernetes Version**: 1.30
- **Worker Nodes**: 3 nodes (4 cores, 8GB each)
- **Total Resources**: 12 cores, 24GB RAM

### **Network Configuration:**
- **Network**: cvetochey-net (enp2depn82rq44b3jn77)
- **Subnet**: cvetochey-subnet (e9bjq4eom3qadbhb9i87)
- **NAT Gateway**: enpkq1on02qutv34epc8
- **Route Table**: enpmvrar6435fvajv0mi

### **Service Accounts:**
- **Cluster SA**: ajecso143s72sdd36vnd
- **Node SA**: aje5ucsjf8d3s74tejva

### **Container Registry:**
- **Registry ID**: crpqt390b8gk59ipqid8
- **Backend Images**: 5 (multi-arch + attestations)
- **Frontend Images**: 8 (multi-arch + attestations)

### **Public Access (LIVE):**
- **Frontend**: http://89.169.138.79 ✅
- **Backend API**: http://51.250.66.103:8080 ✅
- **Backend Health**: http://51.250.66.103:8080/actuator/health ✅

### **Load Balancers:**
- **Frontend NLB**: cvetochey-nlb (enpdssto66o17dmv1p4o)
- **Backend NLB**: cvetochey-backend-nlb (enp7avnkd640iqfa0qdo)
- **Target Group**: k8s-catmmmt9splioud864ff (enpu7jlqke6fivucj2mg)

## 📊 Application Status
```bash
# Check everything is running
kubectl get all -n cvetochey

# Expected output:
# - postgres: 1/1 Running
# - backend: 2/2 Running  
# - frontend: 2/2 Running
# - HPA: cpu: 2%/15% (ready for scaling)
```

## 🔄 Maintenance Commands

### Update Images
```bash
# Update backend
kubectl set image deployment/backend backend=cr.yandex/crpqt390b8gk59ipqid8/cvetochey-backend:latest -n cvetochey

# Update frontend
kubectl set image deployment/frontend frontend=cr.yandex/crpqt390b8gk59ipqid8/cvetochey-frontend:latest -n cvetochey
```

### Scale Applications
```bash
# Manual scaling
kubectl scale deployment backend --replicas=5 -n cvetochey

# Check HPA
kubectl get hpa -n cvetochey
kubectl describe hpa backend-hpa -n cvetochey
```

### Clean Up (DANGER!)
```bash
# Delete load balancers
yc load-balancer network-load-balancer delete cvetochey-nlb
yc load-balancer network-load-balancer delete cvetochey-backend-nlb

# Delete static IPs
yc vpc address delete frontend-public-ip
yc vpc address delete backend-public-ip

# Delete cluster
yc managed-kubernetes cluster delete cvetochey-cluster
```

## 💡 Key Lessons Learned

1. **Service Accounts are REQUIRED** for cluster creation
2. **NAT Gateway is ESSENTIAL** for external registry access
3. **PostgreSQL needs subPath** to avoid volume mount issues
4. **Network Load Balancers work better** than Kubernetes LoadBalancer in Yandex Cloud
5. **Multi-arch builds need 8GB+ Docker memory**
6. **Static IPs must be created separately** and assigned to load balancers

## 🎯 Next Steps Ready
- ✅ **Prometheus deployment**
- ✅ **Grafana setup** 
- ✅ **HPA load testing**
- ✅ **Endpoint monitoring verification**
