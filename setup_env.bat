@echo off
echo ========================================
echo Настройка переменных окружения Art Bot
echo ========================================
echo.

set /p BOT_TOKEN="Введите токен бота (от @BotFather): "
set /p BOT_USERNAME="Введите username бота (без @): "
set /p ADMIN_ID="Введите ID администратора (от @userinfobot): "

echo.
echo ========================================
echo Проверка введенных данных:
echo ========================================
echo BOT_TOKEN: %BOT_TOKEN:~0,10%...
echo BOT_USERNAME: %BOT_USERNAME%
echo ADMIN_ID: %ADMIN_ID%
echo.

set /p CONFIRM="Все данные верны? (y/n): "
if /i "%CONFIRM%"=="y" (
    echo.
    echo Установка переменных окружения...
    setx BOT_TOKEN "%BOT_TOKEN%"
    setx BOT_USERNAME "%BOT_USERNAME%"
    setx ADMIN_ID "%ADMIN_ID%"
    echo.
    echo ✅ Переменные окружения установлены!
    echo.
    echo Для применения изменений перезапустите командную строку
    echo или запустите: start_bot.bat
) else (
    echo.
    echo ❌ Настройка отменена
)

echo.
pause 