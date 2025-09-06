# 📊 Monitoring & Observability Setup

## Overview

Complete monitoring stack with:
- **Prometheus** (in Kubernetes)
- **Grafana** (local Docker)
- **Enhanced HTTP metrics** for endpoint tracking
- **Custom dashboards** with 8 monitoring panels
- **Alerting rules** for proactive monitoring

## 🚀 Quick Setup

```bash
# 1. Deploy Prometheus to Kubernetes
kubectl apply -f k8s/prometheus.yaml

# 2. Setup Grafana locally
./k8s/setup-monitoring.sh

# 3. Start Grafana
docker-compose -f k8s/grafana-local.yaml up -d
```

**Access Grafana**: http://localhost:3001 (admin/admin123)

## 📈 Prometheus Configuration

### Kubernetes Deployment
```yaml
# Scrape configuration for Spring Boot apps
- job_name: 'kubernetes-pods'
  kubernetes_sd_configs:
    - role: pod
  relabel_configs:
    - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
      action: keep
      regex: true
    - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_path]
      action: replace
      target_label: __metrics_path__
      regex: (.+)
```

### Backend Metrics Configuration
Enhanced `application.yaml`:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus,metrics
  metrics:
    tags:
      application: backend
      service: cvetochey-backend
    distribution:
      percentiles-histogram:
        http.server.requests: true
      percentiles:
        http.server.requests: 0.5, 0.95, 0.99
    web:
      server:
        request:
          autotime:
            enabled: true
            percentiles: 0.5, 0.95, 0.99
```

## 📊 Grafana Dashboard

### 8 Monitoring Panels

1. **HTTP Requests Rate**
   ```promql
   rate(http_server_requests_seconds_count{job="kubernetes-pods", kubernetes_namespace="cvetochey"}[5m])
   ```

2. **HTTP Response Time (Percentiles)**
   ```promql
   histogram_quantile(0.95, rate(http_server_requests_seconds_bucket{job="kubernetes-pods", kubernetes_namespace="cvetochey"}[5m]))
   histogram_quantile(0.50, rate(http_server_requests_seconds_bucket{job="kubernetes-pods", kubernetes_namespace="cvetochey"}[5m]))
   histogram_quantile(0.99, rate(http_server_requests_seconds_bucket{job="kubernetes-pods", kubernetes_namespace="cvetochey"}[5m]))
   ```

3. **CPU Usage**
   ```promql
   rate(container_cpu_usage_seconds_total{namespace="cvetochey", container!="POD"}[5m]) * 100
   ```

4. **Memory Usage**
   ```promql
   container_memory_usage_bytes{namespace="cvetochey", container!="POD"} / 1024 / 1024
   ```

5. **Pod Status**
   ```promql
   kube_pod_status_phase{namespace="cvetochey"}
   ```

6. **Endpoint Hit Count (Hourly)**
   ```promql
   increase(http_server_requests_seconds_count{job="kubernetes-pods", kubernetes_namespace="cvetochey"}[1h])
   ```

7. **Top Endpoints by Request Count (Table)**
   ```promql
   topk(10, sum by (method, uri, status) (increase(http_server_requests_seconds_count{job="kubernetes-pods", kubernetes_namespace="cvetochey"}[1h])))
   ```

8. **Error Rate by Endpoint**
   ```promql
   rate(http_server_requests_seconds_count{job="kubernetes-pods", kubernetes_namespace="cvetochey", status=~"4..|5.."}[5m])
   ```

## 🚨 Alerting Rules

### Prometheus Alerts
```yaml
groups:
  - name: cvetochey.rules
    rules:
      - alert: HighCPUUsage
        expr: rate(container_cpu_usage_seconds_total[5m]) * 100 > 80
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "High CPU usage detected"
          description: "CPU usage is above 80% for more than 2 minutes"
      
      - alert: HighMemoryUsage
        expr: (container_memory_usage_bytes / container_spec_memory_limit_bytes) * 100 > 80
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "High memory usage detected"
          description: "Memory usage is above 80% for more than 2 minutes"
      
      - alert: PodCrashLooping
        expr: rate(kube_pod_container_status_restarts_total[15m]) > 0
        for: 0m
        labels:
          severity: critical
        annotations:
          summary: "Pod is crash looping"
          description: "Pod {{ $labels.pod }} is crash looping"
```

## 🔧 Manual Setup Commands

### 1. Deploy Prometheus
```bash
# Create monitoring namespace and deploy Prometheus
kubectl apply -f k8s/prometheus.yaml

# Wait for Prometheus to be ready
kubectl wait --for=condition=ready pod -l app=prometheus -n monitoring --timeout=300s

