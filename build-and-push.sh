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
BACKEND_IMAGE="cvetochey-backend"
FRONTEND_IMAGE="cvetochey-frontend"
TAG="${1:-latest}"

echo -e "${GREEN}Building and pushing multi-architecture Docker images...${NC}"

# Check if Docker buildx is available
if ! docker buildx version &> /dev/null; then
    echo -e "${RED}Docker buildx is not available. Please install Docker Desktop or enable buildx.${NC}"
    exit 1
fi

# Create a new builder instance if it doesn't exist
if ! docker buildx ls | grep -q "multiarch-builder"; then
    echo -e "${YELLOW}Creating multiarch builder...${NC}"
    docker buildx create --name multiarch-builder --use --bootstrap
else
    echo -e "${YELLOW}Using existing multiarch builder...${NC}"
    docker buildx use multiarch-builder
fi

# Build and push backend
echo -e "${GREEN}Building backend for multiple architectures...${NC}"
cd backend
docker buildx build \
    --platform linux/amd64,linux/arm64 \
    --tag ${REGISTRY}/${REGISTRY_ID}/${BACKEND_IMAGE}:${TAG} \
    --tag ${REGISTRY}/${REGISTRY_ID}/${BACKEND_IMAGE}:latest \
    --push \
    .

# Build and push frontend
echo -e "${GREEN}Building frontend for multiple architectures...${NC}"
cd ../frontend
docker buildx build \
    --platform linux/amd64,linux/arm64 \
    --tag ${REGISTRY}/${REGISTRY_ID}/${FRONTEND_IMAGE}:${TAG} \
    --tag ${REGISTRY}/${REGISTRY_ID}/${FRONTEND_IMAGE}:latest \
    --push \
    .

cd ..

echo -e "${GREEN}Successfully built and pushed images:${NC}"
echo -e "  ${REGISTRY}/${REGISTRY_ID}/${BACKEND_IMAGE}:${TAG}"
echo -e "  ${REGISTRY}/${REGISTRY_ID}/${FRONTEND_IMAGE}:${TAG}"

# Verify images
echo -e "${YELLOW}Verifying images...${NC}"
docker buildx imagetools inspect ${REGISTRY}/${REGISTRY_ID}/${BACKEND_IMAGE}:${TAG}
docker buildx imagetools inspect ${REGISTRY}/${REGISTRY_ID}/${FRONTEND_IMAGE}:${TAG}

echo -e "${GREEN}Build and push completed successfully!${NC}"
