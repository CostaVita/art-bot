# 🚀 Развертывание Telegram Bot

## Требования

- Docker
- Docker Compose
- Linux сервер с доступом к интернету

## Быстрое развертывание

### 1. Клонируйте репозиторий
```bash
git clone <your-repo-url>
cd art-bot
```

### 2. Настройте переменные окружения
Отредактируйте `docker-compose.yml`:
```yaml
environment:
  - TELEGRAM_BOT_TOKEN=your_bot_token
  - TELEGRAM_BOT_USERNAME=your_bot_username
  - BOT_ADMIN_IDS=id1,id2,id3
```

### 3. Запустите развертывание
```bash
chmod +x deploy.sh
./deploy.sh
```

## Ручное развертывание

### Сборка образа
```bash
docker build -t art-bot .
```

### Запуск контейнера
```bash
docker run -d \
  --name art-bot \
  --restart unless-stopped \
  -p 8080:8080 \
  -e TELEGRAM_BOT_TOKEN=your_token \
  -e TELEGRAM_BOT_USERNAME=your_username \
  -e BOT_ADMIN_IDS=id1,id2,id3 \
  art-bot
```

## Управление

### Просмотр логов
```bash
docker-compose logs -f
```

### Остановка
```bash
docker-compose down
```

### Перезапуск
```bash
docker-compose restart
```

### Обновление
```bash
git pull
./deploy.sh
```

## Мониторинг

### Проверка статуса
```bash
docker-compose ps
```

### Использование ресурсов
```bash
docker stats art-bot
```

### Проверка здоровья
```bash
curl http://localhost:8080/actuator/health
```

## Безопасность

### Переменные окружения
- Не коммитьте токены в Git
- Используйте `.env` файлы для продакшена
- Регулярно обновляйте токены

### Сеть
- Откройте только порт 8080 (если нужен)
- Используйте reverse proxy (nginx) для HTTPS
- Настройте firewall

## Troubleshooting

### Проблемы с кодировкой
```bash
docker exec -it art-bot locale
```

### Проблемы с памятью
```bash
docker run -e JAVA_OPTS="-Xmx512m -Xms256m" ...
```

### Проблемы с сетью
```bash
docker network ls
docker network inspect bot-network
``` 