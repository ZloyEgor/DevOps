# Базовый образ Maven с Java 21 (Temurin)
FROM maven:3.9.4-eclipse-temurin-21

WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/cvet-ochey-0.0.1-SNAPSHOT.jar"]
