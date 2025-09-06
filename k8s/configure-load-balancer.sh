#!/bin/bash

echo "🔧 Configuring Yandex Cloud Load Balancer for SonarQube access..."

# Get the load balancer ID (you may need to adjust this)
LOAD_BALANCER_ID=$(yc load-balancer network-load-balancer list --format json | jq -r '.[0].id')

if [ -z "$LOAD_BALANCER_ID" ] || [ "$LOAD_BALANCER_ID" = "null" ]; then
    echo "❌ No load balancer found. Please check your Yandex Cloud setup."
    exit 1
fi

echo "📋 Found load balancer: $LOAD_BALANCER_ID"

# Add SonarQube listener (port 30900)
echo "🔧 Adding SonarQube listener (port 30900)..."
yc load-balancer network-load-balancer add-listener \
    --id $LOAD_BALANCER_ID \
    --listener name=sonarqube,port=30900,target-port=30900,protocol=tcp \
    --target-group-id=$(yc load-balancer network-load-balancer get $LOAD_BALANCER_ID --format json | jq -r '.attached_target_groups[0].target_group_id')

if [ $? -eq 0 ]; then
    echo "✅ SonarQube listener added successfully!"
    echo "🌐 SonarQube should now be accessible at: http://89.169.138.79:30900"
else
    echo "❌ Failed to add listener. You may need to do this manually in Yandex Cloud Console."
fi

echo ""
echo "📋 Current load balancer configuration:"
yc load-balancer network-load-balancer get $LOAD_BALANCER_ID
