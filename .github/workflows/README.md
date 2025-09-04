# 🚀 GitHub Actions CI/CD Pipeline

## 📋 Overview

This directory contains GitHub Actions workflows for automated CI/CD pipeline that builds, tests, and deploys the CvetOchey application to Yandex Cloud Kubernetes.

## 🔄 Workflows

### 1. `ci.yaml` - Continuous Integration
**Triggers**: Push to `master`/`dev`, Pull Requests to `master`

**Jobs**:
- **Backend Lint**: Checkstyle validation
- **Backend Test**: Unit tests with PostgreSQL
- **Backend Build**: Maven package build
- **Frontend Lint**: ESLint validation  
- **Frontend Test**: Jest unit tests
- **Frontend Build**: Next.js production build
- **Security Scan**: Trivy vulnerability scanning

### 2. `backend-deploy.yml` - Backend Deployment
**Triggers**: Push to `master`/`dev` with backend changes

**Process**:
1. **Multi-arch Docker Build** (AMD64/ARM64)
2. **Push to Yandex Container Registry**
3. **Deploy to Kubernetes** (master branch only)
4. **Health Check Verification**

### 3. `frontend-deploy.yml` - Frontend Deployment  
**Triggers**: Push to `master`/`dev` with frontend changes

**Process**:
1. **Multi-arch Docker Build** (AMD64/ARM64)
2. **Push to Yandex Container Registry**
3. **Deploy to Kubernetes** (master branch only)
4. **Accessibility Verification**

## 🔐 Required Secrets

Configure these secrets in your GitHub repository settings:

### Yandex Cloud Secrets
```bash
YC_SERVICE_ACCOUNT_KEY    # Base64 encoded service account JSON key
YC_CLOUD_ID              # Your Yandex Cloud ID  
YC_FOLDER_ID             # Your Yandex Cloud folder ID
```

### How to Get Yandex Cloud Secrets

#### 1. Service Account Key
```bash
# Create service account (if not exists)
yc iam service-account create --name github-actions-sa

# Assign required roles
yc resource-manager folder add-access-binding <folder-id> \
  --role container-registry.images.pusher \
  --subject serviceAccount:<service-account-id>

yc resource-manager folder add-access-binding <folder-id> \
  --role k8s.cluster-api.cluster-admin \
  --subject serviceAccount:<service-account-id>

# Create and download key
yc iam key create --service-account-name github-actions-sa --output key.json

# Base64 encode the key
base64 -i key.json | tr -d '\n'
```

#### 2. Cloud and Folder IDs
```bash
# Get Cloud ID
yc config list

# Get Folder ID  
yc resource-manager folder list
```

## 🚀 Deployment Process

### Automatic Deployment
1. **Push to master branch** with changes in `backend/` or `frontend/`
2. **CI pipeline runs** (lint, test, build)
3. **Docker images built** with multi-architecture support
4. **Images pushed** to Yandex Container Registry
5. **Kubernetes deployment updated** with new images
6. **Health checks performed** to verify deployment

### Manual Deployment
```bash
# Trigger workflow manually via GitHub Actions UI
# Or push to master branch with changes
git add .
git commit -m "Deploy: update backend/frontend"
git push origin master
```

## 📊 Registry Structure

### Yandex Container Registry
```
cr.yandex/crpqt390b8gk59ipqid8/
├── cvetochey-backend:latest          # Latest backend image
├── cvetochey-backend:main-abc123     # Branch + commit hash
├── cvetochey-frontend:latest         # Latest frontend image  
└── cvetochey-frontend:main-def456    # Branch + commit hash
```

### Image Tags Strategy
- **`latest`**: Latest master branch build
- **`master-<commit>`**: Specific commit on master branch
- **`dev-<commit>`**: Specific commit on dev branch
- **`pr-<number>`**: Pull request builds

## 🔍 Monitoring Deployments

### GitHub Actions UI
- **Actions tab**: View workflow runs
- **Build logs**: Detailed step-by-step output
- **Artifacts**: Download build artifacts

