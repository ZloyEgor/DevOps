#!/bin/bash

# SonarQube Setup Script for Yandex Cloud Kubernetes
# This script deploys SonarQube with PostgreSQL database to your Kubernetes cluster

set -e

echo "🚀 Starting SonarQube deployment to Yandex Cloud Kubernetes..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if kubectl is installed and configured
if ! command -v kubectl &> /dev/null; then
    print_error "kubectl is not installed. Please install kubectl first."
    exit 1
fi

# Check if we can connect to the cluster
if ! kubectl cluster-info &> /dev/null; then
    print_error "Cannot connect to Kubernetes cluster. Please configure kubectl first."
    exit 1
fi

print_status "Connected to Kubernetes cluster successfully"

# Apply SonarQube configuration
print_status "Deploying SonarQube to Kubernetes..."
kubectl apply -f sonarqube.yaml

# Wait for PostgreSQL to be ready
print_status "Waiting for PostgreSQL to be ready..."
kubectl wait --for=condition=ready pod -l app=postgresql -n sonarqube --timeout=300s

# Wait for SonarQube to be ready
print_status "Waiting for SonarQube to be ready (this may take several minutes)..."
kubectl wait --for=condition=ready pod -l app=sonarqube -n sonarqube --timeout=600s

# Get SonarQube service information
print_status "Getting SonarQube service information..."
kubectl get svc -n sonarqube

# Check if ingress is configured
if kubectl get ingress sonarqube-ingress -n sonarqube &> /dev/null; then
    SONAR_URL=$(kubectl get ingress sonarqube-ingress -n sonarqube -o jsonpath='{.spec.rules[0].host}')
    print_status "SonarQube will be available at: http://$SONAR_URL"
else
    print_warning "Ingress not found. You can access SonarQube via port-forward:"
    print_warning "kubectl port-forward svc/sonarqube 9000:9000 -n sonarqube"
fi

# Display initial setup information
echo ""
print_status "SonarQube deployment completed successfully!"
echo ""
echo "📋 Initial Setup Information:"
echo "================================"
echo "• Default admin credentials: admin/admin"
echo "• Change the default password on first login"
echo "• Create projects for 'cvetochey-backend' and 'cvetochey-frontend'"
echo "• Generate project tokens for CI/CD integration"
echo ""
echo "🔧 Required GitHub Secrets:"
echo "================================"
echo "• SONAR_TOKEN: Generate from SonarQube > My Account > Security > Generate Tokens"
echo "• SONAR_HOST_URL: http://$SONAR_URL (or your SonarQube URL)"
echo "• TELEGRAM_BOT_TOKEN: Create bot via @BotFather on Telegram"
echo "• TELEGRAM_CHAT_ID: Your Telegram chat ID for notifications"
echo ""
echo "📚 Next Steps:"
echo "================================"
echo "1. Access SonarQube and change default password"
echo "2. Create projects for backend and frontend"
echo "3. Generate project tokens"
echo "4. Add secrets to GitHub repository"
echo "5. Configure quality gates (80% coverage requirement is already set)"
echo ""

# Show pod status
print_status "Current pod status:"
kubectl get pods -n sonarqube

print_status "Setup completed! 🎉"
