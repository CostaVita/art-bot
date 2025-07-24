@echo off
chcp 65001 >nul
echo ========================================
echo Тестирование конфигурации Art Bot
echo ========================================
echo.

echo Проверка application.properties:
echo.

REM Проверяем наличие файла
if exist "src\main\resources\application.properties" (
    echo ✅ application.properties найден
    
    REM Проверяем токен
    findstr "telegram.bot.token" src\main\resources\application.properties
    echo.
    
    REM Проверяем username
    findstr "telegram.bot.username" src\main\resources\application.properties
    echo.
    
    REM Проверяем admin ID
    findstr "bot.admin.id" src\main\resources\application.properties
    echo.
    
) else (
    echo ❌ application.properties не найден
)

echo.
echo ========================================
echo Тестирование сборки:
echo ========================================
echo.

echo Компиляция проекта...
call mvn clean compile -q

if %ERRORLEVEL% equ 0 (
    echo ✅ Компиляция успешна
) else (
    echo ❌ Ошибка компиляции
    pause
    exit /b 1
)

echo.
echo ========================================
echo Тестирование конфигурации:
echo ========================================
echo.

echo Запуск тестов конфигурации...
call mvn test -Dtest=AdminBotTest -q

if %ERRORLEVEL% equ 0 (
    echo ✅ Тесты конфигурации прошли успешно
) else (
    echo ❌ Ошибка в тестах конфигурации
)

echo.
echo ========================================
echo Готово к запуску!
echo ========================================
echo.
echo Для запуска бота используйте: start_bot.bat
echo.

pause 