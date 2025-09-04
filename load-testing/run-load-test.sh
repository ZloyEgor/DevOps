#!/bin/bash

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${GREEN}🚀 CvetOchey HPA Load Testing${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

# Check prerequisites
echo -e "${YELLOW}📋 Checking prerequisites...${NC}"

# Check if K6 is installed
if ! command -v k6 &> /dev/null; then
    echo -e "${RED}❌ K6 is not installed${NC}"
    echo -e "${YELLOW}💡 Install with: brew install k6${NC}"
    exit 1
fi

# Check if backend is accessible
echo -e "${YELLOW}🔍 Testing backend connectivity...${NC}"
if curl -s --max-time 5 http://51.250.66.103:8080/actuator/health > /dev/null; then
    echo -e "${GREEN}✅ Backend is accessible${NC}"
else
    echo -e "${RED}❌ Backend is not accessible at http://51.250.66.103:8080${NC}"
    echo -e "${YELLOW}💡 Check if backend pods are running: kubectl get pods -n cvetochey${NC}"
    exit 1
fi

# Check HPA status
echo -e "${YELLOW}🔍 Checking HPA status...${NC}"
HPA_STATUS=$(kubectl get hpa -n cvetochey backend-hpa -o jsonpath='{.status.currentReplicas}' 2>/dev/null || echo "unknown")
echo -e "${BLUE}📊 Current replicas: ${HPA_STATUS}${NC}"

# Display monitoring URLs
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}📊 Monitoring URLs (open in browser):${NC}"
echo -e "  🌐 Grafana Dashboard: ${BLUE}http://localhost:3001/d/cvetochey-monitoring/cvetochey-application-monitoring${NC}"
echo -e "  📈 Prometheus: ${BLUE}http://localhost:9090${NC}"
echo -e "  🎯 Backend API: ${BLUE}http://51.250.66.103:8080${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

# Ask user which test to run
echo ""
echo -e "${YELLOW}🎯 Choose load test type:${NC}"
echo -e "  ${GREEN}1${NC}) Standard HPA Test (gradual load increase)"
echo -e "  ${GREEN}2${NC}) CPU Intensive Test (aggressive load)"
echo -e "  ${GREEN}3${NC}) Custom duration test"
echo ""
read -p "Enter choice (1-3): " choice

case $choice in
    1)
        echo -e "${GREEN}🚀 Starting Standard HPA Load Test...${NC}"
        echo -e "${YELLOW}📈 This test will gradually increase load to trigger HPA scaling${NC}"
        echo -e "${YELLOW}⏱️  Duration: ~7 minutes${NC}"
        echo ""
        k6 run hpa-load-test.js
        ;;
    2)
        echo -e "${GREEN}🚀 Starting CPU Intensive Load Test...${NC}"
        echo -e "${YELLOW}🔥 This test uses aggressive load to trigger HPA quickly${NC}"
        echo -e "${YELLOW}⏱️  Duration: ~4 minutes${NC}"
        echo ""
        k6 run cpu-intensive-test.js
        ;;
    3)
        echo -e "${GREEN}🚀 Starting Custom Load Test...${NC}"
        read -p "Enter number of virtual users: " users
        read -p "Enter duration (e.g., 2m, 120s): " duration
        echo -e "${YELLOW}🎯 Running $users users for $duration${NC}"
        echo ""
        k6 run --vus $users --duration $duration hpa-load-test.js
        ;;
    *)
        echo -e "${RED}❌ Invalid choice${NC}"
        exit 1
        ;;
esac

echo ""
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✅ Load test completed!${NC}"

# Show final HPA status
echo -e "${YELLOW}📊 Final HPA status:${NC}"
kubectl get hpa -n cvetochey

echo -e "${YELLOW}📊 Pod status:${NC}"
kubectl get pods -n cvetochey

echo ""
echo -e "${GREEN}🎯 Next steps:${NC}"
echo -e "  1. Check Grafana dashboard for scaling events"
echo -e "  2. Monitor CPU usage and replica count"
echo -e "  3. Wait for HPA to scale down (may take 5-10 minutes)"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
