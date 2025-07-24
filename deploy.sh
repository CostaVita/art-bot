#!/bin/bash

echo "🚀 Deploying Telegram Bot..."

# Останавливаем существующий контейнер
echo "📦 Stopping existing container..."
docker-compose down

# Удаляем старый образ (опционально)
echo "🧹 Cleaning old images..."
docker system prune -f

# Собираем новый образ
echo "🔨 Building new image..."
docker-compose build --no-cache

# Запускаем контейнер
echo "▶️ Starting container..."
docker-compose up -d

# Проверяем статус
echo "📊 Checking status..."
docker-compose ps

# Показываем логи
echo "📋 Recent logs:"
docker-compose logs --tail=20

echo "✅ Deployment completed!"
echo "🔍 To view logs: docker-compose logs -f"
echo "🛑 To stop: docker-compose down" 