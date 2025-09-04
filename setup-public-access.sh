#!/bin/bash

set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}Setting up permanent public access for CvetOchey application...${NC}"

# Create static public IPs
echo -e "${YELLOW}Creating static public IPs...${NC}"
FRONTEND_IP=$(yc vpc address create --name frontend-public-ip --external-ipv4 zone=ru-central1-a --format json | jq -r '.external_ipv4_address.address')
BACKEND_IP=$(yc vpc address create --name backend-public-ip --external-ipv4 zone=ru-central1-a --format json | jq -r '.external_ipv4_address.address')

echo "Frontend IP: $FRONTEND_IP"
echo "Backend IP: $BACKEND_IP"

# Get NodePort assignments
echo -e "${YELLOW}Getting NodePort assignments...${NC}"
FRONTEND_NODEPORT=$(kubectl get service frontend-service -n cvetochey -o jsonpath='{.spec.ports[0].nodePort}')
BACKEND_NODEPORT=$(kubectl get service backend-service -n cvetochey -o jsonpath='{.spec.ports[0].nodePort}')

echo "Frontend NodePort: $FRONTEND_NODEPORT"
echo "Backend NodePort: $BACKEND_NODEPORT"

# Create Network Load Balancers
echo -e "${YELLOW}Creating Network Load Balancers...${NC}"
yc load-balancer network-load-balancer create \
  --name cvetochey-nlb \
  --region-id ru-central1 \
  --type external \
  --listener name=frontend,port=80,target-port=$FRONTEND_NODEPORT,protocol=tcp,external-ip-version=ipv4,external-address=$FRONTEND_IP

yc load-balancer network-load-balancer create \
  --name cvetochey-backend-nlb \
  --region-id ru-central1 \
  --type external \
  --listener name=backend,port=8080,target-port=$BACKEND_NODEPORT,protocol=tcp,external-ip-version=ipv4,external-address=$BACKEND_IP

# Get target group ID
TARGET_GROUP_ID=$(yc load-balancer target-group list --format json | jq -r '.[] | select(.name | contains("k8s-")) | .id')
echo "Target Group ID: $TARGET_GROUP_ID"

# Attach target groups
echo -e "${YELLOW}Attaching target groups...${NC}"
yc load-balancer network-load-balancer attach-target-group \
  --name cvetochey-nlb \
  --target-group target-group-id=$TARGET_GROUP_ID,healthcheck-name=frontend-health,healthcheck-http-port=$FRONTEND_NODEPORT

yc load-balancer network-load-balancer attach-target-group \
  --name cvetochey-backend-nlb \
  --target-group target-group-id=$TARGET_GROUP_ID,healthcheck-name=backend-health,healthcheck-http-port=$BACKEND_NODEPORT,healthcheck-http-path=/actuator/health

echo -e "${GREEN}Public access setup complete!${NC}"
echo -e "${GREEN}Frontend: http://$FRONTEND_IP${NC}"
echo -e "${GREEN}Backend: http://$BACKEND_IP:8080${NC}"
echo -e "${GREEN}Backend Health: http://$BACKEND_IP:8080/actuator/health${NC}"

# Test the endpoints
echo -e "${YELLOW}Testing endpoints...${NC}"
sleep 10
echo "Frontend test:"
curl -s http://$FRONTEND_IP | head -2

echo -e "\nBackend health test:"
curl -s http://$BACKEND_IP:8080/actuator/health
