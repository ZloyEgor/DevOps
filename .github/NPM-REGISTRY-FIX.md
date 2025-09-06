# 📦 NPM Registry Fix for Docker Builds

## ❌ Problem
Docker builds were failing because pnpm was trying to download packages from Yandex internal registry instead of the public npm registry:
```
WARN GET https://npm.yandex-team.ru/classnames/-/classnames-2.5.1.tgz?rbtorrent= error (ETIMEDOUT)
ETIMEDOUT request to https://npm.yandex-team.ru/@emotion%2fhash/-/hash-0.8.0.tgz failed
```

## 🔍 Root Cause
The Docker build environment was inheriting npm/pnpm registry configuration that pointed to the Yandex internal registry (`npm.yandex-team.ru`) instead of the public npm registry (`registry.npmjs.org`).

This can happen due to:
- Global npm/pnpm configuration
- Environment variables
- Inherited configuration from build environment

## ✅ Solution Applied

### 1. **Updated Frontend Dockerfile**
Added explicit registry configuration in both dependency installation and build stages:

```dockerfile
# In deps stage
RUN corepack enable pnpm
RUN pnpm config set registry https://registry.npmjs.org/
RUN npm config set registry https://registry.npmjs.org/

# In builder stage  
RUN corepack enable pnpm
RUN pnpm config set registry https://registry.npmjs.org/
```

### 2. **Created `.npmrc` File**
Added `frontend/.npmrc` to explicitly set the registry:

```
registry=https://registry.npmjs.org/
audit-level=moderate
fund=false
```

### 3. **Registry Configuration Order**
The fix ensures registry is set in this priority order:
1. **Project `.npmrc`** (highest priority)
2. **Docker RUN commands** (explicit configuration)
3. **pnpm/npm config** (fallback)

## 🔧 How It Works

### **Before (Broken)**:
```bash
# Docker build inheriting wrong registry
pnpm i --frozen-lockfile
# → Tries to download from npm.yandex-team.ru ❌
```

### **After (Fixed)**:
```bash
# Explicit registry configuration
pnpm config set registry https://registry.npmjs.org/
pnpm i --frozen-lockfile  
# → Downloads from registry.npmjs.org ✅
```

## 📝 Files Updated

### 1. **Frontend Dockerfile** (`frontend/Dockerfile`)
```dockerfile
# Dependencies stage
RUN corepack enable pnpm
RUN pnpm config set registry https://registry.npmjs.org/
RUN npm config set registry https://registry.npmjs.org/

# Builder stage
RUN corepack enable pnpm
RUN pnpm config set registry https://registry.npmjs.org/
```

### 2. **NPM Configuration** (`frontend/.npmrc`)
```
registry=https://registry.npmjs.org/
audit-level=moderate
fund=false
```

## 🚀 Expected Behavior Now

### **Successful Package Installation**:
1. ✅ **Registry Set** to `https://registry.npmjs.org/`
2. ✅ **Dependencies Downloaded** from public npm registry
3. ✅ **No Timeouts** or connection errors
4. ✅ **Fast Package Resolution** (public CDN)

### **Build Process**:
```bash
# Docker build logs should show:
RUN pnpm config set registry https://registry.npmjs.org/
RUN pnpm i --frozen-lockfile
# → Downloading packages from registry.npmjs.org ✅
```

## 🧪 Test the Fix

### **Trigger Frontend Build**:
```bash
# Test locally first
cd frontend
pnpm config get registry
# Should return: https://registry.npmjs.org/

# Test Docker build
docker build -t test-frontend .
# Should download from registry.npmjs.org
```

### **GitHub Actions Test**:
```bash
# Make a frontend change
echo "/* Registry fix test $(date) */" >> frontend/src/app/page.tsx
git add frontend/
git commit -m "fix: npm registry configuration"
git push origin master
```

### **Expected Result**:
- ✅ No `npm.yandex-team.ru` requests
- ✅ All packages download from `registry.npmjs.org`
- ✅ No timeout errors
- ✅ Successful Docker build

## 🔍 Troubleshooting

### **If Registry Issues Persist**:

1. **Check Registry Configuration**:
   ```bash
   # In Docker container
   pnpm config get registry
   npm config get registry
   ```

2. **Verify .npmrc File**:
   ```bash
   cat frontend/.npmrc
   # Should show: registry=https://registry.npmjs.org/
   ```

3. **Test Registry Access**:
   ```bash
   # Test connectivity
   curl -I https://registry.npmjs.org/
   # Should return: HTTP/2 200
   ```

### **Alternative Registries** (if needed):

| Registry | URL | Use Case |
|----------|-----|----------|
| **npm (default)** | `https://registry.npmjs.org/` | Public packages |
| **Yarn** | `https://registry.yarnpkg.com/` | Alternative public |
| **Taobao** | `https://registry.npmmirror.com/` | China mirror |
| **GitHub** | `https://npm.pkg.github.com/` | Private packages |

### **Registry Configuration Commands**:
```bash
# Set registry globally
pnpm config set registry https://registry.npmjs.org/
npm config set registry https://registry.npmjs.org/

# Set for project only
echo "registry=https://registry.npmjs.org/" > .npmrc

# Verify configuration
pnpm config get registry
npm config get registry
```

## 📚 References

- [pnpm Registry Configuration](https://pnpm.io/npmrc#registry)
- [npm Registry Documentation](https://docs.npmjs.com/cli/v7/using-npm/registry)
- [Docker Multi-stage Builds](https://docs.docker.com/develop/dev-best-practices/dockerfile_best-practices/#use-multi-stage-builds)

---

📦 **NPM Registry Fixed!** Your Docker builds will now download packages from the public npm registry instead of internal Yandex registry.



