package co.com.bancolombia.usecase.tournament;
import co.com.bancolombia.model.audit.gateways.AuditGateway;
import co.com.bancolombia.model.category.gateways.CategoryRepository;
import co.com.bancolombia.model.enums.TournamentStatus;
import co.com.bancolombia.model.events.gateways.EventsGateway;
import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.exceptions.TechnicalException;
import co.com.bancolombia.model.exceptions.message.BussinessMessages;
import co.com.bancolombia.model.gametype.gateways.GameTypesCacheGateway;
import co.com.bancolombia.model.gametype.gateways.GameTypesRepository;
import co.com.bancolombia.model.tournament.Tournament;
import co.com.bancolombia.model.category.gateways.CategoryCacheGateway;
import co.com.bancolombia.model.tournament.gateways.TournamentCacheGateway;
import co.com.bancolombia.model.tournament.gateways.TournamentRepository;
import co.com.bancolombia.model.tournamentadmin.TournamentAdmin;
import co.com.bancolombia.model.tournamentadmin.gateways.TournamentAdminRepository;
import co.com.bancolombia.model.tournamentstage.TournamentStage;
import co.com.bancolombia.model.tournamentstage.gateways.TournamentStageRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import static co.com.bancolombia.model.exceptions.message.TechnicalMessages.TIMEOUT_EXCEPTION;
import static co.com.bancolombia.model.exceptions.message.TechnicalMessages.UNEXPECTED_EXCEPTION;

@RequiredArgsConstructor
public class CreateTournamentUseCase {

    private static final Logger logger = Logger.getLogger(CreateTournamentUseCase.class.getName());

    private final TournamentRepository tournamentRepository;
    private final TournamentCacheGateway tournamentCache;
    private final CategoryRepository categoryRepository;
    private final CategoryCacheGateway categoryCache;
    private final GameTypesRepository gameTypeRepository;
    private final GameTypesCacheGateway gameTypeCache;
    private final TournamentAdminRepository tournamentAdminRepository;
    private final TournamentStageRepository tournamentStageRepository;
    private final EventsGateway eventsGateway;
    private final AuditGateway auditTrail;

    public Mono<Tournament> create(Tournament tournament) {

        logger.info("[SERVICE] Creacion de Torneo: " +tournament.getName() );

        return validateCategoryAndGameType(tournament)
                .thenReturn(tournament)
                .flatMap(tournamentRepository::save)
                .flatMap(savedTournament -> tournamentCache
                        .putExistence(savedTournament.getName(), savedTournament.getStartDate(), savedTournament.getEndDate(), true)
                        .onErrorComplete()
                        .thenReturn(savedTournament))
                .flatMap(savedTournament -> auditTrail.record("Created_bd_tournaments",
                                savedTournament.toString())
                        .thenReturn(savedTournament))
                .flatMap(savedTournament -> createTournamentAdmins(tournament, savedTournament.getId())
                        .thenReturn(savedTournament))
                .flatMap(savedTournament -> createTournamentStages(tournament, savedTournament.getId())
                        .thenReturn(savedTournament))
                .flatMap(savedTournament ->publishEvents(tournament, savedTournament)
                        .thenReturn(savedTournament))
                .switchIfEmpty(Mono.error(new TechnicalException(UNEXPECTED_EXCEPTION)));
    }


    public Mono<Void> validateCategoryAndGameType(Tournament tournamen) {

        logger.info("[SERVICE] Validacion Torneo, Categoria y Tipo de Juego " );

        return Mono.zip(
                categoryExists(tournamen.getCategoryId()),
                gameTypeExists(tournamen.getGameTypeId()),
                tournamentExists(tournamen.getName(), tournamen.getStartDate(), tournamen.getEndDate())
        ).flatMap(tuple -> {
            if (!tuple.getT1()) return Mono.error(
                    new BusinessException(BussinessMessages.CATEGORY_NOT_FOUND));
            if (!tuple.getT2()) return Mono.error(
                    new BusinessException(BussinessMessages.GAMETYPE_NOT_FOUND));
            if (tuple.getT3()) return Mono.error(
                    new BusinessException(BussinessMessages.TOURNAMENT_EXISTS));
            return Mono.empty();
        });
    }

