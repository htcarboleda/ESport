package co.com.bancolombia.sqs.listener;

import co.com.bancolombia.model.audit.gateway.AuditGateway;
import co.com.bancolombia.usecase.ticket.TicketUseCase;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import java.util.Map;


import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class SqsTicketConsumer {

    private final TicketUseCase ticketUseCase;
    private final AuditGateway auditTrail;
    private final ObjectMapper objectMapper;

    /**
     * Escucha la cola ticket-update-queue.
     *
     * acknowledgementMode = ON_SUCCESS:
     *   - Si el método termina sin excepción → SQS elimina el mensaje (ACK)
     *   - Si lanza excepción → SQS lo vuelve a entregar hasta 3 veces → DLQ
     *
     * Los mensajes vienen de SNS que los envuelve en un envelope JSON.
     * El payload real está en el campo 'Message' del envelope.
     */
    @SqsListener(
            value    = "${aws.sqs.ticket-queue-url}",
            acknowledgementMode = "ON_SUCCESS"
    )
    public void onTournamentCreated(Message<String> message) {

        // 1. Obtener el body del mensaje SQS
        String rawBody = message.getPayload();
        log.debug("[SQS] Mensaje recibido raw: {}", rawBody);

        try {
            Integer tournamentId = extractTournamentId(rawBody);
            String  eventType    = extractEventType(rawBody);

            log.info("[SQS] ✅ Procesando {} tournamentId={}", eventType, tournamentId);

            // Llama al mismo UseCase que usa el bus Bancolombia
            ticketUseCase.createTicketsInventory(tournamentId)
                    .then(auditTrail.record(eventType, tournamentId,
                            "Inventario creado vía SQS"))
                    .doOnSuccess(v -> log.info("[SQS] ✅ OK tournamentId={}", tournamentId))
                    .doOnError(e -> log.error("[SQS] ❌ Error: {}", e.getMessage()))
                    .block(); // Necesario: @SqsListener no soporta retorno reactivo

        } catch (Exception e) {
            log.error("[SQS] ❌ Error procesando mensaje: {}", e.getMessage(), e);
            // Lanzar excepción fuerza el reintento en SQS
            throw new RuntimeException("Fallo al procesar mensaje SQS — reintentando", e);
        }
    }

    // SNS envuelve el mensaje en: {\"Type\":\"Notification\",\"Message\":\"...\"}
    // Detectar si es envelope SNS o mensaje directo SQS
    private Integer extractTournamentId(String rawBody) throws Exception {
        Map<String, Object> outer = objectMapper.readValue(
                rawBody, new TypeReference<>() {});

        Map<String, Object> event;
        if (outer.containsKey("Message")) {
            // Viene de SNS — extraer el payload real
            String inner = (String) outer.get("Message");
            event = objectMapper.readValue(inner, new TypeReference<>() {});
        } else {
            // Viene directo de SQS (ej: pruebas manuales)
            event = outer;
        }

        Object id = event.get("tournamentId");
        if (id == null) throw new IllegalArgumentException("tournamentId no encontrado en el mensaje");
        return ((Number) id).intValue();
    }

    private String extractEventType(String rawBody) throws Exception {
        Map<String, Object> outer = objectMapper.readValue(
                rawBody, new TypeReference<>() {});
        if (outer.containsKey("Message")) {
            String inner = (String) outer.get("Message");
            Map<String, Object> event = objectMapper.readValue(
                    inner, new TypeReference<>() {});
            return (String) event.getOrDefault("eventType", "TOURNAMENT_CREATED");
        }
        return (String) outer.getOrDefault("eventType", "TOURNAMENT_CREATED");
    }
}
