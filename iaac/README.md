# Лабораторная работа №1

## Резвертывание виртуальной машины в Yandex Cloud через Terraform

Переходим в директорию ```terraform```

```shell
cd ./terraform
```

Экспортируем в переменные окружения свежий токен доступа к облаку

```shell
export YC_TOKEN=$(yc iam create-token)
```

Запускаем Terraform пересоздать виртуальную машину с описанными в проекте конфигурациями

```shell
terraform apply -replace=yandex_compute_instance.devops-iac-vm
```

Во избежание ошибок в следствие пересоздания ВМ удаляем из ```known_hosts``` все данные о хосте ВМ

```shell
ssh-keygen -R 84.201.131.26
```

Проверяем подключение к ВМ

```shell
ssh itmo@84.201.131.26
```

Убеждаемся, что ```Docker``` в ней не установлен

```shell
docker ps
```

Возврашаемся из ВМ на хост-машину

```shell
exit
```

## Установка Docker в ВМ
Переходим в директорию ```./ansible```

```shell
cd ..
cd ./ansible
```

Запускам ansible-playbook на установку ```Docker``` в ВМ

```shell
ansible-playbook -i inventory.ini install-docker.yml
```

Подключаемся к ВМ

```shell
ssh itmo@84.201.131.26
```
Проверяем, что теперь ```Docker``` установлен

```shell
docker ps
```

Выходим из ВМ

```shell
exit
```

## Запуск контейнеров приложения в ВМ

Переходим в директорию ```iaac```

```shell
cd ..
```
Копируем ```docker-compose``` (заточенные на использование наших образов бекенда и фронтенда, загруженных в ```Docker Registry```) файлы в ВМ

```shell
scp -r application itmo@84.201.131.26:~/app
```

Подключаемся к БД

```shell
ssh itmo@84.201.131.26
```

Запускаем контейнеры

```shell
docker compose -f docker-compose.backend.yml -f docker-compose.frontend.yml up -d
```

В случае необходимости внесения изменения - пересоздаем и запускаем контейнеры

```shell
docker compose -f docker-compose.backend.yml -f docker-compose.frontend.yml up -d --force-recreate --build
```

Виртуальная машина активна, докер в нее установлен, контейнеры с приложением запущены и активны
