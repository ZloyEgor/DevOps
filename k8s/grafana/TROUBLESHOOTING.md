# 🔧 Grafana Dashboard Troubleshooting Guide

## 📊 Common Dashboard Issues & Solutions

### ❌ Issue: "No data" in Infrastructure Metrics

**Symptoms:**
- CPU Usage, Memory Usage, Pod Status show "No data"
- HPA metrics not visible

**Root Cause:**
Missing `kube-state-metrics` deployment

**Solution:**
```bash
# Deploy kube-state-metrics
kubectl apply -f /Users/egorstukov/Developer/DevOps/k8s/kube-state-metrics.yaml

# Verify deployment
kubectl get pods -n monitoring
kubectl logs -n monitoring deployment/kube-state-metrics
```

**Check metrics availability:**
```bash
curl "http://localhost:9090/api/v1/query?query=kube_deployment_status_replicas"
```

---

### ❌ Issue: Uninformative Legends (Too Many Similar Names)

**Symptoms:**
- HTTP Request Rate shows many "Response Time" entries
- Legends not showing endpoint information properly

**Root Cause:**
- Incorrect `legendFormat` in queries
- Missing `sum() by ()` grouping in PromQL

**Solution:**
```promql
# ❌ Bad query (creates duplicate legends)
rate(http_server_requests_seconds_count[5m])

# ✅ Good query (clean legends)
sum(rate(http_server_requests_seconds_count[5m])) by (uri, method)
```

**Fixed Legend Format:**
```
legendFormat: "{{method}} {{uri}}"
```

---

### ❌ Issue: Backend Endpoints Show as "UNKNOWN"

**Symptoms:**
- All endpoints appear as `uri="UNKNOWN"`
- Cannot see actual API endpoints

**Root Cause:**
Spring Boot metrics not configured for specific endpoints

**Solution:**
Update `backend/src/main/resources/application.yaml`:
```yaml
management:
  metrics:
    web:
      server:
        request:
          autotime:
            enabled: true
            percentiles: 0.5, 0.95, 0.99
```

**Test endpoint visibility:**
```bash
curl http://localhost:8080/actuator/prometheus | grep http_server_requests
```

---

### ❌ Issue: Container Metrics Not Available

**Symptoms:**
- `container_cpu_usage_seconds_total` returns no data
- `container_memory_working_set_bytes` not found

**Root Cause:**
- Missing cAdvisor metrics
- Prometheus not scraping node metrics

**Solution:**
Deploy node-exporter or use available JVM metrics:
```promql
# Use JVM metrics instead of container metrics
jvm_memory_used_bytes{area="heap"} / 1024 / 1024
jvm_threads_live_threads
```

---

### ❌ Issue: Dashboard Shows "Dashboard title cannot be empty"

**Symptoms:**
- Dashboard won't load
- Grafana logs show title error

**Root Cause:**
Dashboard JSON has incorrect structure

**Solution:**
Ensure dashboard JSON has title at root level:
```json
{
  "id": null,
  "title": "Dashboard Name",  // ✅ At root level
  "panels": [...]
}

// ❌ NOT nested inside dashboard object
{
  "dashboard": {
    "title": "Dashboard Name"  // ❌ Wrong location
  }
}
```

---

## 🔍 Diagnostic Commands

### Check Prometheus Targets
```bash
curl http://localhost:9090/api/v1/targets
```

### Test Specific Metrics
```bash
# Check if backend metrics are scraped
curl "http://localhost:9090/api/v1/query?query=up{kubernetes_namespace=\"cvetochey\"}"

# Check kube-state-metrics
curl "http://localhost:9090/api/v1/query?query=kube_deployment_status_replicas"

# Check HTTP metrics
curl "http://localhost:9090/api/v1/query?query=http_server_requests_seconds_count"
```

### Generate Test Traffic
```bash
# Generate some HTTP requests
for i in {1..10}; do
  curl -s http://51.250.66.103:8080/actuator/health > /dev/null
  curl -s http://51.250.66.103:8080/actuator/prometheus > /dev/null
  curl -s http://51.250.66.103:8080/api/v1/catalog > /dev/null
done
```

### Check Grafana Datasource
```bash
# Test Prometheus connection from Grafana
curl -u admin:admin123 http://localhost:3001/api/datasources/proxy/1/api/v1/query?query=up
```

---

## 📈 Optimal Dashboard Queries

### HTTP Metrics
```promql
# Request rate by endpoint
sum(rate(http_server_requests_seconds_count{kubernetes_namespace="cvetochey"}[5m])) by (uri, method)

# Response time percentiles
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket{kubernetes_namespace="cvetochey"}[5m])) by (le))

# Error rate
sum(rate(http_server_requests_seconds_count{kubernetes_namespace="cvetochey", status=~"4..|5.."}[5m]))
```

### Infrastructure Metrics
```promql
# Pod replicas
kube_deployment_status_ready_replicas{namespace="cvetochey"}

# HPA status
kube_horizontalpodautoscaler_status_current_replicas{namespace="cvetochey"}

# JVM memory
jvm_memory_used_bytes{kubernetes_namespace="cvetochey", area="heap"}
```

---

## 🚨 Emergency Recovery

### Restart All Monitoring
```bash
cd /Users/egorstukov/Developer/DevOps/k8s/grafana
./stop-monitoring.sh
./start-monitoring.sh
```

### Reset Dashboard
```bash
# Remove and recreate dashboard
rm dashboards/cvetochey-dashboard.json
# Copy from backup or recreate
docker-compose restart grafana
```

### Check All Services
```bash
# Prometheus
kubectl get pods -n monitoring
kubectl port-forward service/prometheus-service 9090:9090 -n monitoring &

# Backend
kubectl get pods -n cvetochey
kubectl port-forward service/backend-service 8080:8080 -n cvetochey &

# Grafana
docker-compose ps
curl http://localhost:3001/api/health
```
