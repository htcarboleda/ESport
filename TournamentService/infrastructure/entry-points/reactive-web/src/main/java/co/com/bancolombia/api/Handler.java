package co.com.bancolombia.api;

import co.com.bancolombia.api.Util.UtilHandler;
import co.com.bancolombia.api.dto.request.TournamentCreateRequest;
import co.com.bancolombia.api.dto.request.TournamentsFilterRequest;
import co.com.bancolombia.api.dto.response.TournamentListResponse;
import co.com.bancolombia.api.dto.validator.ObjectValidator;
import co.com.bancolombia.model.exceptions.TechnicalException;
import co.com.bancolombia.usecase.tournament.CreateTournamentUseCase;
import co.com.bancolombia.usecase.tournament.GetTournamentsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeoutException;

import static co.com.bancolombia.model.exceptions.message.TechnicalMessages.TIMEOUT_EXCEPTION;
import static co.com.bancolombia.model.exceptions.message.TechnicalMessages.UNEXPECTED_EXCEPTION;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final GetTournamentsUseCase getTournamentsUseCase;
    private final CreateTournamentUseCase createTournamentUseCase;
    private final ObjectValidator objectValidator;
    private final UtilHandler utilHandler;

    public Mono<ServerResponse> createTournament(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(TournamentCreateRequest.class)
                .doOnNext(objectValidator::validate)
                .flatMap(x -> createTournamentUseCase.create(utilHandler.createRqToTournament(x))
                        .map(rs-> utilHandler.tournamentToDto(rs))
                        .switchIfEmpty(Mono.error(new TechnicalException(UNEXPECTED_EXCEPTION))))
                .flatMap(e -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(e)
                )
                .onErrorResume(TimeoutException.class, ex -> Mono.error(new TechnicalException(TIMEOUT_EXCEPTION)));
    }

    public Mono<ServerResponse> getTournaments(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(TournamentsFilterRequest.class)
                .flatMap(x -> getTournamentsUseCase.findAll(x.getPage(),x.getSize(),x.getCategory(),x.getGameType(),x.getIsFree())
                        .collectList()
                        .map(tournaments -> new TournamentListResponse(
                                tournaments.stream().map(rs-> utilHandler.tournamentToDto(rs)).toList(),
                                tournaments.size(),
                                (int) Math.ceil((double) tournaments.size() / x.getSize()),
                                x.getPage()
                        )))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response)
                )
                .onErrorResume(TimeoutException.class, ex -> Mono.error(new TechnicalException(TIMEOUT_EXCEPTION)));
    }

    public Mono<ServerResponse> getTournamentById(ServerRequest serverRequest) {
        Integer tournamentId = Integer.parseInt(serverRequest.pathVariable("id"));
        return getTournamentsUseCase.findById(tournamentId)
                .map(rs-> utilHandler.tournamentToDto(rs))
                .flatMap(response -> ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(response))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(TimeoutException.class, ex -> Mono.error(new TechnicalException(TIMEOUT_EXCEPTION)));
    }



}
