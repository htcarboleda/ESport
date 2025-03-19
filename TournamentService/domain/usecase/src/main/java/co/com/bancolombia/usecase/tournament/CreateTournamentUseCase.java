package co.com.bancolombia.usecase.tournament;

import co.com.bancolombia.model.category.gateways.CategoryRepository;
import co.com.bancolombia.model.enums.AdminRole;
import co.com.bancolombia.model.enums.TournamentStatus;
import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.exceptions.message.ErrorMessage;
import co.com.bancolombia.model.gametype.gateways.GameTypesRepository;
import co.com.bancolombia.model.tournament.Tournament;
import co.com.bancolombia.model.tournament.gateways.TournamentRepository;
import co.com.bancolombia.model.tournamentadmin.TournamentAdmin;
import co.com.bancolombia.model.tournamentadmin.gateways.TournamentAdminRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CreateTournamentUseCase {

    private static final Logger logger = Logger.getLogger(CreateTournamentUseCase.class.getName());


    private final TournamentRepository tournamentRepository;
    private final CategoryRepository categoryRepository;
    private final GameTypesRepository gameTypesRepository;
    private final TournamentAdminRepository tournamentAdminRepository;

    public Mono<Tournament> create(Tournament tournament) {
        logger.info("Creating tournament"+tournament);
//        return tournamentRepository.save(tournament);
        return validateTournament(tournament)
                .doOnNext(x -> logger.info(" *********************************************** validateTournament: " + x))
                .flatMap(tournamentRepository::save)
                .doOnNext(x -> logger.info(" *********************************************** createTournamentAdmins:  Tournament created: " + x))
                .flatMap(savedTournament -> createTournamentAdmins(savedTournament)
                        .doOnNext(x -> logger.info("*********************************************** createTournamentAdmins: " + x))
                        .thenReturn(savedTournament))
//                .flatMap(savedTournament -> eventPublisher.publishTournamentCreated(savedTournament)
//                        .thenReturn(savedTournament)
                ;
    }


    private Mono<Tournament> validateTournament(Tournament tournament) {
        return Mono.just(tournament)
                .flatMap(this::validateCategoryExists)
                .flatMap(this::validateGameTypeExists);
    }

    private Mono<Tournament> validateCategoryExists(Tournament tournament) {
        return categoryRepository.findById(tournament.getCategoryId())
                .switchIfEmpty(Mono.error(new BusinessException(ErrorMessage.CATEGORY_NOT_FOUND)))
                .map(category -> {
                    tournament.setCategory(category);
                    return tournament;
                });
    }

    private Mono<Tournament> validateGameTypeExists(Tournament tournament) {
        return gameTypesRepository.findById(tournament.getGameTypeId())
                .switchIfEmpty(Mono.error(new BusinessException(ErrorMessage.GAMETYPE_NOT_FOUND)))
                .map(gameType -> {
                    tournament.setGameType(gameType);
                    return tournament;
                });
    }

    private Mono<Void> createTournamentAdmins(Tournament tournament) {
        // Si no hay administradores, terminamos
        if (tournament.getAdmins() == null || tournament.getAdmins().isEmpty()) {
            return Mono.empty();
        }

        // Convertir cada administrador en la lista y guardarlos
        List<Mono<TournamentAdmin>> adminMonos = tournament.getAdmins().stream()
                .map(admin -> {
                    TournamentAdmin completeAdmin = admin.toBuilder()
                            .tournamentId(tournament.getId())
                            .userId(admin.getUserId())
                            .role(admin.getRole())
                            .isActive(true)
                            .build();

                    return tournamentAdminRepository.save(completeAdmin);
                })
                .collect(Collectors.toList());

        // Ejecutar todas las operaciones en paralelo
        return Mono.when(adminMonos);
    }
}
