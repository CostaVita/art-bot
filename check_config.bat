@echo off
echo ========================================
echo Проверка конфигурации Art Bot
echo ========================================
echo.

REM Проверяем переменные окружения
echo Проверка переменных окружения:
echo.

if "%BOT_TOKEN%"=="" (
    echo ❌ BOT_TOKEN не установлен
) else (
    echo ✅ BOT_TOKEN установлен: %BOT_TOKEN:~0,10%...
)

if "%BOT_USERNAME%"=="" (
    echo ❌ BOT_USERNAME не установлен
) else (
    echo ✅ BOT_USERNAME установлен: %BOT_USERNAME%
)

if "%ADMIN_ID%"=="" (
    echo ❌ ADMIN_ID не установлен
) else (
    echo ✅ ADMIN_ID установлен: %ADMIN_ID%
)

echo.
echo ========================================
echo Инструкции по настройке:
echo ========================================
echo.
echo 1. Получите токен бота у @BotFather:
echo    - Найдите @BotFather в Telegram
echo    - Отправьте /newbot
echo    - Следуйте инструкциям
echo.
echo 2. Получите ID администратора у @userinfobot:
echo    - Найдите @userinfobot в Telegram
echo    - Отправьте любое сообщение
echo    - Скопируйте полученный ID
echo.
echo 3. Установите переменные окружения:
echo    set BOT_TOKEN=ваш_токен_бота
echo    set BOT_USERNAME=имя_вашего_бота
echo    set ADMIN_ID=ваш_id_администратора
echo.
echo 4. Или отредактируйте application.properties
echo.
echo ========================================
pause 