#!/bin/bash

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${GREEN}🚀 Starting CvetOchey Monitoring Stack...${NC}"

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    echo -e "${RED}❌ kubectl is not installed or not in PATH${NC}"
    exit 1
fi

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}❌ docker-compose is not installed or not in PATH${NC}"
    exit 1
fi

# Check if Prometheus is running in Kubernetes
echo -e "${YELLOW}📊 Checking Prometheus status...${NC}"
if ! kubectl get pods -n monitoring | grep prometheus | grep Running > /dev/null; then
    echo -e "${RED}❌ Prometheus is not running in Kubernetes${NC}"
    echo -e "${YELLOW}💡 Please deploy Prometheus first: kubectl apply -f ../prometheus.yaml${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Prometheus is running in Kubernetes${NC}"

# Start port-forward for Prometheus (in background)
echo -e "${YELLOW}🔗 Starting Prometheus port-forward...${NC}"
kubectl port-forward service/prometheus-service 9090:9090 -n monitoring > /dev/null 2>&1 &
PROMETHEUS_PID=$!

# Wait for port-forward to establish
sleep 5

# Test Prometheus connection
if curl -s http://localhost:9090/api/v1/status/config > /dev/null; then
    echo -e "${GREEN}✅ Prometheus accessible at http://localhost:9090${NC}"
else
    echo -e "${RED}❌ Failed to connect to Prometheus${NC}"
    kill $PROMETHEUS_PID 2>/dev/null || true
    exit 1
fi

# Start Grafana
echo -e "${YELLOW}📈 Starting Grafana...${NC}"
docker-compose up -d

# Wait for Grafana to start
echo -e "${YELLOW}⏳ Waiting for Grafana to start...${NC}"
sleep 10

# Test Grafana connection
for i in {1..30}; do
    if curl -s http://localhost:3001/api/health > /dev/null; then
        echo -e "${GREEN}✅ Grafana is ready!${NC}"
        break
    fi
    echo -e "${YELLOW}⏳ Waiting for Grafana... ($i/30)${NC}"
    sleep 2
done

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}🎉 Monitoring Stack Started Successfully!${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo -e "${GREEN}📊 Access Points:${NC}"
echo -e "  🌐 Grafana:    ${BLUE}http://localhost:3001${NC} (admin/admin123)"
echo -e "  📈 Prometheus: ${BLUE}http://localhost:9090${NC}"
echo -e "  🔧 Backend:    ${BLUE}http://localhost:8080${NC} (metrics: /actuator/prometheus)"
echo ""
echo -e "${GREEN}🎯 Public Application URLs:${NC}"
echo -e "  🌐 Frontend:   ${BLUE}http://89.169.138.79${NC}"
echo -e "  🔧 Backend:    ${BLUE}http://51.250.66.103:8080${NC}"
echo ""
echo -e "${YELLOW}📝 Next Steps:${NC}"
echo -e "  1. Open Grafana: ${BLUE}http://localhost:3001${NC}"
echo -e "  2. Login with: admin/admin123"
echo -e "  3. Go to Dashboards → CvetOchey folder"
echo -e "  4. Open 'CvetOchey Application Monitoring'"
echo -e "  5. Start load testing to see HPA scaling"
echo ""
echo -e "${GREEN}🔄 To stop monitoring:${NC}"
echo -e "  docker-compose down"
echo -e "  kill $PROMETHEUS_PID"
echo ""

# Save PID for cleanup
echo $PROMETHEUS_PID > .prometheus-port-forward.pid
echo -e "${YELLOW}💾 Prometheus port-forward PID saved: $PROMETHEUS_PID${NC}"
