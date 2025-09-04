#!/bin/bash

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
REGISTRY="cr.yandex"
REGISTRY_ID="crpqt390b8gk59ipqid8"
IMAGE_NAME="cvetochey-frontend"
TAG="${1:-latest}"

echo -e "${GREEN}Building frontend with memory optimization...${NC}"

# Check available memory
TOTAL_MEM=$(docker system info | grep "Total Memory" | awk '{print $3}')
echo -e "${YELLOW}Available Docker memory: $TOTAL_MEM${NC}"

if [[ "$TOTAL_MEM" < "4" ]]; then
    echo -e "${RED}Warning: Docker has less than 4GB memory. Multi-arch build may fail.${NC}"
    echo -e "${YELLOW}Consider increasing Docker memory in Docker Desktop settings.${NC}"
fi

# Clean up before build
echo -e "${YELLOW}Cleaning Docker cache...${NC}"
docker builder prune -f
docker system prune -f

# Use default builder to avoid buildx memory overhead
docker buildx use default

cd frontend

echo -e "${GREEN}Building AMD64 image with memory optimization...${NC}"
docker build \
    --platform linux/amd64 \
    --build-arg NODE_OPTIONS="--max_old_space_size=1536 --optimize_for_size" \
    --tag ${REGISTRY}/${REGISTRY_ID}/${IMAGE_NAME}:${TAG}-amd64 \
    .

echo -e "${GREEN}Pushing AMD64 image...${NC}"
docker push ${REGISTRY}/${REGISTRY_ID}/${IMAGE_NAME}:${TAG}-amd64

echo -e "${GREEN}Cleaning cache before ARM64 build...${NC}"
docker builder prune -f

# Wait a moment for memory cleanup
sleep 5

echo -e "${GREEN}Building ARM64 image with memory optimization...${NC}"
docker build \
    --platform linux/arm64 \
    --build-arg NODE_OPTIONS="--max_old_space_size=1536 --optimize_for_size" \
    --tag ${REGISTRY}/${REGISTRY_ID}/${IMAGE_NAME}:${TAG}-arm64 \
    .

echo -e "${GREEN}Pushing ARM64 image...${NC}"
docker push ${REGISTRY}/${REGISTRY_ID}/${IMAGE_NAME}:${TAG}-arm64

# Create multi-arch manifest using docker manifest (lighter than buildx)
echo -e "${GREEN}Creating multi-arch manifest...${NC}"
docker manifest create ${REGISTRY}/${REGISTRY_ID}/${IMAGE_NAME}:${TAG} \
    --amend ${REGISTRY}/${REGISTRY_ID}/${IMAGE_NAME}:${TAG}-amd64 \
    --amend ${REGISTRY}/${REGISTRY_ID}/${IMAGE_NAME}:${TAG}-arm64

docker manifest push ${REGISTRY}/${REGISTRY_ID}/${IMAGE_NAME}:${TAG}

echo -e "${GREEN}Verifying multi-arch manifest...${NC}"
docker manifest inspect ${REGISTRY}/${REGISTRY_ID}/${IMAGE_NAME}:${TAG}

echo -e "${GREEN}Frontend multi-arch build completed successfully!${NC}"
echo -e "${YELLOW}Cleaning up...${NC}"
docker system prune -f
