FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Cache dependencies first for faster rebuilds
COPY pom.xml .

# Render may inject MAVEN_CONFIG=/root/.m2, which can confuse mvnw; use mvn directly.
RUN mvn -q -DskipTests dependency:go-offline

COPY src src
RUN mvn -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/occupational-safety-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 10000
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-10000} -jar /app/app.jar"]