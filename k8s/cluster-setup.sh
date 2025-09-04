#!/bin/bash

# Create Kubernetes cluster in Yandex Cloud
echo "Creating Kubernetes cluster..."

# Create cluster
yc managed-kubernetes cluster create \
  --name cvetochey-cluster \
  --network-name default \
  --zone ru-central1-a \
  --subnet-name default-ru-central1-a \
  --public-ip \
  --release-channel regular \
  --version 1.28 \
  --cluster-ipv4-range 10.96.0.0/16 \
  --service-ipv4-range 10.112.0.0/16

# Wait for cluster to be ready
echo "Waiting for cluster to be ready..."
sleep 60

# Create node group
yc managed-kubernetes node-group create \
  --cluster-name cvetochey-cluster \
  --name worker-nodes \
  --platform standard-v3 \
  --cores 2 \
  --memory 4GB \
  --disk-type network-hdd \
  --disk-size 32GB \
  --fixed-size 2 \
  --location zone=ru-central1-a \
  --public-ip

echo "Cluster setup complete!"

# Get cluster credentials
echo "Getting cluster credentials..."
yc managed-kubernetes cluster get-credentials cvetochey-cluster --external

echo "Setup complete! You can now use kubectl to manage your cluster."
