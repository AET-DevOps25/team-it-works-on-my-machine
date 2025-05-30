# Stage 1: Build the application
FROM gradle:8.14-jdk21 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
COPY src ./src
RUN gradle bootJar --no-daemon

# Stage 2: Run the application
FROM gcr.io/distroless/java21-debian12
ARG GH_CONNECTOR_PORT=3000
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE ${GH_CONNECTOR_PORT}
ENTRYPOINT ["java", "-jar", "app.jar"]
