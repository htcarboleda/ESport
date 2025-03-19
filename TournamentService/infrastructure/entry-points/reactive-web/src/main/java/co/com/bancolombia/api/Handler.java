package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.TournamentCreateRequest;
import co.com.bancolombia.api.dto.request.TournamentsFilterRequest;
import co.com.bancolombia.api.dto.response.TournamentDTO;
import co.com.bancolombia.api.dto.response.TournamentListResponse;
import co.com.bancolombia.api.dto.validator.ObjectValidator;
import co.com.bancolombia.model.tournament.Tournament;
import co.com.bancolombia.model.tournamentadmin.TournamentAdmin;
import co.com.bancolombia.usecase.tournament.CreateTournamentUseCase;
import co.com.bancolombia.usecase.tournament.GetTournamentsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final GetTournamentsUseCase getTournamentsUseCase;
    private final CreateTournamentUseCase createTournamentUseCase;
    private final ObjectValidator objectValidator;

    public Mono<ServerResponse> getTournaments(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(TournamentsFilterRequest.class)
                .flatMap(x -> getTournamentsUseCase.findAll(x.getPage(),x.getSize(),x.getCategory(),x.getGameType(),x.getIsFree())
                        .collectList()
                        .map(tournaments -> new TournamentListResponse(
                                tournaments.stream().map(this::TournamentToDto).toList(),
                                tournaments.size(),
                                (int) Math.ceil((double) tournaments.size() / x.getSize()),
                                x.getPage()
                        )))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response)
                )
                .doOnError(e -> log.error("Error", e));

    }


    public Mono<ServerResponse> createTournament(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(TournamentCreateRequest.class)
                .doOnNext(objectValidator::validate)
                .doOnNext(e -> log.info("createTournamentUseCase createTournamentUseCase: {}", e.toString()))
                .flatMap(x -> createTournamentUseCase.create(CreateRqToTournament(x))
                        .doOnNext(e -> log.info("createTournamentUseCase createTournamentUseCase: {}", e.toString()))
                        .map(this::TournamentToDto)
                        .doOnNext(e -> log.info("TournamentToDtoTournamentToDto: {}", e.toString()))
                )
                .flatMap(e -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(e)
                )
                .doOnError(e -> log.error("Error", e));
    }

    public Mono<ServerResponse> getTournamentById(ServerRequest serverRequest) {

        Integer tournamentId = Integer.parseInt(serverRequest.pathVariable("id"));

        return getTournamentsUseCase.findById(tournamentId)
                .map(this::TournamentToDto)
                .flatMap(response -> ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(response))
                .doOnError(e -> log.error("Error", e));
    }


    private TournamentDTO TournamentToDto(Tournament rs) {

        return TournamentDTO.builder()
                .id(rs.getId())
                .name(rs.getName())
                .description(rs.getDescription())
                .category(rs.getCategory().getDescription())
                .gameType(rs.getGameType().getName())
                .startDate(rs.getStartDate())
                .endDate(rs.getEndDate())
                .format(rs.getFormat().toString())
                .isFree(rs.getIsFree())
                .status(rs.getStatus().toString())
                .creator(rs.getCreator().getFullName())
                .createdAt(rs.getCreatedAt())
                .updatedAt(rs.getUpdatedAt())
                .build();
    }

    private Tournament CreateRqToTournament(TournamentCreateRequest rs) {
        log.info("CreateRqToTournament: {}", rs.getIsFree());

        return Tournament.builder()
                .name(rs.getName())
                .description(rs.getDescription())
                .categoryId(rs.getCategory())
                .gameTypeId(rs.getGameType())
                .startDate(rs.getStartDate())
                .endDate(rs.getEndDate())
                .format(rs.getFormat())
                .isFree(rs.getIsFree())
                .status(rs.getStatus())
                .creatorId(1)
                .admins(rs.getAdditionalAdmins() != null ?
                        rs.getAdditionalAdmins().stream()
                                .map(admin -> TournamentAdmin.builder()
                                        .userId(admin.getUserId())
                                        .role(admin.getRole())
                                        .build())
                                .toList() :
                        null
                )
                .build();
    }



/*

.admins(rs.getAdditionalAdmins() != null ?
        rs.getAdditionalAdmins().stream()
            .map(admin -> co.com.bancolombia.model.tournamentadmin.TournamentAdmin.builder()
                .userId(admin.getUserId())
                .build())
            .toList() :
        null)
.build();



*    public Flux<Tournament> getFilteredTournaments(TournamentFilterRequest filter) {
        return tournamentGateway.findFiltered(filter)
                .collectList()
                .map(tournaments -> new TournamentListResponse(
                        tournaments,
                        tournaments.size(),
                        (int) Math.ceil((double) tournaments.size() / filter.getSize()),
                        filter.getPage()
                ));
    }
*
                .flatMap(e -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(header -> headersResponse(e, serverRequest.path()).accept(header))
                        .body(fromValue(e))
                        .doOnNext(response -> loggingAdapter.loggingHttpAdapter(
                                EventType.RESPONSE,
                                uuidCode,
                                Constants.CONSUMER_ID_VALUE,
                                serverRequest.path(),
                                response,
                                serverRequest.headers().asHttpHeaders().toSingleValueMap(),
                                serverRequest.method().name(),
                                null))
                );
*
*
*
* */




}
