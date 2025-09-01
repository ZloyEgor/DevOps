## Запуск нагрузочного тестирования 

Создаем configmap с тестом:
```bash
kubectl create configmap load-test --from-file=load-test.js
```

Запускаем под с нагрузочным тестом:
```bash
kubectl apply -f load-test.yaml
```

## Во время нагрузочного тестирования

Смотрим, как гоняется под нагрузочного тестирования:
```bash
kubectl logs -f k6-test
```

Смотрим на загрузку бэкенда в целом:
```bash
kubectl describe hpa backend
```

Смотрим на создаваемые подики:
```bash
kubectl get pods
```
## Остановка нагрузочного тестирования
```bash
kubectl delete pod k6-test
kubectl delete configmap load-test
```