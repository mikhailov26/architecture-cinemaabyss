FROM --platform=$BUILDPLATFORM openjdk:21-jdk-slim as build

WORKDIR /app

COPY . .

RUN ./mvnw clean install -DskipTests

COPY src /app/src

RUN ./mvnw clean install -DskipTests

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/target/app.jar app.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]