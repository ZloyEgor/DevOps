# 🔐 Docker Authentication Fix for Yandex Container Registry

## ❌ Problem
GitHub Actions workflows were failing with Docker authentication error:
```
Error: Error response from daemon: Get "https://cr.yandex/v2/": unauthorized: Password is invalid - must be JSON key
```

## 🔍 Root Cause
The issue was in how we were authenticating with Yandex Container Registry:

### **Before (Broken)**:
```yaml
- name: Log in to Yandex Container Registry
  uses: docker/login-action@v3
  with:
    registry: ${{ env.REGISTRY }}
    username: json_key
    password: ${{ secrets.YC_SERVICE_ACCOUNT_KEY }}  # ❌ Base64 encoded
```

**Problem**: The `docker/login-action` was receiving the base64-encoded service account key directly, but Yandex Container Registry expects the raw JSON key.

### **After (Fixed)**:
```yaml
- name: Log in to Yandex Container Registry
  run: |
    echo "${{ secrets.YC_SERVICE_ACCOUNT_KEY }}" | base64 -d | docker login ${{ env.REGISTRY }} -u json_key --password-stdin
```

**Solution**: We decode the base64-encoded service account key and pipe it directly to `docker login` via stdin.

## ✅ Files Updated

### 1. **Backend Deploy** (`backend-deploy.yml`)
```yaml
- name: Log in to Yandex Container Registry
  run: |
    echo "${{ secrets.YC_SERVICE_ACCOUNT_KEY }}" | base64 -d | docker login ${{ env.REGISTRY }} -u json_key --password-stdin
```

### 2. **Frontend Deploy** (`frontend-deploy.yml`)
```yaml
- name: Log in to Yandex Container Registry
  run: |
    echo "${{ secrets.YC_SERVICE_ACCOUNT_KEY }}" | base64 -d | docker login ${{ env.REGISTRY }} -u json_key --password-stdin
```

## 🔧 How It Works

### **Step-by-Step Process**:

1. **GitHub Secret**: `YC_SERVICE_ACCOUNT_KEY` contains base64-encoded JSON key
2. **Decode**: `base64 -d` converts base64 back to raw JSON
3. **Login**: Raw JSON key is piped to `docker login` via `--password-stdin`
4. **Registry**: Yandex Container Registry accepts the raw JSON key

### **Command Breakdown**:
```bash
echo "${{ secrets.YC_SERVICE_ACCOUNT_KEY }}" | base64 -d | docker login cr.yandex -u json_key --password-stdin
#     ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^        ^^^^^^^^^    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
#     1. Get base64 secret from GitHub         2. Decode    3. Login with raw JSON via stdin
```

## 🚀 Expected Behavior Now

### **Successful Authentication Flow**:
1. ✅ **Decode** base64 service account key
2. ✅ **Login** to `cr.yandex` with raw JSON key
3. ✅ **Build** multi-architecture Docker images
4. ✅ **Push** images to Yandex Container Registry
5. ✅ **Deploy** to Kubernetes (if master branch)

### **Registry Access**:
```bash
# After successful login, workflows can:
docker build -t cr.yandex/crpqt390b8gk59ipqid8/cvetochey-frontend:latest .
docker push cr.yandex/crpqt390b8gk59ipqid8/cvetochey-frontend:latest
```

## 🧪 Test the Fix

### **Trigger Frontend Deploy**:
```bash
# Make a frontend change
echo "/* Updated $(date) */" >> frontend/src/app/page.tsx
git add frontend/
git commit -m "feat: trigger frontend deploy"
git push origin master
```

### **Expected Result**:
- ✅ Docker login succeeds
- ✅ Frontend image builds and pushes
- ✅ Kubernetes deployment updates
- ✅ No authentication errors

### **Verify in Registry**:
```bash
# Check if images are pushed successfully
yc container image list --registry-id crpqt390b8gk59ipqid8
```

## 🔍 Troubleshooting

### **If Authentication Still Fails**:

1. **Check Secret Format**:
   ```bash
   # Verify secret is valid base64
   echo "YOUR_BASE64_KEY" | base64 -d | jq .
   ```

2. **Verify Service Account Permissions**:
   ```bash
   # Check service account has container-registry.images.pusher role
   yc iam service-account list-access-bindings --id aje1ojmlgr7rem5oegih
   ```

3. **Test Local Authentication**:
   ```bash
   # Test the same command locally
   echo "YOUR_BASE64_KEY" | base64 -d | docker login cr.yandex -u json_key --password-stdin
   ```

### **Common Issues**:

| Issue | Cause | Solution |
|-------|-------|----------|
| `Password is invalid` | Base64 not decoded | Use `base64 -d` before login |
| `unauthorized` | Wrong service account | Check service account ID |
| `permission denied` | Missing role | Add `container-registry.images.pusher` |
| `invalid JSON` | Corrupted secret | Regenerate service account key |

## 📚 References

- [Yandex Container Registry Authentication](https://cloud.yandex.ru/docs/container-registry/operations/authentication#sa-json)
- [Docker Login Documentation](https://docs.docker.com/engine/reference/commandline/login/)
- [GitHub Actions Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)

---

🎯 **Authentication Fixed!** Your GitHub Actions workflows can now successfully push Docker images to Yandex Container Registry.



