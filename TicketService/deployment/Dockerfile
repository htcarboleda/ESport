# Dockerfile (en la raíz de cada servicio)

# ── Stage 1: Build ───────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
RUN ./gradlew dependencies --no-daemon   # cachea dependencias
COPY . .
RUN ./gradlew bootJar --no-daemon -x test

# ── Stage 2: Runtime (imagen mínima) ─────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuario no-root por seguridad
RUN addgroup -S esport && adduser -S esport -G esport
USER esport

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 9097
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
    CMD wget -qO- http://localhost:9097/actuator/health || exit 1

ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75","-jar","app.jar"]
