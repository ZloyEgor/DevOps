# 🌐 Network Setup Guide - Critical Configuration

## 🚨 CRITICAL: Network Connectivity Solution

This guide documents the essential network configuration that enables Kubernetes nodes to access external registries (Docker Hub, Yandex Container Registry).

## ❌ Problem: ImagePullBackOff
```
Failed to pull image: dial tcp registry-1.docker.io:443: i/o timeout
Failed to pull image: dial tcp cr.yandex:443: i/o timeout
```

## ✅ Solution: NAT Gateway + Route Table

### 1. Create NAT Gateway
```bash
yc vpc gateway create --name nat-gateway
# Gateway ID: enpkq1on02qutv34epc8
```

### 2. Create Route Table
```bash
yc vpc route-table create \
  --name nat-route-table \
  --network-name cvetochey-net \
  --route destination=0.0.0.0/0,gateway-id=enpkq1on02qutv34epc8
# Route Table ID: enpmvrar6435fvajv0mi
```

### 3. Attach Route Table to Subnet (CRITICAL)
```bash
yc vpc subnet update cvetochey-subnet --route-table-name nat-route-table
```

### 4. Verify Network Setup
```bash
# Check subnet has route table
yc vpc subnet get cvetochey-subnet

# Should show:
# route_table_id: enpmvrar6435fvajv0mi
```

## 🔄 After Network Fix
```bash
# Restart all pods to trigger new image pulls
kubectl delete pods --all -n cvetochey

# Verify pods can now pull images
kubectl get pods -n cvetochey
# Should show: Running (not ImagePullBackOff)
```

## 🌐 Public Access Configuration

### Why Kubernetes LoadBalancer Fails in Yandex Cloud
- LoadBalancer services stay in `<pending>` state
- Yandex Cloud requires specific annotations
- Network Load Balancers work more reliably

### Working Solution: Network Load Balancers

#### 1. Create Static Public IPs
```bash
# Frontend
yc vpc address create --name frontend-public-ip --external-ipv4 zone=ru-central1-a
# Result: 89.169.138.79

# Backend
yc vpc address create --name backend-public-ip --external-ipv4 zone=ru-central1-a  
# Result: 51.250.66.103
```

#### 2. Create Network Load Balancers
```bash
# Frontend NLB
yc load-balancer network-load-balancer create \
  --name cvetochey-nlb \
  --region-id ru-central1 \
  --type external \
  --listener name=frontend,port=80,target-port=32743,protocol=tcp,external-ip-version=ipv4,external-address=89.169.138.79

# Backend NLB
yc load-balancer network-load-balancer create \
  --name cvetochey-backend-nlb \
  --region-id ru-central1 \
  --type external \
  --listener name=backend,port=8080,target-port=32466,protocol=tcp,external-ip-version=ipv4,external-address=51.250.66.103
```

#### 3. Attach Target Groups
```bash
# Find Kubernetes auto-created target group
yc load-balancer target-group list
# Target Group: k8s-catmmmt9splioud864ff (enpu7jlqke6fivucj2mg)

# Attach to frontend
yc load-balancer network-load-balancer attach-target-group \
  --name cvetochey-nlb \
  --target-group target-group-id=enpu7jlqke6fivucj2mg,healthcheck-name=frontend-health,healthcheck-http-port=32743

# Attach to backend
yc load-balancer network-load-balancer attach-target-group \
  --name cvetochey-backend-nlb \
  --target-group target-group-id=enpu7jlqke6fivucj2mg,healthcheck-name=backend-health,healthcheck-http-port=32466,healthcheck-http-path=/actuator/health
```

## 🎯 Working Configuration

### Network Resources
- **VPC**: cvetochey-net (enp2depn82rq44b3jn77)
- **Subnet**: cvetochey-subnet (e9bjq4eom3qadbhb9i87, 192.168.10.0/24)
- **NAT Gateway**: nat-gateway (enpkq1on02qutv34epc8)
- **Route Table**: nat-route-table (enpmvrar6435fvajv0mi)

### Public IPs
- **Frontend**: 89.169.138.79 (e9bg97babfu52oceaagr)
- **Backend**: 51.250.66.103 (e9b7053grjrolcl0kb0l)

### Load Balancers
- **Frontend NLB**: cvetochey-nlb (enpdssto66o17dmv1p4o)
- **Backend NLB**: cvetochey-backend-nlb (enp7avnkd640iqfa0qdo)

### Target Groups
- **Kubernetes Nodes**: k8s-catmmmt9splioud864ff (enpu7jlqke6fivucj2mg)
  - Node 1: 192.168.10.6
  - Node 2: 192.168.10.12  
  - Node 3: 192.168.10.8

## 🔍 Verification Commands

### Test Public Access
```bash
# Frontend
curl -s http://89.169.138.79 | head -5

# Backend Health
curl -s http://51.250.66.103:8080/actuator/health

# Backend Metrics
curl -s http://51.250.66.103:8080/actuator/prometheus | head -10
```

### Check Load Balancer Status
```bash
# List all load balancers
yc load-balancer network-load-balancer list

# Check specific load balancer
yc load-balancer network-load-balancer get cvetochey-nlb

# Check target group health
yc load-balancer target-group get enpu7jlqke6fivucj2mg
```

### Monitor Network Traffic
```bash
# Check service endpoints
kubectl get endpoints -n cvetochey

# Check NodePort services
kubectl get services -n cvetochey
# Look for NodePort mappings: 32743 (frontend), 32466 (backend)
```

## ⚠️ Common Issues & Solutions

### Issue: LoadBalancer External IP Pending
```bash
# Problem: Kubernetes LoadBalancer services show <pending>
kubectl get services -n cvetochey

# Solution: Use Yandex Cloud Network Load Balancers instead
# Follow the Network Load Balancer setup above
```

### Issue: ImagePullBackOff
```bash
# Problem: Nodes can't reach external registries
kubectl describe pod <pod-name> -n cvetochey

# Solution: Ensure NAT Gateway and route table are configured
yc vpc subnet get cvetochey-subnet
# Must show route_table_id
```

### Issue: PostgreSQL Init Error
```bash
# Problem: "directory exists but is not empty"
kubectl logs deployment/postgres -n cvetochey

# Solution: Use subPath in volumeMount
# mountPath: /var/lib/postgresql/data
# subPath: postgres
```

## 🎯 Production Best Practices

1. **Always use NAT Gateway** for private node internet access
2. **Use Network Load Balancers** instead of Kubernetes LoadBalancer in Yandex Cloud
3. **Create static IPs first** then assign to load balancers
4. **Use subPath for database volumes** to avoid mount conflicts
5. **Monitor target group health** for load balancer functionality
6. **Test both internal and external connectivity** after deployment

This configuration provides:
- ✅ **High Availability**: 3 nodes with load balancing
- ✅ **Internet Access**: NAT Gateway for external registry pulls
- ✅ **Permanent IPs**: Static public IPs for services
- ✅ **Health Monitoring**: Automatic health checks
- ✅ **Production Ready**: Scalable and reliable setup
