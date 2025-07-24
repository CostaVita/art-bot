#!/bin/bash

echo "Starting Art Bot..."
echo

# Проверяем наличие переменных окружения
if [ -z "$BOT_TOKEN" ]; then
    echo "ERROR: BOT_TOKEN environment variable is not set!"
    echo "Please set BOT_TOKEN=your_bot_token_here"
    exit 1
fi

if [ -z "$ADMIN_ID" ]; then
    echo "ERROR: ADMIN_ID environment variable is not set!"
    echo "Please set ADMIN_ID=your_admin_id_here"
    exit 1
fi

if [ -z "$BOT_USERNAME" ]; then
    echo "ERROR: BOT_USERNAME environment variable is not set!"
    echo "Please set BOT_USERNAME=your_bot_username"
    exit 1
fi

echo "Bot configuration:"
echo "BOT_TOKEN: $BOT_TOKEN"
echo "BOT_USERNAME: $BOT_USERNAME"
echo "ADMIN_ID: $ADMIN_ID"
echo

echo "Building project..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "ERROR: Build failed!"
    exit 1
fi

echo
echo "Starting bot..."
java -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -jar target/art-bot-1.0-SNAPSHOT.jar 