# 🔄 Branch Names Updated for GitHub Actions

## ✅ Changes Applied

### 📝 Updated Workflows
- ✅ **`backend-deploy.yml`** - Updated all branch references
- ✅ **`frontend-deploy.yml`** - Updated all branch references  
- ✅ **`ci.yaml`** - Updated trigger branches
- ✅ **Documentation** - Updated all references in README files

### 🌿 Branch Configuration

#### **Before (Incorrect)**:
```yaml
branches: [ main, develop ]
pull_request:
  branches: [ main ]
```

#### **After (Correct)**:
```yaml
branches: [ master, dev ]
pull_request:
  branches: [ master ]
```

### 🚀 Deployment Triggers

#### **CI Workflow** (`ci.yaml`):
- **Triggers**: Push to `master` or `dev`, PRs to `master`
- **Actions**: Lint, test, build, security scan
- **Deployment**: No deployment (CI only)

#### **Backend Deploy** (`backend-deploy.yml`):
- **Triggers**: Push to `master` or `dev` with backend changes
- **Build**: Multi-arch Docker image
- **Deploy**: Only on `master` branch pushes
- **Registry**: `cr.yandex/crpqt390b8gk59ipqid8/cvetochey-backend`

#### **Frontend Deploy** (`frontend-deploy.yml`):
- **Triggers**: Push to `master` or `dev` with frontend changes  
- **Build**: Multi-arch Docker image
- **Deploy**: Only on `master` branch pushes
- **Registry**: `cr.yandex/crpqt390b8gk59ipqid8/cvetochey-frontend`

### 🏷️ Image Tagging Strategy

#### **Master Branch**:
- `latest` - Latest production build
- `master-<commit-hash>` - Specific commit

#### **Dev Branch**:
- `dev-<commit-hash>` - Development builds
- No `latest` tag (not production)

#### **Pull Requests**:
- `pr-<number>` - PR validation builds

### 🔍 Key Changes Made

1. **Branch References**: `main` → `master`, `develop` → `dev`
2. **Deployment Conditions**: `refs/heads/main` → `refs/heads/master`
3. **Latest Tag Logic**: Only applies to `master` branch
4. **Documentation**: Updated all branch references

### ✅ Validation

Run this to verify the changes:
```bash
# Check workflow syntax
grep -r "master\|dev" .github/workflows/

# Verify no old branch names remain
grep -r "main\|develop" .github/workflows/ || echo "✅ No old branch names found"
```

### 🚀 Testing the Pipeline

1. **Push to dev branch** (builds only):
```bash
git checkout dev
echo "# Dev test $(date)" >> README.md
git add . && git commit -m "test: dev branch CI" && git push origin dev
```

2. **Push to master branch** (builds + deploys):
```bash
git checkout master
echo "# Master deploy $(date)" >> README.md  
git add . && git commit -m "deploy: trigger production deployment" && git push origin master
```

### 📊 Expected Behavior

#### **Dev Branch Push**:
- ✅ CI workflow runs (lint, test, build)
- ✅ Docker images built and pushed
- ❌ No Kubernetes deployment (dev branch)

#### **Master Branch Push**:
- ✅ CI workflow runs (lint, test, build)
- ✅ Docker images built and pushed with `latest` tag
- ✅ Kubernetes deployment updated
- ✅ Health checks performed

## 🎯 Ready for Production

Your GitHub Actions workflows are now correctly configured for your branch structure:
- **`master`** - Production deployments
- **`dev`** - Development builds
- **Pull Requests** - Validation builds

The CI/CD pipeline will now work seamlessly with your actual Git branch names! 🚀



