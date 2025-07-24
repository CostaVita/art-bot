# Используем OpenJDK 17 как базовый образ
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем Maven файлы для кэширования зависимостей
COPY pom.xml .
COPY src ./src

# Устанавливаем Maven
RUN apt-get update && apt-get install -y maven

# Собираем приложение
RUN mvn clean package -DskipTests

# Удаляем Maven после сборки для уменьшения размера образа
RUN apt-get remove -y maven && apt-get autoremove -y && apt-get clean

# Копируем собранный JAR файл
COPY target/art-bot-1.0-SNAPSHOT.jar app.jar

# Устанавливаем переменные окружения для кодировки
ENV JAVA_OPTS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"

# Открываем порт (если нужен для мониторинга)
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 