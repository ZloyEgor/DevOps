# 🚀 Increase Docker Memory for Multi-Arch Builds

## Docker Desktop Settings

### macOS/Windows:
1. Open **Docker Desktop**
2. Go to **Settings** → **Resources** → **Advanced**
3. Increase **Memory** to at least **8GB** (recommended 12GB)
4. Increase **Swap** to **4GB**
5. Click **Apply & Restart**

### Command Line Check:
```bash
# Check current memory
docker system info | grep "Total Memory"

# Should show at least 8GiB after restart
```

## Alternative: Cloud Build

If local memory is limited, use Yandex Cloud Build:

```bash
# Create cloud build configuration
yc builds trigger create github \
  --name frontend-multiarch \
  --repository-owner YOUR_GITHUB_USERNAME \
  --repository-name YOUR_REPO_NAME \
  --branch-pattern "main" \
  --build-spec-path .yandex-cloud-build.yml
```

## Temporary Workaround: Build on Different Machine

```bash
# On a machine with more memory (8GB+ RAM)
git clone YOUR_REPO
cd YOUR_REPO
./build-frontend-multiarch.sh
```
