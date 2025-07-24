@echo off
chcp 65001 >nul
echo ========================================
echo Быстрый запуск Art Bot
echo ========================================
echo.

echo Проверка конфигурации...
if exist "src\main\resources\application.properties" (
    echo ✅ application.properties найден
) else (
    echo ❌ application.properties не найден
    pause
    exit /b 1
)

echo.
echo Сборка проекта...
call mvn clean compile -q

if %ERRORLEVEL% neq 0 (
    echo ❌ Ошибка сборки
    pause
    exit /b 1
)

echo ✅ Сборка успешна
echo.
echo Запуск бота...
echo.

java -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -cp target/classes org.example.Main

pause 