### Kubernetes Monitoring
```bash
# Check deployment status
kubectl get deployments -n cvetochey

# View rollout history
kubectl rollout history deployment/backend -n cvetochey
kubectl rollout history deployment/frontend -n cvetochey

# Check pod logs
kubectl logs -l app=backend -n cvetochey --tail=100
kubectl logs -l app=frontend -n cvetochey --tail=100
```

### Grafana Dashboard
Monitor deployment impact in real-time:
- **HTTP Request Rate**: Should show traffic resuming after deployment
- **Error Rate**: Should remain low during deployment
- **Pod Status**: Shows new pods starting up
- **Response Time**: Should remain stable

## 🛠️ Troubleshooting

### Common Issues

#### 1. Docker Build Fails
```yaml
# Check Dockerfile syntax
docker build -t test ./backend
docker build -t test ./frontend

# Verify build context
ls -la backend/
ls -la frontend/
```

#### 2. Registry Push Fails
```bash
# Test registry authentication locally
echo "$YC_SERVICE_ACCOUNT_KEY" | base64 -d | docker login cr.yandex --username json_key --password-stdin

# Check registry permissions
yc container registry list
yc container image list --registry-id crpqt390b8gk59ipqid8
```

#### 3. Kubernetes Deployment Fails
```bash
# Check cluster connectivity
yc managed-kubernetes cluster get-credentials cvetochey-cluster --external
kubectl cluster-info

# Verify deployment configuration
kubectl describe deployment backend -n cvetochey
kubectl describe deployment frontend -n cvetochey

# Check image pull status
kubectl get events -n cvetochey --sort-by='.lastTimestamp'
```

#### 4. Health Checks Fail
```bash
# Test health endpoints directly
kubectl port-forward svc/backend-service 8080:8080 -n cvetochey &
curl http://localhost:8080/actuator/health

kubectl port-forward svc/frontend-service 3000:3000 -n cvetochey &
curl http://localhost:3000/
```

### Workflow Debugging

#### Enable Debug Logging
Add to workflow:
```yaml
env:
  ACTIONS_STEP_DEBUG: true
  ACTIONS_RUNNER_DEBUG: true
```

#### Check Secret Configuration
```yaml
- name: Debug secrets (DO NOT USE IN PRODUCTION)
  run: |
    echo "YC_CLOUD_ID length: ${#YC_CLOUD_ID}"
    echo "YC_FOLDER_ID length: ${#YC_FOLDER_ID}"  
    echo "YC_SERVICE_ACCOUNT_KEY length: ${#YC_SERVICE_ACCOUNT_KEY}"
```

## 🔄 Rollback Strategy

### Automatic Rollback
```bash
# Rollback to previous version
kubectl rollout undo deployment/backend -n cvetochey
kubectl rollout undo deployment/frontend -n cvetochey

# Rollback to specific revision
kubectl rollout undo deployment/backend --to-revision=2 -n cvetochey
```

### Manual Rollback
```bash
# Use previous image tag
kubectl set image deployment/backend backend=cr.yandex/crpqt390b8gk59ipqid8/cvetochey-backend:main-previous-commit -n cvetochey
```

## 📈 Performance Optimization

### Build Optimization
- **Docker Layer Caching**: Enabled with GitHub Actions cache
- **Multi-stage Builds**: Minimize final image size
- **Parallel Jobs**: CI jobs run in parallel when possible

### Deployment Optimization
- **Rolling Updates**: Zero-downtime deployments
- **Health Checks**: Ensure pods are ready before traffic routing
- **Resource Limits**: Prevent resource exhaustion

## 🔒 Security Best Practices

### Implemented Security
- ✅ **Secret Management**: GitHub Secrets for sensitive data
- ✅ **Least Privilege**: Service accounts with minimal required permissions
- ✅ **Image Scanning**: Trivy vulnerability scanning
- ✅ **Non-root Containers**: Security-hardened Docker images
- ✅ **Network Policies**: Kubernetes network segmentation

### Additional Recommendations
- 🔄 **Rotate Secrets**: Regularly rotate service account keys
- 📊 **Audit Logs**: Monitor deployment activities
- 🛡️ **RBAC**: Implement fine-grained Kubernetes permissions
- 🔍 **Monitoring**: Set up alerts for failed deployments
