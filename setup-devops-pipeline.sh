#!/bin/bash

# Complete DevOps Pipeline Setup Script
# This script helps you set up the entire DevOps pipeline for CvetOchey project

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================${NC}"
}

print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_step() {
    echo -e "${YELLOW}[STEP]${NC} $1"
}

# Welcome message
clear
print_header "CvetOchey DevOps Pipeline Setup"
echo ""
echo "This script will help you set up:"
echo "• SonarQube for code quality analysis"
echo "• GitHub Actions CI/CD pipelines"
echo "• Telegram bot for notifications"
echo "• Continuous delivery to Yandex Cloud K8s"
echo ""
read -p "Press Enter to continue..."

# Step 1: Prerequisites check
print_header "Step 1: Prerequisites Check"

# Check kubectl
if ! command -v kubectl &> /dev/null; then
    print_error "kubectl is not installed. Please install kubectl first."
    exit 1
fi
print_status "✓ kubectl is installed"

# Check yc CLI
if ! command -v yc &> /dev/null; then
    print_warning "yc CLI is not installed. Installing..."
    curl -sSL https://storage.yandexcloud.net/yandexcloud-yc/install.sh | bash
    export PATH="$HOME/yandex-cloud/bin:$PATH"
fi
print_status "✓ yc CLI is available"

# Check cluster connection
if ! kubectl cluster-info &> /dev/null; then
    print_error "Cannot connect to Kubernetes cluster."
    print_warning "Please run: yc managed-kubernetes cluster get-credentials cvetochey-cluster --external"
    exit 1
fi
print_status "✓ Connected to Kubernetes cluster"

# Step 2: Deploy SonarQube
print_header "Step 2: Deploy SonarQube"
print_step "Deploying SonarQube to Kubernetes..."

cd k8s
if [ -f "sonarqube.yaml" ]; then
    kubectl apply -f sonarqube.yaml
    print_status "SonarQube deployment initiated"
    
    print_step "Waiting for SonarQube to be ready..."
    kubectl wait --for=condition=ready pod -l app=postgresql -n sonarqube --timeout=300s
    kubectl wait --for=condition=ready pod -l app=sonarqube -n sonarqube --timeout=600s
    
    print_status "✓ SonarQube is ready!"
else
    print_error "sonarqube.yaml not found!"
    exit 1
fi

# Step 3: Get SonarQube access information
print_header "Step 3: SonarQube Access Information"

# Check if ingress exists
if kubectl get ingress sonarqube-ingress -n sonarqube &> /dev/null; then
    SONAR_URL=$(kubectl get ingress sonarqube-ingress -n sonarqube -o jsonpath='{.spec.rules[0].host}')
    print_status "SonarQube URL: http://$SONAR_URL"
else
    print_warning "Ingress not configured. Use port-forward to access SonarQube:"
    print_warning "kubectl port-forward svc/sonarqube 9000:9000 -n sonarqube"
    SONAR_URL="localhost:9000"
fi

echo ""
echo "📋 SonarQube Setup:"
echo "• URL: http://$SONAR_URL"
echo "• Default credentials: admin/admin"
echo "• Change password on first login!"
echo ""

# Step 4: GitHub Secrets Configuration
print_header "Step 4: GitHub Secrets Configuration"
echo ""
echo "You need to add the following secrets to your GitHub repository:"
echo "Go to: Settings > Secrets and variables > Actions"
echo ""
echo "🔐 Required Secrets:"
echo "================================"
echo "SONAR_TOKEN=<generate-from-sonarqube>"
echo "SONAR_HOST_URL=http://$SONAR_URL"
echo "TELEGRAM_BOT_TOKEN=<create-via-botfather>"
echo "TELEGRAM_CHAT_ID=<your-telegram-chat-id>"
echo ""
echo "Optional (if not already configured):"
echo "YC_SERVICE_ACCOUNT_KEY=<base64-encoded-key>"
echo "YC_CLOUD_ID=<your-cloud-id>"
echo "YC_FOLDER_ID=<your-folder-id>"
echo "CODECOV_TOKEN=<optional-codecov-token>"
echo ""

# Step 5: Telegram Bot Setup Guide
print_header "Step 5: Telegram Bot Setup Guide"
echo ""
echo "🤖 Create Telegram Bot:"
echo "1. Message @BotFather on Telegram"
echo "2. Send /newbot"
echo "3. Follow instructions to create your bot"
echo "4. Save the bot token"
echo ""
echo "📱 Get Chat ID:"
echo "1. Add your bot to a group or start a chat"
echo "2. Send a message to the bot"
echo "3. Visit: https://api.telegram.org/bot<BOT_TOKEN>/getUpdates"
echo "4. Find 'chat':{'id': YOUR_CHAT_ID} in the response"
echo ""

# Step 6: SonarQube Project Setup
print_header "Step 6: SonarQube Project Setup"
echo ""
echo "🏗️ Create Projects in SonarQube:"
echo "1. Access SonarQube at http://$SONAR_URL"
echo "2. Login with admin/admin (change password!)"
echo "3. Create project: 'cvetochey-backend'"
echo "4. Create project: 'cvetochey-frontend'"
echo "5. Generate tokens for both projects"
echo "6. Add tokens to GitHub secrets"
echo ""

# Step 7: Verify Setup
print_header "Step 7: Verification"
echo ""
echo "📊 Current Status:"
kubectl get pods -n sonarqube
echo ""
kubectl get svc -n sonarqube
echo ""

# Step 8: Next Steps
print_header "Step 8: Next Steps"
echo ""
echo "✅ What's been set up:"
echo "• SonarQube deployed to Kubernetes"
echo "• CI/CD workflows configured"
echo "• Telegram notification workflows ready"
echo "• Continuous delivery pipeline ready"
echo ""
echo "🔧 Manual steps required:"
echo "1. Configure SonarQube projects and generate tokens"
echo "2. Create Telegram bot and get chat ID"
echo "3. Add all secrets to GitHub repository"
echo "4. Test the pipeline by making a commit"
echo ""
echo "📚 Documentation:"
echo "• Full setup guide: docs/DEVOPS-PIPELINE-SETUP.md"
echo "• SonarQube access: http://$SONAR_URL"
echo ""

print_status "Setup completed! 🎉"
print_warning "Don't forget to complete the manual steps above!"

# Optional: Open SonarQube in browser (macOS)
if [[ "$OSTYPE" == "darwin"* ]]; then
    read -p "Open SonarQube in browser? (y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        if command -v open &> /dev/null; then
            open "http://$SONAR_URL"
        fi
    fi
fi
