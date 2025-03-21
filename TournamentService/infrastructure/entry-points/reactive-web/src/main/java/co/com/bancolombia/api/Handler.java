package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.TournamentCreateRequest;
import co.com.bancolombia.api.dto.request.TournamentsFilterRequest;
import co.com.bancolombia.api.dto.response.TournamentDTO;
import co.com.bancolombia.api.dto.response.TournamentListResponse;
import co.com.bancolombia.api.dto.validator.ObjectValidator;
import co.com.bancolombia.model.category.Category;
import co.com.bancolombia.model.gametype.GameType;
import co.com.bancolombia.model.tournament.Tournament;
import co.com.bancolombia.model.tournamentadmin.TournamentAdmin;
import co.com.bancolombia.model.tournamentstage.TournamentStage;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.usecase.tournament.CreateTournamentUseCase;
import co.com.bancolombia.usecase.tournament.GetTournamentsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final GetTournamentsUseCase getTournamentsUseCase;
    private final CreateTournamentUseCase createTournamentUseCase;
    private final ObjectValidator objectValidator;
    public static final String NOT_FOUND = "Unknown";
    public static final String ERROR = "Error en la Petición";

    public Mono<ServerResponse> getTournaments(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(TournamentsFilterRequest.class)
                .flatMap(x -> getTournamentsUseCase.findAll(x.getPage(),x.getSize(),x.getCategory(),x.getGameType(),x.getIsFree())
                        .collectList()
                        .map(tournaments -> new TournamentListResponse(
                                tournaments.stream().map(this::tournamentToDto).toList(),
                                tournaments.size(),
                                (int) Math.ceil((double) tournaments.size() / x.getSize()),
                                x.getPage()
                        )))
                .flatMap(response -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response)
                )
                .doOnError(e -> log.error(ERROR, e));
    }

    public Mono<ServerResponse> createTournament(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(TournamentCreateRequest.class)
                .doOnNext(objectValidator::validate)
                .flatMap(x -> createTournamentUseCase.create(createRqToTournament(x))
                        .map(this::tournamentToDto)
                        .switchIfEmpty(Mono.error(new RuntimeException("Error en la Creación del Torneo")))
                )
                .flatMap(e -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(e)
                )
                .doOnError(e -> log.error(ERROR, e));
    }

    public Mono<ServerResponse> getTournamentById(ServerRequest serverRequest) {
        Integer tournamentId = Integer.parseInt(serverRequest.pathVariable("id"));
        return getTournamentsUseCase.findById(tournamentId)
                .map(this::tournamentToDto)
                .flatMap(response -> ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(response))
                .switchIfEmpty(ServerResponse.notFound().build())
                .doOnError(e -> log.error(ERROR, e));
    }


    private TournamentDTO tournamentToDto(Tournament rs) {

        return TournamentDTO.builder()
                .id(rs.getId())
                .name(rs.getName())
                .description(rs.getDescription())
                .category(Optional.ofNullable(rs.getCategory()).map(Category::getDescription).orElse(NOT_FOUND))
                .gameType(Optional.ofNullable(rs.getGameType()).map(GameType::getName).orElse(NOT_FOUND))
                .startDate(rs.getStartDate())
                .endDate(rs.getEndDate())
                .format(rs.getFormat() != null ? rs.getFormat().toString() : NOT_FOUND)
                .isFree(rs.getIsFree())
                .status(rs.getStatus() != null ? rs.getStatus().toString() : NOT_FOUND)
                .creator(Optional.ofNullable(rs.getCreator()).map(User::getFullName).orElse(NOT_FOUND))
                .createdAt(rs.getCreatedAt())
                .updatedAt(rs.getUpdatedAt())
                .build();
    }

    private Tournament createRqToTournament(TournamentCreateRequest rs) {
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
                .stages(rs.getStages() != null ?
                        rs.getStages().stream()
                                .map(stage -> TournamentStage.builder()
                                        .name(stage.getName())
                                        .startDate(stage.getStartDate())
                                        .endDate(stage.getEndDate())
                                        .participantPrice(stage.getParticipantPrice())
                                        .spectatorPrice(stage.getSpectatorPrice())
                                        .maxParticipantTickets(stage.getMaxParticipantTickets())
                                        .maxSpectatorTickets(stage.getMaxSpectatorTickets())
                                        .freeParticipantSlots(stage.getFreeParticipantSlots())
                                        .paidParticipantSlots(stage.getPaidParticipantSlots())
                                        .freeSpectatorSlots(stage.getFreeSpectatorSlots())
                                        .paidSpectatorSlots(stage.getPaidSpectatorSlots())
                                        .build())
                                .toList() :
                        null
                )
                .build();
    }


}
