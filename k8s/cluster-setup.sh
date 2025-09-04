#!/bin/bash

# Create Kubernetes cluster in Yandex Cloud
echo "Creating Kubernetes cluster..."

# Create cluster
yc managed-kubernetes cluster create \
  --name cvetochey-cluster \
  --network-name cvetochey-net \
  --zone ru-central1-a \
  --subnet-name cvetochey-subnet \
  --public-ip \
  --release-channel regular \
  --version 1.30 \
  --cluster-ipv4-range 10.96.0.0/16 \
  --service-ipv4-range 10.112.0.0/16 \
  --service-account-id ajecso143s72sdd36vnd \
  --node-service-account-id aje5ucsjf8d3s74tejva

# Wait for cluster to be ready
echo "Waiting for cluster to be ready..."
sleep 60

# Create node group with adequate resources
yc managed-kubernetes node-group create worker-nodes \
  --cluster-name cvetochey-cluster \
  --platform standard-v3 \
  --cores 4 \
  --memory 8GB \
  --disk-type network-hdd \
  --disk-size 64GB \
  --fixed-size 3 \
  --network-interface subnets=e9bjq4eom3qadbhb9i87,ipv4-address=auto

echo "Cluster setup complete!"

# Get cluster credentials
echo "Getting cluster credentials..."
yc managed-kubernetes cluster get-credentials cvetochey-cluster --external

echo "Setup complete! You can now use kubectl to manage your cluster."
