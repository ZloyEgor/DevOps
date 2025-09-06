#!/bin/bash

echo "🔧 Setting up CI RBAC permissions for SonarQube access..."

# Apply the RBAC configuration
kubectl apply -f ci-rbac.yaml

# Verify the configuration
echo "✅ Checking RBAC setup..."
kubectl get role ci-role -n sonarqube
kubectl get rolebinding ci-role-binding -n sonarqube
kubectl get clusterrole ci-cluster-role
kubectl get clusterrolebinding ci-cluster-role-binding

echo "🎯 CI service account should now have access to SonarQube namespace!"
