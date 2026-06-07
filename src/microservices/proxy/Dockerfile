FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Копируем POM файлы
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем исходники и собираем
COPY src src
RUN mvn clean package -DskipTests

# Запуск
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Копируем JAR из этапа build
COPY --from=build /app/target/*.jar app.jar

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]