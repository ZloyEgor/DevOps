# 🔧 GitHub Workflow Trigger Fix

## ❌ Problem
GitHub Actions workflows were **not triggering** on Pull Requests from `feature/test` branch to `dev` branch.

## 🔍 Root Cause
The workflows were configured to only trigger on Pull Requests targeting the `master` branch:

### **Before (Broken)**:
```yaml
pull_request:
  branches: [ master ]  # ❌ Only PRs to master
```

### **After (Fixed)**:
```yaml
pull_request:
  branches: [ master, dev ]  # ✅ PRs to master OR dev
```

## ✅ Files Updated

### 1. **CI Workflow** (`ci.yaml`)
```yaml
on:
  push:
    branches:
      - master
      - dev
  pull_request:
    branches:
      - master  # ← Added
      - dev     # ← Added
```

### 2. **Backend Deploy** (`backend-deploy.yml`)
```yaml
on:
  push:
    branches: [ master, dev ]
    paths:
      - 'backend/**'
  pull_request:
    branches: [ master, dev ]  # ← Added dev
    paths:
      - 'backend/**'
```

### 3. **Frontend Deploy** (`frontend-deploy.yml`)
```yaml
on:
  push:
    branches: [ master, dev ]
    paths:
      - 'frontend/**'
  pull_request:
    branches: [ master, dev ]  # ← Added dev
    paths:
      - 'frontend/**'
```

## 🚀 Workflow Behavior Now

### **Pull Request Triggers**:

#### **PR to `master` branch** (Production):
- ✅ **CI workflow** runs (lint, test, build)
- ✅ **Backend deploy** workflow runs (if backend changes)
- ✅ **Frontend deploy** workflow runs (if frontend changes)
- ❌ **No actual deployment** (PR validation only)

#### **PR to `dev` branch** (Development):
- ✅ **CI workflow** runs (lint, test, build)
- ✅ **Backend deploy** workflow runs (if backend changes)
- ✅ **Frontend deploy** workflow runs (if frontend changes)
- ❌ **No actual deployment** (PR validation only)

### **Push Triggers**:

#### **Push to `master` branch**:
- ✅ **CI workflow** runs
- ✅ **Deploy workflows** run + **Deploy to Kubernetes**
- ✅ **Images tagged with `latest`**

#### **Push to `dev` branch**:
- ✅ **CI workflow** runs
- ✅ **Deploy workflows** run (build + push images)
- ❌ **No Kubernetes deployment** (dev branch)
- ✅ **Images tagged with `dev-<commit>`**

## 🧪 Test Your Fix

### 1. **Test PR to Dev Branch**:
```bash
# Create feature branch
git checkout -b feature/test-workflow
echo "# Test PR to dev $(date)" >> README.md
git add . && git commit -m "test: PR workflow trigger"
git push origin feature/test-workflow

# Create PR: feature/test-workflow → dev
# Expected: CI workflows should now run! ✅
```

### 2. **Test PR to Master Branch**:
```bash
# Create PR: dev → master
# Expected: CI workflows run ✅
```

### 3. **Test Direct Push**:
```bash
# Push to dev
git checkout dev
echo "# Test dev push $(date)" >> README.md
git add . && git commit -m "test: dev push"
git push origin dev
# Expected: Build + push images (no deployment)

# Push to master
git checkout master
echo "# Test master push $(date)" >> README.md
git add . && git commit -m "deploy: master push"
git push origin master  
# Expected: Build + push + deploy to Kubernetes
```

## 📊 Workflow Matrix

| Trigger | Branch | CI | Build Images | Deploy K8s | Image Tags |
|---------|--------|----|--------------|-----------:|------------|
| **PR → master** | any → master | ✅ | ✅ | ❌ | `pr-<number>` |
| **PR → dev** | any → dev | ✅ | ✅ | ❌ | `pr-<number>` |
| **Push master** | master | ✅ | ✅ | ✅ | `latest`, `master-<commit>` |
| **Push dev** | dev | ✅ | ✅ | ❌ | `dev-<commit>` |

## 🎯 Expected Behavior

Your **PR from `feature/test` to `dev`** should now trigger:

1. ✅ **CI workflow** - Lint, test, build validation
2. ✅ **Backend deploy workflow** - Docker build (if backend changes)
3. ✅ **Frontend deploy workflow** - Docker build (if frontend changes)

The workflows will **validate your code** but **won't deploy** to Kubernetes (since it's a PR, not a push to master).

---

🚀 **Problem Fixed!** Your GitHub Actions workflows will now trigger on PRs to both `master` and `dev` branches.



