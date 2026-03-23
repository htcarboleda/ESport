package co.com.bancolombia.snseventpublisher;

import co.com.bancolombia.model.events.gateways.SnsEventGateway;
import co.com.bancolombia.model.tournament.Tournament;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class SnsEventPublisher implements SnsEventGateway {

    private final SnsTemplate snsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${aws.sns.tournament-events-arn}")
    private String tournamentTopicArn;

    @Override
    public Mono<Void> publishTournamentCreated(Tournament tournament) {
        log.info("[SNS] Iniciando publicación TOURNAMENT_CREATED tournamentId={}", tournament.getId());

        return Mono.fromRunnable(() -> {
            try {
                Map<String, Object> payload = new HashMap<>();
                payload.put("eventType", "TOURNAMENT_CREATED");
                payload.put("tournamentId", tournament.getId());
                payload.put("name", tournament.getName());
                payload.put("status", tournament.getStatus() != null ? tournament.getStatus().name() : null);
                payload.put("categoryId", tournament.getCategoryId());
                payload.put("gameTypeId", tournament.getGameTypeId());
                payload.put("isFree", tournament.getIsFree());
                payload.put("startDate", tournament.getStartDate() != null ? tournament.getStartDate().toString() : null);
                payload.put("endDate", tournament.getEndDate() != null ? tournament.getEndDate().toString() : null);

                String message = objectMapper.writeValueAsString(payload);
                snsTemplate.convertAndSend(tournamentTopicArn, message);
                log.info("[SNS] Publicado TOURNAMENT_CREATED tournamentId={}", tournament.getId());

            } catch (JsonProcessingException e) {
                log.error("[SNS] Error serializando payload TOURNAMENT_CREATED tournamentId={} — causa: {}",
                        tournament.getId(), e.getMessage(), e);
                throw new RuntimeException(
                        "[SNS] Error serializando payload para tournamentId=" + tournament.getId(), e);
            } catch (Exception e) {
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                log.error("[SNS] Error enviando TOURNAMENT_CREATED tournamentId={} — tipo: {} — causa: {}",
                        tournament.getId(), cause.getClass().getSimpleName(), cause.getMessage(), e);
                throw new RuntimeException(
                        "[SNS] Fallo al enviar evento TOURNAMENT_CREATED tournamentId=" + tournament.getId()
                        + " | tipo: " + cause.getClass().getSimpleName()
                        + " | detalle: " + cause.getMessage(), e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> publishTournamentStatusChanged(Integer tournamentId, String newStatus) {
        log.info("[SNS] Iniciando publicación TOURNAMENT_STATUS_CHANGED tournamentId={}", tournamentId);

        return Mono.fromRunnable(() -> {
            try {
                Map<String, Object> payload = new HashMap<>();
                payload.put("eventType", "TOURNAMENT_STATUS_CHANGED");
                payload.put("tournamentId", tournamentId);
                payload.put("newStatus", newStatus);

                String message = objectMapper.writeValueAsString(payload);
                snsTemplate.convertAndSend(tournamentTopicArn, message);
                log.info("[SNS] Publicado TOURNAMENT_STATUS_CHANGED tournamentId={}", tournamentId);

            } catch (JsonProcessingException e) {
                log.error("[SNS] Error serializando payload TOURNAMENT_STATUS_CHANGED tournamentId={} — causa: {}",
                        tournamentId, e.getMessage(), e);
                throw new RuntimeException(
                        "[SNS] Error serializando payload para tournamentId=" + tournamentId, e);
            } catch (Exception e) {
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                log.error("[SNS] Error enviando TOURNAMENT_STATUS_CHANGED tournamentId={} — tipo: {} — causa: {}",
                        tournamentId, cause.getClass().getSimpleName(), cause.getMessage(), e);
                throw new RuntimeException(
                        "[SNS] Fallo al enviar evento TOURNAMENT_STATUS_CHANGED tournamentId=" + tournamentId
                        + " | tipo: " + cause.getClass().getSimpleName()
                        + " | detalle: " + cause.getMessage(), e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