    private Mono<Boolean> categoryExists(Integer categoryId) {
        return categoryCache.getExistence(categoryId)
                .switchIfEmpty(
                        categoryRepository.existsById(categoryId)
                                .flatMap(exists -> categoryCache
                                        .putExistence(categoryId, exists)
                                        .onErrorComplete()
                                        .thenReturn(exists))
                )
                .onErrorResume(e -> {
                    logger.warning("[REDIS ERROR] category fallback DB. id={}"+ categoryId);
                    return categoryRepository.existsById(categoryId);
                });
    }

    private Mono<Boolean> gameTypeExists(Integer gameTypeId) {
        return gameTypeCache.getExistence(gameTypeId)
                .switchIfEmpty(
                        gameTypeRepository.existsById(gameTypeId)
                                .flatMap(exists -> gameTypeCache
                                        .putExistence(gameTypeId, exists)
                                        .onErrorComplete()
                                        .thenReturn(exists))
                )
                .onErrorResume(e -> {
                    logger.warning("[REDIS ERROR] gameType fallback DB. id={}"+gameTypeId);
                    return gameTypeRepository.existsById(gameTypeId);
                });
    }

    private Mono<Boolean> tournamentExists(String description, LocalDate startDate, LocalDate endDate) {
        return tournamentCache.getExistence(description, startDate, endDate)
                .switchIfEmpty(
                        tournamentRepository.existsByName(description, startDate, endDate)
                                .flatMap(exists -> tournamentCache
                                        .putExistence(description, startDate, endDate, exists)
                                        .onErrorComplete()
                                        .thenReturn(exists))
                )
                .onErrorResume(e -> {
                    logger.warning("[REDIS ERROR] tournament fallback DB. id={}"+description);
                    return tournamentRepository.existsByName(description, startDate, endDate);
                });
    }


    private Mono<Void> createTournamentAdmins(Tournament tournament, Integer idTournament) {

        logger.info("[SERVICE] Creacion de Administradores " );

        if (tournament.getAdmins() == null || tournament.getAdmins().isEmpty()) {
            return Mono.empty();
        }

        // Convertir cada administrador en la lista y guardarlos
        List<Mono<TournamentAdmin>> adminMonos = tournament.getAdmins().stream()
                .map(admin -> {
                    TournamentAdmin completeAdmin = admin.toBuilder()
                            .tournamentId(idTournament)
                            .userId(admin.getUserId())
                            .role(admin.getRole())
                            .isActive(true)
                            .build();

                    return tournamentAdminRepository.save(completeAdmin)
                            .flatMap(saved -> auditTrail.record("Created_bd_tournament_admins",
                                            saved.toString())
                                    .thenReturn(saved));
                })
                .toList();

        // Ejecutar todas las operaciones en paralelo
        return Mono.when(adminMonos);
    }

    private Mono<Void> createTournamentStages(Tournament tournament, Integer idTournament) {

        logger.info("[SERVICE] Creacion de Etapas " );

        if (tournament.getStages() == null || tournament.getStages().isEmpty()) {
            return Mono.empty();
        }

        List<Mono<TournamentStage>> stagesMonos = tournament.getStages().stream()
                .map(stage -> {
                    TournamentStage completeStage = stage.toBuilder()
                            .tournamentId(idTournament)
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
                            .build();
                    return tournamentStageRepository.save(completeStage)
                            .flatMap(saved -> auditTrail.record("Created_bd_tournament_stages",
                                            saved.toString())
                                    .thenReturn(saved));
                })
                .toList();

        return Mono.when(stagesMonos);
    }

    // Publica el evento
    private Mono<Tournament> publishEvents(Tournament original, Tournament saved) {
        boolean shouldPublish = TournamentStatus.PUBLICADO.equals(saved.getStatus())
                && original.getStages() != null
                && !original.getStages().isEmpty();

        if (!shouldPublish) return Mono.just(saved);

        return eventsGateway.emit(saved)
                .thenReturn(saved);
    }


}
