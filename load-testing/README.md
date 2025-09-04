# 🚀 Load Testing for HPA Scaling

## 🎯 Overview

This directory contains K6 load testing scripts designed to trigger Horizontal Pod Autoscaler (HPA) scaling for the CvetOchey backend deployment.

## 📋 Prerequisites

### Required Tools
```bash
# Install K6
brew install k6

# Verify installation
k6 version
```

### Required Services
- ✅ Backend deployed and accessible at `http://51.250.66.103:8080`
- ✅ HPA configured with 15% CPU threshold
- ✅ Grafana dashboard running at `http://localhost:3001`
- ✅ Prometheus port-forward active (`kubectl port-forward service/prometheus-service 9090:9090 -n monitoring`)

## 🧪 Load Test Scripts

### 1. Standard HPA Test (`hpa-load-test.js`)
**Purpose**: Gradual load increase to trigger HPA scaling naturally

**Load Pattern**:
- **Warm up**: 5 users (30s)
- **Ramp up**: 5→20→50→100 users (3 minutes)
- **High load**: 100 users (3 minutes)
- **Scale down**: 100→20→0 users (1.5 minutes)

**Total Duration**: ~7 minutes

### 2. CPU Intensive Test (`cpu-intensive-test.js`)
**Purpose**: Aggressive load to trigger HPA scaling quickly

**Load Pattern**:
- **Quick ramp**: 10→50→150→200 users (1 minute)
- **Extreme load**: 200 users (2 minutes)
- **Scale down**: 200→50→0 users (1 minute)

**Total Duration**: ~4 minutes

## 🚀 Running Load Tests

### Quick Start
```bash
./run-load-test.sh
```

### Manual Execution
```bash
# Standard test
k6 run hpa-load-test.js

# CPU intensive test  
k6 run cpu-intensive-test.js

# Custom test
k6 run --vus 50 --duration 2m hpa-load-test.js
```

## 📊 Monitoring During Tests

### 1. Grafana Dashboard
**URL**: http://localhost:3001/d/cvetochey-monitoring/cvetochey-application-monitoring

**Key Panels to Watch**:
- **HPA Status (Backend)**: Shows replica scaling (2→3→4→...→10)
- **System CPU Usage**: Should increase above 15% to trigger HPA
- **HTTP Requests Rate**: Shows load test traffic
- **Error Rate vs Success Rate**: Monitor for failures under load

### 2. Kubernetes Commands
```bash
# Watch HPA scaling in real-time
watch kubectl get hpa -n cvetochey

# Watch pod scaling
watch kubectl get pods -n cvetochey

# Check HPA events
kubectl describe hpa backend-hpa -n cvetochey

# Monitor resource usage
kubectl top pods -n cvetochey
```

### 3. Prometheus Queries
```promql
# CPU usage triggering HPA
system_cpu_usage{kubernetes_namespace="cvetochey"} * 100

# Current replica count
kube_horizontalpodautoscaler_status_current_replicas{namespace="cvetochey"}

# Request rate during test
sum(rate(http_server_requests_seconds_count{kubernetes_namespace="cvetochey"}[1m]))
```

## 🎯 Expected Results

### HPA Scaling Behavior
1. **Baseline**: 2 replicas (min replicas)
2. **Load increase**: CPU usage rises above 15%
3. **Scaling up**: HPA creates new pods (2→3→4→...up to 10)
4. **Load decrease**: CPU usage drops below 15%
5. **Scaling down**: HPA removes pods after cooldown period (5-10 minutes)

### Performance Metrics
- **Response Time**: Should remain under 2s for 95% of requests
- **Error Rate**: Should stay below 10%
- **Throughput**: Will increase with more replicas

## 🔧 Troubleshooting

### Load Test Issues

#### Backend Not Accessible
```bash
# Check backend pods
kubectl get pods -n cvetochey

# Check service
kubectl get svc -n cvetochey backend-service

# Test connectivity
curl http://51.250.66.103:8080/actuator/health
```

#### HPA Not Scaling
```bash
# Check HPA configuration
kubectl get hpa -n cvetochey backend-hpa -o yaml

# Check metrics server
kubectl get apiservice v1beta1.metrics.k8s.io

# Check pod resource requests (required for HPA)
kubectl describe deployment backend -n cvetochey
```

### Monitoring Issues

#### Grafana Dashboard Empty
```bash
# Check Prometheus port-forward
ps aux | grep "kubectl port-forward.*9090"

# Restart port-forward if needed
kubectl port-forward service/prometheus-service 9090:9090 -n monitoring &

# Test Prometheus
curl http://localhost:9090/api/v1/query?query=up
```

## 📈 Load Test Results Analysis

### Successful HPA Scaling Indicators
- ✅ **CPU usage increases** above 15% during load
- ✅ **Replica count increases** from 2 to higher numbers
- ✅ **Response times remain stable** despite increased load
- ✅ **Error rate stays low** (< 10%)
- ✅ **Pods scale back down** after load decreases

### Performance Benchmarks
- **Baseline (2 replicas)**: ~50-100 requests/second
- **Scaled (4+ replicas)**: ~200+ requests/second
- **Response time**: < 2s for 95% of requests
- **Error rate**: < 10% even under high load

## 🎛️ Advanced Testing

### Custom Load Patterns
```javascript
// Spike test
export const options = {
  stages: [
    { duration: '10s', target: 0 },
    { duration: '1s', target: 100 },   // Sudden spike
    { duration: '60s', target: 100 },  // Sustain
    { duration: '10s', target: 0 },
  ],
};

// Stress test
export const options = {
  stages: [
    { duration: '2m', target: 200 },   // Beyond normal capacity
    { duration: '5m', target: 200 },   // Sustain stress
    { duration: '2m', target: 0 },
  ],
};
```

### Multiple Endpoint Testing
```javascript
const endpoints = [
  { url: '/api/v1/catalog', weight: 40 },     // 40% of traffic
  { url: '/api/v1/products', weight: 30 },    // 30% of traffic  
  { url: '/actuator/health', weight: 20 },    // 20% of traffic
  { url: '/actuator/prometheus', weight: 10 }, // 10% of traffic
];
```

## 🚨 Load Testing Best Practices

### Before Testing
- ✅ Verify all monitoring is working
- ✅ Check baseline resource usage
- ✅ Ensure HPA is configured correctly
- ✅ Have Grafana dashboard open

### During Testing
- 📊 Monitor Grafana dashboard continuously
- 🔍 Watch for error rate increases
- ⚡ Observe HPA scaling events
- 📈 Track response time degradation

### After Testing
- ⏳ Wait for scale-down (5-10 minutes)
- 📋 Document scaling behavior
- 🔍 Analyze performance metrics
- 🛠️ Adjust HPA settings if needed
