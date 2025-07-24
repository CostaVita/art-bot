@echo off
chcp 65001 >nul
echo ========================================
echo Проверка кодировки системы
echo ========================================
echo.

echo Текущая кодировка консоли:
chcp
echo.

echo Проверка переменных окружения Java:
echo file.encoding: %JAVA_TOOL_OPTIONS%
echo.

echo Проверка доступности UTF-8:
java -Dfile.encoding=UTF-8 -cp target/classes org.example.util.EncodingUtils
echo.

echo ========================================
echo Рекомендации:
echo ========================================
echo 1. Убедитесь, что консоль использует UTF-8 (chcp 65001)
echo 2. Запускайте бота с параметрами кодировки:
echo    java -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -jar target/art-bot-1.0-SNAPSHOT.jar
echo 3. Используйте скрипт start_bot.bat для автоматической настройки
echo.

pause 