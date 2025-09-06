# SonarQube Setup Summary

## ✅ SonarQube Configuration Complete

### 🏗️ Infrastructure
- **Status**: ✅ Running in Kubernetes
- **Version**: SonarQube 9.9.6 Community
- **Database**: PostgreSQL 15
- **Namespace**: `sonarqube`
- **Access**: Port-forward active on `http://localhost:9000`

### 🔐 Authentication
- **Admin User**: `admin`
- **Password**: `12345`
- **Status**: ✅ Password changed from default

### 📊 Projects Created
1. **Backend Projects**
   - **Master Branch**: `cvetochey-backend` - `CvetOchey Backend`
   - **Dev Branch**: `cvetochey-backend-dev` - `CvetOchey Backend (Dev Branch)`
   - **Token**: `squ_7bc3252a9327322f87bd9fad092540e3b192f293`

2. **Frontend Projects**
   - **Master Branch**: `cvetochey-frontend` - `CvetOchey Frontend`
   - **Dev Branch**: `cvetochey-frontend-dev` - `CvetOchey Frontend (Dev Branch)`
   - **Token**: `squ_d0e89ffa52d9c1d264f2eefc7206d878d3b90086`

### 🎯 Quality Gate
- **Name**: `CvetOchey-80-Coverage`
- **Coverage Requirement**: 80% minimum
- **Status**: ✅ Set as default quality gate
- **Condition**: Coverage < 80% = FAIL

### 🔑 GitHub Actions Token
- **Token**: `squ_ab08334243b8ac91442f39c7f7a2fa1cde336f8b`
- **Name**: `github-actions-token`
- **Usage**: For CI/CD pipeline integration

## 📋 Required GitHub Secrets

Add these secrets to your GitHub repository (`Settings > Secrets and variables > Actions`):

```bash
# SonarQube Configuration
SONAR_TOKEN=squ_ab08334243b8ac91442f39c7f7a2fa1cde336f8b
SONAR_HOST_URL=http://sonar.cvetochey.ru

# Alternative for testing (if ingress not configured)
# SONAR_HOST_URL=http://89.169.138.79:9000
```

## 🚀 Next Steps

### 1. Configure DNS (Optional)
If you want to use the ingress `sonar.cvetochey.ru`, configure your DNS:
```bash
# Add A record: sonar.cvetochey.ru -> 89.169.138.79
```

### 2. Test SonarQube Integration
```bash
# Test backend analysis
cd backend
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=cvetochey-backend \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=squ_ab08334243b8ac91442f39c7f7a2fa1cde336f8b

# Test frontend analysis
cd frontend
npm run test -- --coverage --watchAll=false
npx sonar-scanner \
  -Dsonar.projectKey=cvetochey-frontend \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=squ_ab08334243b8ac91442f39c7f7a2fa1cde336f8b
```

### 3. Verify Quality Gate
- Projects will fail if coverage < 80%
- Check results at: http://localhost:9000

## 🔧 Kubernetes Commands

```bash
# Check SonarQube status
kubectl get pods -n sonarqube

# View logs
kubectl logs -n sonarqube -l app=sonarqube

# Port forward (if needed)
kubectl port-forward -n sonarqube svc/sonarqube 9000:9000

# Access SonarQube shell
kubectl exec -it -n sonarqube deployment/sonarqube -- /bin/bash
```

## 🎯 Quality Gate Details

The custom quality gate `CvetOchey-80-Coverage` enforces:
- **Coverage**: Must be ≥ 80%
- **Applies to**: Both backend and frontend projects
- **Failure**: Pipeline will fail if coverage is below threshold

## 📊 SonarQube URLs

- **Dashboard**: http://localhost:9000
- **Backend Master**: http://localhost:9000/dashboard?id=cvetochey-backend
- **Backend Dev**: http://localhost:9000/dashboard?id=cvetochey-backend-dev
- **Frontend Master**: http://localhost:9000/dashboard?id=cvetochey-frontend
- **Frontend Dev**: http://localhost:9000/dashboard?id=cvetochey-frontend-dev
- **Quality Gates**: http://localhost:9000/quality_gates

## 🌿 Branch Analysis Configuration

The CI/CD pipeline now supports branch-specific analysis:

### Master Branch (`master`)
- Uses projects: `cvetochey-backend` and `cvetochey-frontend`
- Represents production-ready code
- Full quality gate enforcement

### Dev Branch (`dev`)
- Uses projects: `cvetochey-backend-dev` and `cvetochey-frontend-dev`
- Tracks development progress
- Same 80% coverage requirement
- Separate quality metrics from master

### How It Works
1. **Automatic Detection**: CI detects branch name automatically
2. **Project Selection**: Routes analysis to correct SonarQube project
3. **Version Tagging**: Uses branch-specific version tags
4. **Quality Gates**: Same 80% coverage rule applies to both branches

---

**Status**: ✅ SonarQube fully configured and ready for CI/CD integration!
