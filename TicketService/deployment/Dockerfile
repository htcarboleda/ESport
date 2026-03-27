# ── Stage 2: Runtime (imagen mínima) ─────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuario no-root por seguridad
RUN addgroup -S esport && adduser -S esport -G esport
USER esport

COPY applications/app-service/build/libs/TicketService.jar app.jar

EXPOSE 8085
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget -qO- http://localhost:8085/actuator/health/liveness || exit 1

ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75","-jar","app.jar"]