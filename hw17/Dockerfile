# Stage 1: Build stage
FROM maven:3.9.11-eclipse-temurin-17-alpine AS builder
WORKDIR /build
COPY pom.xml .
# download necessary dependencies to a cached layer
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package

# Stage 2: Runtime image creation stage, use jre as base image
FROM bellsoft/liberica-openjre-alpine-musl:17
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=builder --chown=appuser:appgroup /build/target/hw17-docker-mysql-1.0.jar ./app.jar
EXPOSE 8080
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]