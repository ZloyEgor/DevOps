# 🐳 Docker Commands Reference

## Registry Management

### Authentication
```bash
# Configure Docker for Yandex Container Registry
yc container registry configure-docker

# Manual login (if needed)
echo "$(yc iam create-token)" | docker login --username iam --password-stdin cr.yandex
```

### Registry Operations
```bash
# List registries
yc container registry list

# List images in registry
yc container image list --folder-id b1gs0cg1voiht42pp513

# Delete specific image
yc container image delete <image-id>
```

## Building Images

### Single Architecture Build
```bash
# Backend
cd backend
docker build -t cr.yandex/crpqt390b8gk59ipqid8/cvetochey-backend:latest .
docker push cr.yandex/crpqt390b8gk59ipqid8/cvetochey-backend:latest

# Frontend
cd frontend  
docker build -t cr.yandex/crpqt390b8gk59ipqid8/cvetochey-frontend:latest .
docker push cr.yandex/crpqt390b8gk59ipqid8/cvetochey-frontend:latest
```

### Multi-Architecture Build
```bash
# Create buildx builder (one time)
docker buildx create --name multiarch-builder --use --bootstrap

# Build for multiple architectures
docker buildx build \
    --platform linux/amd64,linux/arm64 \
    --tag cr.yandex/crpqt390b8gk59ipqid8/cvetochey-backend:latest \
    --push \
    backend/

# Inspect multi-arch manifest
docker buildx imagetools inspect cr.yandex/crpqt390b8gk59ipqid8/cvetochey-backend:latest
```

## Testing Images Locally

### Backend Testing
```bash
# Run backend with database
cd backend
docker-compose up -d

# Test endpoints
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/prometheus
```

### Frontend Testing  
```bash
# Run frontend standalone
docker run -p 3000:3000 cr.yandex/crpqt390b8gk59ipqid8/cvetochey-frontend:latest

# With environment variables
docker run -p 3000:3000 \
    -e NEXT_PUBLIC_API_URL=http://localhost:8080 \
    cr.yandex/crpqt390b8gk59ipqid8/cvetochey-frontend:latest
```

## Image Management

### Tagging
```bash
# Tag with version
docker tag cr.yandex/crpqt390b8gk59ipqid8/cvetochey-backend:latest \
           cr.yandex/crpqt390b8gk59ipqid8/cvetochey-backend:v1.0.0

# Push specific version
docker push cr.yandex/crpqt390b8gk59ipqid8/cvetochey-backend:v1.0.0
```

### Cleanup
```bash
# Remove local images
docker rmi cr.yandex/crpqt390b8gk59ipqid8/cvetochey-backend:latest
docker rmi cr.yandex/crpqt390b8gk59ipqid8/cvetochey-frontend:latest

# Clean up build cache
docker builder prune

# Clean up everything
docker system prune -a
```

## Dockerfile Optimization

### Current Optimizations
- ✅ Multi-stage builds
- ✅ Non-root users  
- ✅ Health checks
- ✅ Security updates
- ✅ Minimal base images (Alpine)
- ✅ Layer caching optimization
- ✅ .dockerignore files

### Build Arguments
```bash
# Build with custom Node memory
docker build --build-arg NODE_OPTIONS="--max_old_space_size=8192" frontend/

# Build for specific platform
docker build --platform linux/amd64 backend/
```

## Troubleshooting

### Common Issues
```bash
# Check build context size
du -sh backend/ frontend/

# Build without cache
docker build --no-cache backend/

# Build with verbose output
docker build --progress=plain backend/

# Check buildx builders
docker buildx ls

# Remove and recreate builder
docker buildx rm multiarch-builder
docker buildx create --name multiarch-builder --use --bootstrap
```

### Memory Issues (Frontend)
```bash
# Increase Docker memory limit (Docker Desktop)
# Or build with less parallelism
docker build --build-arg NODE_OPTIONS="--max_old_space_size=4096" frontend/

# Use single architecture for memory-constrained environments
docker build --platform linux/amd64 frontend/
```

## Registry Credentials

### Service Account for CI/CD
```bash
# Create service account
yc iam service-account create --name docker-pusher

# Create key
yc iam key create --service-account-name docker-pusher --output key.json

# Assign registry permissions
yc resource-manager folder add-access-binding b1gs0cg1voiht42pp513 \
  --role container-registry.images.pusher \
  --subject serviceAccount:$(yc iam service-account get docker-pusher --format json | jq -r '.id')

# Use in CI/CD
echo "$SERVICE_ACCOUNT_KEY" | base64 -d > key.json
yc config set service-account-key key.json
```

## Automated Scripts

### Build Script (build-and-push.sh)
```bash
#!/bin/bash
set -e

REGISTRY="cr.yandex"  
REGISTRY_ID="crpqt390b8gk59ipqid8"
TAG="${1:-latest}"

# Backend
echo "Building backend..."
cd backend
docker buildx build \
    --platform linux/amd64,linux/arm64 \
    --tag ${REGISTRY}/${REGISTRY_ID}/cvetochey-backend:${TAG} \
    --push \
    .

# Frontend  
echo "Building frontend..."
cd ../frontend
docker build -t ${REGISTRY}/${REGISTRY_ID}/cvetochey-frontend:${TAG} .
docker push ${REGISTRY}/${REGISTRY_ID}/cvetochey-frontend:${TAG}

echo "Build complete!"
```

### Usage
```bash
# Build latest
./build-and-push.sh

# Build with version tag
./build-and-push.sh v1.2.3
```
