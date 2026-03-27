package co.com.bancolombia.snseventpublisher;

import co.com.bancolombia.model.events.gateways.EventsGateway;
import co.com.bancolombia.model.tournament.Tournament;
import co.com.bancolombia.snseventpublisher.commons.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Profile("aws")
@Component
@RequiredArgsConstructor
public class SnsEventPublisher implements EventsGateway {

    private final SnsTemplate snsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${aws.sns.tournament-events-arn}")
    private String tournamentTopicArn;

    @Override
    public Mono<Void> emit(Tournament tournament) {
         log.info("[SNS] Iniciando publicación evento tournamentId={}", tournament.getId());

            return buildPayload(tournament)
                    .flatMap(message -> Mono.fromRunnable(
                            () -> snsTemplate.convertAndSend(tournamentTopicArn, message)))
                    .doOnSuccess(v -> log.info("[SNS] Finalizado publicacion evento tournamentId={}", tournament.getId()))
                    .doOnError(e -> log.error("[SNS] Error tournamentId={}: {}", tournament.getId(), e.getMessage()))
                    .onErrorMap(JsonProcessingException.class, e ->
                             new RuntimeException(
                                    "[SNS] Fallo al enviar evento tournamentId=" + tournament.getId()
                                            + " | tipo: " + e.getClass().getSimpleName()
                                            + " | detalle: " + e.getMessage(), e))
                    .subscribeOn(Schedulers.boundedElastic())
                    .then();

    }

    private Mono<String> buildPayload(Tournament tournament) {
        return Mono.fromCallable(() -> {
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("eventType",    Constants.EVENT_TOURNAMENT_CREATE);
                    payload.put("tournamentId", tournament.getId());
                    payload.put("name",         tournament.getName());
                    payload.put("status",       tournament.getStatus() != null
                            ? tournament.getStatus().name() : null);
                    payload.put("categoryId",   tournament.getCategoryId());
                    payload.put("gameTypeId",   tournament.getGameTypeId());
                    payload.put("isFree",       tournament.getIsFree());
                    payload.put("startDate",    tournament.getStartDate() != null
                            ? tournament.getStartDate().toString() : null);
                    payload.put("endDate",      tournament.getEndDate() != null
                            ? tournament.getEndDate().toString() : null);

                    return objectMapper.writeValueAsString(payload);
                })
                .onErrorMap(JsonProcessingException.class, e ->
                        new RuntimeException(
                                "[SNS] Error serializando payload tournamentId=" + tournament.getId(), e))
                 ;
    }
}
