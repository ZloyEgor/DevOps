# 🏷️ Docker Tag Generation Fix

## ❌ Problem
GitHub Actions workflows were failing with invalid Docker tag error:
```
ERROR: failed to build: invalid tag "cr.yandex/crpqt390b8gk59ipqid8/cvetochey-frontend:-2faa6df": invalid reference format
```

## 🔍 Root Cause
The issue was in the Docker metadata action tag configuration:

### **Before (Broken)**:
```yaml
tags: |
  type=ref,event=branch
  type=ref,event=pr
  type=sha,prefix={{branch}}-     # ❌ {{branch}} was resolving to empty/invalid
  type=raw,value=latest,enable=${{ github.ref == 'refs/heads/master' }}
```

**Problem**: The `{{branch}}` template variable was not resolving correctly for PR events, creating invalid tags like `:-2faa6df` (notice the missing branch name before the dash).

### **After (Fixed)**:
```yaml
tags: |
  type=ref,event=branch
  type=ref,event=pr
  type=sha,prefix=commit-         # ✅ Fixed prefix
  type=raw,value=latest,enable=${{ github.ref == 'refs/heads/master' }}
```

**Solution**: Use a static `commit-` prefix instead of the dynamic `{{branch}}-` template.

## ✅ Files Updated

### 1. **Backend Deploy** (`backend-deploy.yml`)
```yaml
tags: |
  type=ref,event=branch           # master, dev
  type=ref,event=pr              # pr-38
  type=sha,prefix=commit-        # commit-2faa6df
  type=raw,value=latest,enable=${{ github.ref == 'refs/heads/master' }}
```

### 2. **Frontend Deploy** (`frontend-deploy.yml`)
```yaml
tags: |
  type=ref,event=branch           # master, dev  
  type=ref,event=pr              # pr-38
  type=sha,prefix=commit-        # commit-2faa6df
  type=raw,value=latest,enable=${{ github.ref == 'refs/heads/master' }}
```

## 🏷️ Tag Generation Matrix

### **Expected Tags by Event Type**:

| Event Type | Branch/PR | Generated Tags | Example |
|------------|-----------|----------------|---------|
| **Push to master** | `master` | `master`, `commit-abc123`, `latest` | `cvetochey-frontend:master`<br/>`cvetochey-frontend:commit-abc123`<br/>`cvetochey-frontend:latest` |
| **Push to dev** | `dev` | `dev`, `commit-def456` | `cvetochey-frontend:dev`<br/>`cvetochey-frontend:commit-def456` |
| **PR to master** | `PR #38` | `pr-38`, `commit-ghi789` | `cvetochey-frontend:pr-38`<br/>`cvetochey-frontend:commit-ghi789` |
| **PR to dev** | `PR #42` | `pr-42`, `commit-jkl012` | `cvetochey-frontend:pr-42`<br/>`cvetochey-frontend:commit-jkl012` |

### **Tag Breakdown**:
- **`type=ref,event=branch`** → Branch name (e.g., `master`, `dev`)
- **`type=ref,event=pr`** → PR number (e.g., `pr-38`)
- **`type=sha,prefix=commit-`** → Commit hash (e.g., `commit-2faa6df`)
- **`type=raw,value=latest`** → Latest tag (only for master branch)

## 🚀 Expected Behavior Now

### **Successful Tag Generation**:
1. ✅ **Valid tag formats** for all events
2. ✅ **No invalid characters** or empty prefixes
3. ✅ **Consistent naming** across all workflows
4. ✅ **Proper tagging** for registry organization

### **Registry Structure**:
```
cr.yandex/crpqt390b8gk59ipqid8/
├── cvetochey-backend:latest          # Master branch only
├── cvetochey-backend:master          # Master branch pushes
├── cvetochey-backend:dev             # Dev branch pushes
├── cvetochey-backend:pr-38           # Pull request #38
├── cvetochey-backend:commit-2faa6df  # Specific commit
├── cvetochey-frontend:latest         # Master branch only
├── cvetochey-frontend:master         # Master branch pushes
├── cvetochey-frontend:dev            # Dev branch pushes
├── cvetochey-frontend:pr-38          # Pull request #38
└── cvetochey-frontend:commit-2faa6df # Specific commit
```

## 🧪 Test the Fix

### **Trigger a PR Build**:
```bash
# Create a test PR
git checkout -b feature/test-tags
echo "# Test tag generation $(date)" >> README.md
git add . && git commit -m "test: tag generation fix"
git push origin feature/test-tags

# Create PR: feature/test-tags → dev
# Expected tags: pr-XX, commit-XXXXXX
```

### **Expected Result**:
- ✅ Docker build succeeds
- ✅ Valid tags generated (e.g., `pr-38`, `commit-2faa6df`)
- ✅ Images pushed to registry
- ✅ No "invalid reference format" errors

### **Verify in Registry**:
```bash
# Check generated images
yc container image list --registry-id crpqt390b8gk59ipqid8

# Should show images with proper tags like:
# - cvetochey-frontend:pr-38
# - cvetochey-frontend:commit-2faa6df
```

## 🔍 Troubleshooting

### **If Tags Still Invalid**:

1. **Check Tag Format**:
   ```bash
   # Valid Docker tag format
   ^[a-z0-9]+([._-][a-z0-9]+)*$
   ```

2. **Verify Metadata Action**:
   - Ensure no special characters in branch names
   - Check that prefixes don't start with `-` or `.`
   - Validate tag length < 128 characters

3. **Test Locally**:
   ```bash
   # Test tag generation
   docker tag myimage:latest cr.yandex/crpqt390b8gk59ipqid8/cvetochey-frontend:commit-abc123
   ```

### **Tag Naming Best Practices**:

| ✅ Valid | ❌ Invalid | Reason |
|----------|------------|--------|
| `commit-abc123` | `-abc123` | Can't start with `-` |
| `pr-38` | `pr_38` | Use `-` not `_` for readability |
| `v1.0.0` | `v1.0.0.` | Can't end with `.` |
| `latest` | `Latest` | Must be lowercase |

## 📚 References

- [Docker Tag Naming Conventions](https://docs.docker.com/engine/reference/commandline/tag/#extended-description)
- [Docker Metadata Action](https://github.com/docker/metadata-action#tags-input)
- [Yandex Container Registry](https://cloud.yandex.ru/docs/container-registry/)

---

🏷️ **Tag Generation Fixed!** Your GitHub Actions workflows will now generate valid Docker tags for all events (pushes, PRs, commits).


