@echo off
chcp 65001 >nul
echo ========================================
echo Статус Art Bot
echo ========================================
echo.

echo Проверка конфигурации:
echo.

REM Проверяем application.properties
if exist "src\main\resources\application.properties" (
    echo ✅ application.properties найден
    
    echo.
    echo Текущие настройки:
    echo.
    
    REM Показываем токен (частично)
    for /f "tokens=2 delims==" %%i in ('findstr "telegram.bot.token" src\main\resources\application.properties') do (
        set TOKEN=%%i
        set TOKEN=!TOKEN: =!
        echo Токен: !TOKEN:~0,10!...
    )
    
    REM Показываем username
    for /f "tokens=2 delims==" %%i in ('findstr "telegram.bot.username" src\main\resources\application.properties') do (
        set USERNAME=%%i
        set USERNAME=!USERNAME: =!
        echo Username: !USERNAME!
    )
    
    REM Показываем admin ID
    for /f "tokens=2 delims==" %%i in ('findstr "bot.admin.id" src\main\resources\application.properties') do (
        set ADMIN_ID=%%i
        set ADMIN_ID=!ADMIN_ID: =!
        echo Admin ID: !ADMIN_ID!
    )
    
) else (
    echo ❌ application.properties не найден
)

echo.
echo Проверка логов:
echo.

if exist "bot.log" (
    echo ✅ Лог файл найден
    echo Последние записи:
    echo.
    powershell "Get-Content bot.log -Tail 5"
) else (
    echo ❌ Лог файл не найден
)

echo.
echo ========================================
echo Инструкции:
echo ========================================
echo 1. Убедитесь, что администратор написал боту /start
echo 2. Проверьте, что токен и username корректны
echo 3. Запустите бота: run_bot.bat
echo.

pause 