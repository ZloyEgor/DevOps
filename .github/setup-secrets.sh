#!/bin/bash

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${GREEN}🔐 GitHub Actions Secrets Setup for CvetOchey${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

# Check if yc CLI is installed
if ! command -v yc &> /dev/null; then
    echo -e "${RED}❌ Yandex Cloud CLI (yc) is not installed${NC}"
    echo -e "${YELLOW}💡 Install with: curl -sSL https://storage.yandexcloud.net/yandexcloud-yc/install.sh | bash${NC}"
    exit 1
fi

# Check if yc is configured
if ! yc config list &> /dev/null; then
    echo -e "${RED}❌ Yandex Cloud CLI is not configured${NC}"
    echo -e "${YELLOW}💡 Run: yc init${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Yandex Cloud CLI is configured${NC}"

# Get current configuration
CLOUD_ID=$(yc config get cloud-id)
FOLDER_ID=$(yc config get folder-id)

echo -e "${BLUE}📋 Current Yandex Cloud Configuration:${NC}"
echo -e "  Cloud ID: ${YELLOW}${CLOUD_ID}${NC}"
echo -e "  Folder ID: ${YELLOW}${FOLDER_ID}${NC}"

# Check if service account exists
SA_NAME="github-actions-sa"
echo -e "${YELLOW}🔍 Checking for service account: ${SA_NAME}${NC}"

if yc iam service-account get $SA_NAME &> /dev/null; then
    echo -e "${GREEN}✅ Service account ${SA_NAME} already exists${NC}"
    SA_ID=$(yc iam service-account get $SA_NAME --format json | jq -r '.id')
else
    echo -e "${YELLOW}📝 Creating service account: ${SA_NAME}${NC}"
    yc iam service-account create --name $SA_NAME --description "Service account for GitHub Actions CI/CD"
    SA_ID=$(yc iam service-account get $SA_NAME --format json | jq -r '.id')
    echo -e "${GREEN}✅ Created service account with ID: ${SA_ID}${NC}"
fi

# Assign required roles
echo -e "${YELLOW}🔐 Assigning required roles to service account...${NC}"

# Container Registry roles
yc resource-manager folder add-access-binding $FOLDER_ID \
    --role container-registry.images.pusher \
    --subject serviceAccount:$SA_ID \
    --quiet || echo "Role already assigned"

yc resource-manager folder add-access-binding $FOLDER_ID \
    --role container-registry.images.puller \
    --subject serviceAccount:$SA_ID \
    --quiet || echo "Role already assigned"

# Kubernetes roles
yc resource-manager folder add-access-binding $FOLDER_ID \
    --role k8s.cluster-api.cluster-admin \
    --subject serviceAccount:$SA_ID \
    --quiet || echo "Role already assigned"

yc resource-manager folder add-access-binding $FOLDER_ID \
    --role k8s.clusters.agent \
    --subject serviceAccount:$SA_ID \
    --quiet || echo "Role already assigned"

echo -e "${GREEN}✅ Roles assigned successfully${NC}"

# Create service account key
KEY_FILE="github-actions-key.json"
echo -e "${YELLOW}🔑 Creating service account key...${NC}"

if [ -f "$KEY_FILE" ]; then
    echo -e "${YELLOW}⚠️  Key file already exists. Overwriting...${NC}"
    rm "$KEY_FILE"
fi

yc iam key create --service-account-id $SA_ID --output $KEY_FILE

if [ ! -f "$KEY_FILE" ]; then
    echo -e "${RED}❌ Failed to create service account key${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Service account key created: ${KEY_FILE}${NC}"

# Base64 encode the key
echo -e "${YELLOW}🔄 Encoding service account key...${NC}"
KEY_BASE64=$(base64 -i $KEY_FILE | tr -d '\n')

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}🎯 GitHub Secrets Configuration${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

echo -e "${YELLOW}📝 Add these secrets to your GitHub repository:${NC}"
echo -e "${BLUE}   Settings → Secrets and variables → Actions → New repository secret${NC}"
echo ""

echo -e "${GREEN}Secret Name: ${YELLOW}YC_SERVICE_ACCOUNT_KEY${NC}"
echo -e "${GREEN}Secret Value:${NC}"
echo "${KEY_BASE64}"
echo ""

echo -e "${GREEN}Secret Name: ${YELLOW}YC_CLOUD_ID${NC}"
echo -e "${GREEN}Secret Value:${NC}"
echo "${CLOUD_ID}"
echo ""

echo -e "${GREEN}Secret Name: ${YELLOW}YC_FOLDER_ID${NC}"
echo -e "${GREEN}Secret Value:${NC}"
echo "${FOLDER_ID}"
echo ""

# Save to file for reference
SECRETS_FILE="github-secrets.txt"
cat > $SECRETS_FILE << EOF
# GitHub Secrets for CvetOchey CI/CD
# Add these to your GitHub repository: Settings → Secrets and variables → Actions

YC_SERVICE_ACCOUNT_KEY=${KEY_BASE64}

YC_CLOUD_ID=${CLOUD_ID}

YC_FOLDER_ID=${FOLDER_ID}

# Service Account Details:
# Name: ${SA_NAME}
# ID: ${SA_ID}
# Key File: ${KEY_FILE}
# Created: $(date)
EOF

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}💾 Secrets saved to: ${SECRETS_FILE}${NC}"
echo -e "${RED}⚠️  IMPORTANT: Keep ${KEY_FILE} and ${SECRETS_FILE} secure!${NC}"
echo -e "${RED}⚠️  DO NOT commit these files to Git!${NC}"

# Add to .gitignore if it exists
if [ -f ".gitignore" ]; then
    if ! grep -q "$KEY_FILE" .gitignore; then
        echo "$KEY_FILE" >> .gitignore
        echo "$SECRETS_FILE" >> .gitignore
        echo -e "${GREEN}✅ Added secrets files to .gitignore${NC}"
    fi
fi

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}🚀 Next Steps:${NC}"
echo -e "${YELLOW}1.${NC} Add the secrets to your GitHub repository"
echo -e "${YELLOW}2.${NC} Push changes to trigger CI/CD pipeline"
echo -e "${YELLOW}3.${NC} Monitor deployments in GitHub Actions tab"
echo -e "${YELLOW}4.${NC} Check Grafana dashboard for deployment impact"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

# Test the setup
echo -e "${YELLOW}🧪 Testing service account permissions...${NC}"

# Test container registry access
if yc container registry list &> /dev/null; then
    echo -e "${GREEN}✅ Container registry access: OK${NC}"
else
    echo -e "${RED}❌ Container registry access: FAILED${NC}"
fi

# Test Kubernetes access
if yc managed-kubernetes cluster list &> /dev/null; then
    echo -e "${GREEN}✅ Kubernetes access: OK${NC}"
else
    echo -e "${RED}❌ Kubernetes access: FAILED${NC}"
fi

echo -e "${GREEN}🎉 GitHub Actions secrets setup completed!${NC}"



