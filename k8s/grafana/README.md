# 📊 Grafana Setup for CvetOchey Monitoring

## 🎯 Overview

This folder contains all Grafana configuration files for monitoring your CvetOchey application deployed in Yandex Cloud Kubernetes.

## 📁 Folder Structure

```
grafana/
├── README.md                           # This file
├── docker-compose.yml                  # Grafana Docker Compose
├── config/                             # Grafana configuration
├── dashboards/                         # Dashboard JSON files
│   └── cvetochey-dashboard.json        # Main application dashboard
└── provisioning/                       # Auto-provisioning configs
    ├── datasources/
    │   └── prometheus.yml              # Prometheus datasource
    └── dashboards/
        └── dashboard.yml               # Dashboard provisioning
```

## 🚀 Quick Start

### 1. Start Required Services

**Start Prometheus port-forward** (in separate terminal):
```bash
cd /Users/egorstukov/Developer/DevOps/k8s
kubectl port-forward service/prometheus-service 9090:9090 -n monitoring
```

**Start Backend port-forward** (in separate terminal):
```bash
cd /Users/egorstukov/Developer/DevOps/k8s  
kubectl port-forward service/backend-service 8080:8080 -n cvetochey
```

### 2. Start Grafana

```bash
cd /Users/egorstukov/Developer/DevOps/k8s/grafana
docker-compose up -d
```

### 3. Access Grafana

- **URL**: http://localhost:3001
- **Login**: admin
- **Password**: admin123
- **Direct Dashboard**: http://localhost:3001/d/cvetochey-monitoring/cvetochey-application-monitoring

## 🔧 Port Forwarding Commands

### Essential Port Forwards (Run in separate terminals)

#### Prometheus (Required for Grafana)
```bash
# Terminal 1: Prometheus
kubectl port-forward service/prometheus-service 9090:9090 -n monitoring

# Verify: http://localhost:9090
curl http://localhost:9090/api/v1/status/config
```

#### Backend Metrics (For testing)
```bash  
# Terminal 2: Backend API
kubectl port-forward service/backend-service 8080:8080 -n cvetochey

# Verify: http://localhost:8080/actuator/prometheus
curl http://localhost:8080/actuator/prometheus | head -10
```

#### Frontend (Optional)
```bash
# Terminal 3: Frontend (optional - already has public IP)
kubectl port-forward service/frontend-service 3000:3000 -n cvetochey
```

## 📊 Dashboard Features

### CvetOchey Application Dashboard

**8 Monitoring Panels:**

1. **HTTP Requests Rate** - Real-time API calls per second
2. **HTTP Response Time** - 50th, 95th, 99th percentiles  
3. **CPU Usage** - Container CPU utilization
4. **Memory Usage** - Container memory consumption
5. **Pod Status** - Kubernetes pod health
6. **Endpoint Hit Count** - Hourly request counts per endpoint
7. **Top Endpoints by Request Count** - Most used APIs (table)
8. **Error Rate by Endpoint** - 4xx/5xx errors per endpoint

### Key Metrics Tracked

- **Endpoint Usage**: Which APIs are called most frequently
- **Response Times**: Performance per endpoint
- **Error Rates**: Failed requests by endpoint
- **Infrastructure**: CPU, memory, pod status
- **Scaling**: HPA metrics and scaling events

## 🔍 Troubleshooting

### Grafana Can't Connect to Prometheus

**Check port-forward is running:**
```bash
# Should show active port forward
ps aux | grep "kubectl port-forward.*9090"

# Test Prometheus directly
curl http://localhost:9090/api/v1/status/config
```

**Restart port-forward:**
```bash
# Kill existing port-forward
pkill -f "kubectl port-forward.*prometheus"

# Restart
kubectl port-forward service/prometheus-service 9090:9090 -n monitoring
```

### Prometheus Not Scraping Backend

**Check backend pods have annotations:**
```bash
kubectl get pods -n cvetochey -o yaml | grep -A5 -B5 prometheus
```

**Should show:**
```yaml
annotations:
  prometheus.io/scrape: "true"
  prometheus.io/port: "8080"  
  prometheus.io/path: "/actuator/prometheus"
```

**Test backend metrics directly:**
```bash
curl http://localhost:8080/actuator/prometheus | grep http_server_requests
```

### Dashboard Not Loading Data

**Check datasource in Grafana:**
1. Go to Configuration → Data Sources
2. Click "Prometheus"
3. Test connection (should be green)
4. URL should be: `http://host.docker.internal:9090`

**Check metrics in Prometheus:**
1. Go to http://localhost:9090
2. Try query: `up{job="kubernetes-pods"}`
3. Should show backend pods

## 🎛️ Advanced Configuration

### Custom Datasource Configuration

Edit `provisioning/datasources/prometheus.yml`:
```yaml
datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://host.docker.internal:9090
    isDefault: true
    editable: true
    basicAuth: false
    jsonData:
      timeInterval: 5s
      queryTimeout: 60s
```

### Dashboard Auto-Import

Dashboards in `dashboards/` folder are automatically imported on Grafana startup.

To add new dashboards:
1. Export dashboard JSON from Grafana UI
2. Save to `dashboards/` folder
3. Restart Grafana: `docker-compose restart`

## 🔗 Useful Links

### Local Access
- **Grafana**: http://localhost:3001 (admin/admin123)
- **Prometheus**: http://localhost:9090 (via port-forward)
- **Backend Metrics**: http://localhost:8080/actuator/prometheus (via port-forward)

### Public Access  
- **Frontend**: http://89.169.138.79
- **Backend API**: http://51.250.66.103:8080
- **Backend Health**: http://51.250.66.103:8080/actuator/health

### Kubernetes Resources
```bash
# Check all monitoring resources
kubectl get all -n monitoring

# Check application resources
kubectl get all -n cvetochey

# Check HPA status
kubectl get hpa -n cvetochey
```

## 🚨 Important Notes

1. **Port-forwards must be running** for Grafana to connect to Prometheus
2. **Backend annotations** are required for Prometheus scraping
3. **Grafana runs on port 3001** to avoid conflict with frontend (port 3000)
4. **Dashboards auto-load** from the dashboards folder
5. **Prometheus data persists** in Kubernetes PVC
