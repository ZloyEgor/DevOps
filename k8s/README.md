Создание кластера:
```bash
yc managed-kubernetes cluster create \
  --name cvetochey-k8s-cluster \
  --folder-id b1gs0cg1voiht42pp513 \
  --network-name cvetochey-net \
  --subnet-name cvetochey-subnet \
  --zone ru-central1-a \
  --public-ip \
  --service-account-name k8s-admin \
  --node-service-account-name k8s-nodes \
  --release-channel regular \
  --version 1.28
```

Создаем группу нод внутри кластера:
```
yc managed-kubernetes node-group create \
  --name my-node-group \
  --cluster-name cvetochey-k8s-cluster \
  --folder-id b1gs0cg1voiht42pp513 \
  --platform-id standard-v2 \
  --cores 2 \
  --memory 4 \
  --disk-size 50 \
  --network-interface subnets=cvetochey-subnet,ipv4-address=nat \
  --fixed-size 1
```

Мониторим подики:
```bash
kubectl get pods -w
```

Команда для перезапуска пода:
```bash
kubectl rollout restart deployment backend
```

Порт-форвард для дебага бэкенда:
```bash
kubectl port-forward svc/backend 8080:8080
```

### Горизонтальное масштабирование бэка
Заводим metrics-server:

```bash
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
```
Настраиваем горизонтальное масштабирование:

```bash
kubectl autoscale deployment backend \
  --cpu-percent=15 \
  --min=1 \
  --max=5 \
  --namespace=default
```

Проверяем автоскейлинг (HPA):
```bash
kubectl get hpa
```
Выводит что-то типа такого:
```
NAME      REFERENCE            TARGETS       MINPODS   MAXPODS   REPLICAS   AGE
backend   Deployment/backend   cpu: 2%/15%   1         5         1          65s
```


## Prometheus
Проверка, что поды мониторинга работают:
```bash
kubectl get pods -n monitoring
```