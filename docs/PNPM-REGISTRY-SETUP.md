# 📦 pnpm Registry Configuration for CvetOchey

## 🎯 Overview
This repository is configured to use the **public npm registry** (`https://registry.npmjs.org/`) for all package downloads, preventing any fallback to internal or alternative registries.

## ✅ Configuration Applied

### 1. **Repository Root `.npmrc`**
```
registry=https://registry.npmjs.org/

# Security and performance settings
audit-level=moderate
fund=false
save-exact=true

# Force public registry for all scopes
@ant-design:registry=https://registry.npmjs.org/
@tanstack:registry=https://registry.npmjs.org/
@types:registry=https://registry.npmjs.org/
@babel:registry=https://registry.npmjs.org/
@next:registry=https://registry.npmjs.org/
@emotion:registry=https://registry.npmjs.org/
@adobe:registry=https://registry.npmjs.org/
@ampproject:registry=https://registry.npmjs.org/

# Workspace settings
auto-install-peers=true
strict-peer-dependencies=false
```

### 2. **Frontend `.npmrc`**
```
registry=https://registry.npmjs.org/

# Security and performance settings
audit-level=moderate
fund=false
save-exact=true
package-lock=false

# Disable telemetry and unnecessary features
disable-self-update-notifier=true
update-notifier=false

# Force public registry for all scopes
@ant-design:registry=https://registry.npmjs.org/
@tanstack:registry=https://registry.npmjs.org/
@types:registry=https://registry.npmjs.org/
@babel:registry=https://registry.npmjs.org/
@next:registry=https://registry.npmjs.org/
@emotion:registry=https://registry.npmjs.org/
```

### 3. **pnpm Global Configuration**
```bash
pnpm config set registry https://registry.npmjs.org/
pnpm config set audit-level moderate
pnpm config set fund false
pnpm config set save-exact true
pnpm config set auto-install-peers true
```

## 🔧 Setup Script

### **Automated Setup**
Run the setup script to configure pnpm registry settings:

```bash
./setup-pnpm-registry.sh
```

### **Manual Setup**
If you need to configure manually:

```bash
# Set registry
pnpm config set registry https://registry.npmjs.org/

# Configure settings
pnpm config set audit-level moderate
pnpm config set fund false
pnpm config set save-exact true
pnpm config set auto-install-peers true
pnpm config set strict-peer-dependencies false

# Verify configuration
pnpm config get registry
```

## 🔍 Verification

### **Check Current Configuration**
```bash
# Check pnpm config
pnpm config list | grep registry

# Check .npmrc files
cat .npmrc
cat frontend/.npmrc

# Test package resolution
cd frontend && pnpm config get registry
```

### **Expected Output**
```bash
$ pnpm config get registry
https://registry.npmjs.org/

$ pnpm config list | grep registry
registry=https://registry.npmjs.org/
@ant-design:registry=https://registry.npmjs.org/
@tanstack:registry=https://registry.npmjs.org/
# ... other scoped registries
```

## 📊 Registry Resolution Priority

| Priority | Configuration Method | Status |
|----------|---------------------|--------|
| **1 (Highest)** | Command line `--registry` flag | ✅ Used in Dockerfile |
| **2** | Project `.npmrc` | ✅ Configured |
| **3** | User `~/.npmrc` | ✅ Configured via pnpm config |
| **4** | Global `/etc/npmrc` | ⚠️ Not modified |
| **5 (Lowest)** | npm defaults | ❌ Overridden |

## 🚀 Benefits

### **Reliability**
- ✅ **No timeouts** from inaccessible internal registries
- ✅ **Consistent downloads** from public CDN
- ✅ **Fast package resolution** with global mirrors

### **Security**
- ✅ **Public packages only** from verified registry
- ✅ **No fallback** to unknown registries
- ✅ **Scoped registries** explicitly configured

### **Development**
- ✅ **Same packages** across all environments
- ✅ **Reproducible builds** with exact versions
- ✅ **No registry conflicts** in CI/CD

## 🔄 Maintenance

### **Regenerate Lockfile** (if needed)
```bash
cd frontend
rm pnpm-lock.yaml
pnpm install
```

### **Add New Scoped Registry**
```bash
# Add to .npmrc
echo "@newscope:registry=https://registry.npmjs.org/" >> .npmrc
echo "@newscope:registry=https://registry.npmjs.org/" >> frontend/.npmrc
```

### **Reset Configuration**
```bash
# Run setup script
./setup-pnpm-registry.sh

# Or manually reset
pnpm config delete registry
pnpm config set registry https://registry.npmjs.org/
```

## 🧪 Testing

### **Test Package Installation**
```bash
cd frontend

# Clean install
rm -rf node_modules pnpm-lock.yaml
pnpm install

# Should show downloads from registry.npmjs.org
```

### **Test Docker Build**
```bash
# Build frontend Docker image
docker build -t test-frontend ./frontend

# Should complete without registry timeouts
```

### **Test GitHub Actions**
```bash
# Push changes and check Actions logs
git add .npmrc frontend/.npmrc setup-pnpm-registry.sh
git commit -m "feat: configure pnpm public registry"
git push origin master

# Check GitHub Actions logs for successful package downloads
```

## 📚 Related Files

- **`.npmrc`** - Repository root configuration
- **`frontend/.npmrc`** - Frontend-specific configuration
- **`setup-pnpm-registry.sh`** - Automated setup script
- **`frontend/Dockerfile`** - Docker build configuration
- **`frontend/pnpm-lock.yaml`** - Clean lockfile (no hardcoded URLs)

## 🆘 Troubleshooting

### **If Packages Still Download from Wrong Registry**
1. **Check lockfile**: `grep -i "yandex\|registry" frontend/pnpm-lock.yaml`
2. **Regenerate lockfile**: `rm frontend/pnpm-lock.yaml && cd frontend && pnpm install`
3. **Verify config**: `pnpm config get registry`
4. **Run setup script**: `./setup-pnpm-registry.sh`

### **If Docker Build Fails**
1. **Check Dockerfile** has registry configuration
2. **Verify .npmrc** is copied to Docker context
3. **Test locally**: `docker build -t test ./frontend`

---

🎯 **Registry Configuration Complete!** All npm packages will be downloaded from the public registry (`registry.npmjs.org`).
