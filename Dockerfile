# Этап сборки - используем официальный образ Maven с Java 17
FROM maven:3.8-openjdk-17 AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем pom.xml для кэширования зависимостей
COPY pom.xml .

# Скачиваем зависимости (этот слой будет кэшироваться)
RUN mvn dependency:go-offline -B

# Копируем исходный код
COPY src ./src

# Собираем приложение
RUN mvn clean package -DskipTests

# Этап выполнения - используем минимальный образ Java
FROM openjdk:17-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный JAR из этапа сборки
COPY --from=build /app/target/art-bot-1.0-SNAPSHOT.jar app.jar

# Устанавливаем переменные окружения для кодировки
ENV JAVA_OPTS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"

# Открываем порт (если нужен для мониторинга)
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 