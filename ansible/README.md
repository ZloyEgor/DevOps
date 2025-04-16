# Работа с Ansible

## Начало работы

1. Создайте файл hosts.ini в папке ansible.
2. Скопируйте в него содержимое файла [example.hosts.ini](inventory/example.hosts.ini)
3. Вместо `PATH_TO_YOUR_SSH_KEY` укажите путь до ssh ключа. NB: публичный ключ должен быть добавлен на сервере в `~/.ssh/authorized_keys`
4. Запустите команду для проверки достижимости сервера из папки ansible:
    ```bash
   ansible all -m ping
   ```

## Деплой плейбуков

```bash
ansible-playbook playbooks/deploy-frontend.yml
```