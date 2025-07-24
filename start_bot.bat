@echo off
echo Starting Art Bot...
echo.

REM Проверяем наличие переменных окружения
if "%BOT_TOKEN%"=="" (
    echo ERROR: BOT_TOKEN environment variable is not set!
    echo Please set BOT_TOKEN=your_bot_token_here
    pause
    exit /b 1
)

if "%ADMIN_ID%"=="" (
    echo ERROR: ADMIN_ID environment variable is not set!
    echo Please set ADMIN_ID=your_admin_id_here
    pause
    exit /b 1
)

if "%BOT_USERNAME%"=="" (
    echo ERROR: BOT_USERNAME environment variable is not set!
    echo Please set BOT_USERNAME=your_bot_username
    pause
    exit /b 1
)

echo Bot configuration:
echo BOT_TOKEN: %BOT_TOKEN%
echo BOT_USERNAME: %BOT_USERNAME%
echo ADMIN_ID: %ADMIN_ID%
echo.

echo Building project...
call mvn clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo Starting bot...
chcp 65001 >nul
java -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Dconsole.encoding=UTF-8 -jar target/art-bot-1.0-SNAPSHOT.jar

pause 