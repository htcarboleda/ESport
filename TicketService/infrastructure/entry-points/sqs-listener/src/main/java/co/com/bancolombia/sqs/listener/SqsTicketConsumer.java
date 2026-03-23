package co.com.bancolombia.sqs.listener;

import co.com.bancolombia.model.audit.gateway.AuditGateway;
import co.com.bancolombia.usecase.ticket.TicketUseCase;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsTicketConsumer {

    private final TicketUseCase ticketUseCase;
    private final AuditGateway auditTrail;
    private final ObjectMapper objectMapper;

    public Mono<Void> process(Message message) {
        String rawBody = message.body();
        log.info("[SQS] Mensaje recibido: {}", rawBody);

        return Mono.fromCallable(() -> extractPayload(rawBody))
                .flatMap(payload -> {
                    Integer tournamentId = extractTournamentId(payload);
                    String eventType = extractEventType(payload);
                    log.info("[SQS] Procesando {} tournamentId={}", eventType, tournamentId);
                    return ticketUseCase.createTicketsInventory(tournamentId)
                            .then(auditTrail.record(eventType, tournamentId,
                                    "Inventario creado vía SQS"));
                })
                .doOnSuccess(v -> log.info("[SQS] OK mensaje procesado"))
                .onErrorResume(JsonParseException.class, e -> {
                    log.warn("[SQS] Mensaje ignorado — no es JSON válido. Body: '{}'. Error: {}",
                            rawBody, e.getMessage());
                    return Mono.empty();
                })
                .doOnError(e -> log.error("[SQS] Error procesando mensaje: {}", e.getMessage(), e));
    }

    private Map<String, Object> extractPayload(String rawBody) throws Exception {
        Map<String, Object> outer = objectMapper.readValue(rawBody, new TypeReference<>() {});
        if (outer.containsKey("Message")) {
            String inner = (String) outer.get("Message");
            if (!inner.trim().startsWith("{")) {
                throw new JsonParseException(null,
                        "El campo 'Message' del envelope SNS no es JSON: '" + inner + "'");
            }
            return objectMapper.readValue(inner, new TypeReference<>() {});
        }
        return outer;
    }

    private Integer extractTournamentId(Map<String, Object> payload) {
        Object id = payload.get("tournamentId");
        if (id == null) throw new IllegalArgumentException("tournamentId no encontrado en el mensaje");
        return ((Number) id).intValue();
    }

    private String extractEventType(Map<String, Object> payload) {
        return (String) payload.getOrDefault("eventType", "TOURNAMENT_CREATED");
    }
}
