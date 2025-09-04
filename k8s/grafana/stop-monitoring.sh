#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}🛑 Stopping CvetOchey Monitoring Stack...${NC}"

# Stop Grafana
echo -e "${YELLOW}📈 Stopping Grafana...${NC}"
docker-compose down

# Stop Prometheus port-forward
if [ -f .prometheus-port-forward.pid ]; then
    PID=$(cat .prometheus-port-forward.pid)
    echo -e "${YELLOW}🔗 Stopping Prometheus port-forward (PID: $PID)...${NC}"
    kill $PID 2>/dev/null || true
    rm .prometheus-port-forward.pid
fi

# Kill any remaining port-forwards
echo -e "${YELLOW}🧹 Cleaning up any remaining port-forwards...${NC}"
pkill -f "kubectl port-forward.*prometheus" 2>/dev/null || true
pkill -f "kubectl port-forward.*backend" 2>/dev/null || true
pkill -f "kubectl port-forward.*frontend" 2>/dev/null || true

echo -e "${GREEN}✅ Monitoring stack stopped successfully!${NC}"
echo ""
echo -e "${GREEN}🔄 To restart monitoring:${NC}"
echo -e "  ./start-monitoring.sh"
