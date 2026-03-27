# ── Stage 2: Runtime (imagen mínima) ─────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuario no-root por seguridad
RUN addgroup -S esport && adduser -S esport -G esport
USER esport

COPY applications/app-service/build/libs/TournamentService.jar app.jar

EXPOSE 9097
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
    CMD wget -qO- http://localhost:9097/actuator/health || exit 1

ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75","-jar","app.jar"]


