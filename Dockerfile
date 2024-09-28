FROM maven:3.9.9-amazoncorretto-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn package

FROM eclipse-temurin:21-ubi9-minimal

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
