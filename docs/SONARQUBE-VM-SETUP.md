# SonarQube VM Setup in Yandex Cloud

## Option 2: Dedicated Virtual Machine Setup

### Prerequisites
- Yandex Cloud CLI configured
- SSH key pair for VM access

### Step 1: Create VM with Terraform

```hcl
# sonarqube-vm.tf
resource "yandex_compute_instance" "sonarqube" {
  name        = "sonarqube-server"
  platform_id = "standard-v3"
  zone        = "ru-central1-a"

  resources {
    cores  = 4
    memory = 8
  }

  boot_disk {
    initialize_params {
      image_id = "fd8kdq6d0p8sij7h5qe3" # Ubuntu 22.04 LTS
      size     = 50
      type     = "network-ssd"
    }
  }

  network_interface {
    subnet_id = yandex_vpc_subnet.sonarqube_subnet.id
    nat       = true
  }

  metadata = {
    ssh-keys = "ubuntu:${file("~/.ssh/id_rsa.pub")}"
    user-data = file("cloud-init.yaml")
  }
}

resource "yandex_vpc_network" "sonarqube_network" {
  name = "sonarqube-network"
}

resource "yandex_vpc_subnet" "sonarqube_subnet" {
  name           = "sonarqube-subnet"
  zone           = "ru-central1-a"
  network_id     = yandex_vpc_network.sonarqube_network.id
  v4_cidr_blocks = ["10.2.0.0/16"]
}
```

### Step 2: Cloud-Init Configuration

```yaml
# cloud-init.yaml
#cloud-config
package_update: true
package_upgrade: true

packages:
  - docker.io
  - docker-compose
  - nginx

write_files:
  - path: /opt/sonarqube/docker-compose.yml
    content: |
      version: '3.8'
      services:
        sonarqube:
          image: sonarqube:10.7.0-community
          container_name: sonarqube
          depends_on:
            - db
          environment:
            SONAR_JDBC_URL: jdbc:postgresql://db:5432/sonar
            SONAR_JDBC_USERNAME: sonar
            SONAR_JDBC_PASSWORD: sonar
          volumes:
            - sonarqube_data:/opt/sonarqube/data
            - sonarqube_extensions:/opt/sonarqube/extensions
            - sonarqube_logs:/opt/sonarqube/logs
          ports:
            - "9000:9000"
          ulimits:
            nofile:
              soft: 65536
              hard: 65536
            nproc: 4096
          
        db:
          image: postgres:15-alpine
          container_name: sonarqube-db
          environment:
            POSTGRES_USER: sonar
            POSTGRES_PASSWORD: sonar
            POSTGRES_DB: sonar
          volumes:
            - postgresql_data:/var/lib/postgresql/data
          ports:
            - "5432:5432"

      volumes:
        sonarqube_data:
        sonarqube_extensions:
        sonarqube_logs:
        postgresql_data:

runcmd:
  - systemctl enable docker
  - systemctl start docker
  - usermod -aG docker ubuntu
  - sysctl -w vm.max_map_count=524288
  - echo 'vm.max_map_count=524288' >> /etc/sysctl.conf
  - cd /opt/sonarqube && docker-compose up -d
```

### Step 3: Deploy VM

```bash
# Deploy infrastructure
terraform init
terraform plan
terraform apply

# Get VM IP
VM_IP=$(terraform output -raw sonarqube_external_ip)
echo "SonarQube will be available at: http://$VM_IP:9000"
```

### Step 4: Configure Nginx Reverse Proxy

```bash
# SSH to VM
ssh ubuntu@$VM_IP

# Configure Nginx
sudo tee /etc/nginx/sites-available/sonarqube << EOF
server {
    listen 80;
    server_name sonar.cvetochey.ru;

    location / {
        proxy_pass http://localhost:9000;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF

# Enable site
sudo ln -s /etc/nginx/sites-available/sonarqube /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```
