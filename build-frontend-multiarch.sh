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

echo -e "${GREEN}Building frontend for multiple architectures sequentially...${NC}"

# Ensure we have a buildx builder
if ! docker buildx ls | grep -q "multiarch-builder"; then
    echo -e "${YELLOW}Creating multiarch builder...${NC}"
    docker buildx create --name multiarch-builder --use --bootstrap
else
    echo -e "${YELLOW}Using existing multiarch builder...${NC}"
    docker buildx use multiarch-builder
fi

cd frontend

echo -e "${GREEN}Building for AMD64...${NC}"
docker buildx build \
    --platform linux/amd64 \
    --tag ${REGISTRY}/${REGISTRY_ID}/${IMAGE_NAME}:${TAG}-amd64 \
    --push \
    .

echo -e "${GREEN}Building for ARM64...${NC}"
docker buildx build \
    --platform linux/arm64 \
    --tag ${REGISTRY}/${REGISTRY_ID}/${IMAGE_NAME}:${TAG}-arm64 \
    --push \
    .

echo -e "${GREEN}Creating multi-arch manifest...${NC}"
docker buildx imagetools create \
    --tag ${REGISTRY}/${REGISTRY_ID}/${IMAGE_NAME}:${TAG} \
    ${REGISTRY}/${REGISTRY_ID}/${IMAGE_NAME}:${TAG}-amd64 \
    ${REGISTRY}/${REGISTRY_ID}/${IMAGE_NAME}:${TAG}-arm64

echo -e "${GREEN}Verifying multi-arch manifest...${NC}"
docker buildx imagetools inspect ${REGISTRY}/${REGISTRY_ID}/${IMAGE_NAME}:${TAG}

echo -e "${GREEN}Frontend multi-arch build completed successfully!${NC}"