# Get external IP
kubectl get service prometheus-service -n monitoring
```

### 2. Configure Grafana Datasource
```bash
# Get Prometheus external IP
PROMETHEUS_IP=$(kubectl get service prometheus-service -n monitoring -o jsonpath='{.status.loadBalancer.ingress[0].ip}')

# Update datasource configuration
sed -i "s/YOUR_PROMETHEUS_EXTERNAL_IP/$PROMETHEUS_IP/g" k8s/grafana/provisioning/datasources/prometheus.yml
```

### 3. Start Grafana
```bash
# Start Grafana with docker-compose
docker-compose -f k8s/grafana-local.yaml up -d

# Verify Grafana is running
docker ps | grep grafana

# Access Grafana
open http://localhost:3001
```

## 📊 Metrics Available

### Spring Boot Actuator Metrics
- `http_server_requests_seconds_count` - Request count by endpoint
- `http_server_requests_seconds_sum` - Total request duration
- `http_server_requests_seconds_bucket` - Request duration histogram
- `jvm_memory_used_bytes` - JVM memory usage
- `jvm_gc_pause_seconds` - Garbage collection metrics
- `system_cpu_usage` - System CPU usage

### Kubernetes Metrics
- `container_cpu_usage_seconds_total` - Container CPU usage
- `container_memory_usage_bytes` - Container memory usage
- `kube_pod_status_phase` - Pod status
- `kube_deployment_status_replicas` - Deployment replica count

### Custom Application Tags
```yaml
application: backend
service: cvetochey-backend
```

## 🔍 Troubleshooting

### Prometheus Issues
```bash
# Check Prometheus pod logs
kubectl logs -f deployment/prometheus -n monitoring

# Port forward to access Prometheus UI
kubectl port-forward service/prometheus-service 9090:9090 -n monitoring

# Check Prometheus targets
curl http://localhost:9090/api/v1/targets
```

### Grafana Issues
```bash
# Check Grafana logs
docker logs grafana

# Restart Grafana
docker-compose -f k8s/grafana-local.yaml restart

# Access Grafana shell
docker exec -it grafana /bin/bash
```

### Metrics Not Appearing
```bash
# Check backend metrics endpoint
kubectl exec -it deployment/backend -n cvetochey -- curl localhost:8080/actuator/prometheus

# Check if pod has correct annotations
kubectl get pods -n cvetochey -o yaml | grep -A5 -B5 prometheus

# Verify service discovery
kubectl port-forward service/prometheus-service 9090:9090 -n monitoring
# Then check http://localhost:9090/targets
```

## 📈 Advanced Queries

### Business Metrics
```promql
# Most popular endpoints
topk(5, sum by (uri) (increase(http_server_requests_seconds_count{status="200"}[24h])))

# Error rate by endpoint
sum by (uri) (rate(http_server_requests_seconds_count{status=~"4..|5.."}[5m])) / 
sum by (uri) (rate(http_server_requests_seconds_count[5m]))

# 99th percentile response time trend
histogram_quantile(0.99, sum by (le) (rate(http_server_requests_seconds_bucket[5m])))
```

### Infrastructure Metrics
```promql
# Pod restarts in last hour
increase(kube_pod_container_status_restarts_total[1h])

# Memory usage percentage
100 * (container_memory_usage_bytes / container_spec_memory_limit_bytes)

# CPU throttling
rate(container_cpu_cfs_throttled_seconds_total[5m])
```

## 🎯 Dashboard Import

### Manual Dashboard Import
1. Access Grafana: http://localhost:3001
2. Login: admin/admin123
3. Go to Dashboards → Import
4. Upload: `k8s/grafana/dashboards/cvetochey-dashboard.json`

### Automated Dashboard Provisioning
Dashboards are automatically loaded from:
```
k8s/grafana/dashboards/cvetochey-dashboard.json
```

## 📝 Monitoring Best Practices

1. **Use Labels Consistently**: All metrics tagged with `application` and `service`
2. **Monitor Business Metrics**: Track endpoint usage, not just infrastructure
3. **Set Meaningful Alerts**: Focus on user-impacting issues
4. **Use Percentiles**: 95th/99th percentile more meaningful than averages
5. **Dashboard Organization**: Group related metrics together
6. **Regular Review**: Update dashboards based on operational needs

## 🔗 Useful Links

- **Prometheus UI**: `http://<prometheus-external-ip>:9090`
- **Grafana**: `http://localhost:3001`
- **Backend Metrics**: `http://<backend-service>:8080/actuator/prometheus`
- **Backend Health**: `http://<backend-service>:8080/actuator/health`
