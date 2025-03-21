package co.com.bancolombia.usecase.tournament;

import co.com.bancolombia.model.category.Category;
import co.com.bancolombia.model.category.gateways.CategoryRepository;
import co.com.bancolombia.model.enums.AdminRole;
import co.com.bancolombia.model.enums.TournamentStatus;
import co.com.bancolombia.model.events.gateways.EventsGateway;
import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.exceptions.TechnicalException;
import co.com.bancolombia.model.exceptions.message.ErrorMessage;
import co.com.bancolombia.model.gametype.GameType;
import co.com.bancolombia.model.gametype.gateways.GameTypesRepository;
import co.com.bancolombia.model.tournament.Tournament;
import co.com.bancolombia.model.tournament.gateways.TournamentRepository;
import co.com.bancolombia.model.tournamentadmin.TournamentAdmin;
import co.com.bancolombia.model.tournamentadmin.gateways.TournamentAdminRepository;
import co.com.bancolombia.model.tournamentstage.TournamentStage;
import co.com.bancolombia.model.tournamentstage.gateways.TournamentStageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateTournamentUseCaseTest {

    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private GameTypesRepository gameTypesRepository;
    @Mock
    private TournamentAdminRepository tournamentAdminRepository;
    @Mock
    private TournamentStageRepository tournamentStageRepository;
    @Mock
    private EventsGateway eventsGateway;

    @InjectMocks
    private CreateTournamentUseCase createTournamentUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTournament_Success() {
        Tournament tournament = Tournament.builder()
                .id(1)
                .categoryId(1)
                .gameTypeId(2)
                .status(TournamentStatus.PUBLICADO)
                .build();

        when(categoryRepository.findById(anyInt())).thenReturn(Mono.just(new Category(1, "Soccer")));
        when(gameTypesRepository.findById(anyInt())).thenReturn(Mono.just(new GameType(2, "Shooter")));
        when(tournamentRepository.save(any())).thenReturn(Mono.just(tournament));

        StepVerifier.create(createTournamentUseCase.create(tournament))
                .expectNextMatches(t -> t.getId().equals(1))
                .verifyComplete();

        verify(tournamentRepository, times(1)).save(any());
    }

    @Test
    void testCreateTournament_CategoryNotFound() {
        Tournament tournament = Tournament.builder().categoryId(1).gameTypeId(2).build();

        when(categoryRepository.findById(anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(createTournamentUseCase.create(tournament))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getErrorMessage().equals(ErrorMessage.CATEGORY_NOT_FOUND))
                .verify();

        verify(categoryRepository, times(1)).findById(anyInt());
        verify(gameTypesRepository, never()).findById(anyInt());
    }

    @Test
    void testCreateTournament_GameTypeNotFound() {
        Tournament tournament = Tournament.builder().categoryId(1).gameTypeId(2).build();

        when(categoryRepository.findById(anyInt())).thenReturn(Mono.just(new Category(1, "Soccer")));
        when(gameTypesRepository.findById(anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(createTournamentUseCase.create(tournament))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getErrorMessage().equals(ErrorMessage.GAMETYPE_NOT_FOUND))
                .verify();

        verify(categoryRepository, times(1)).findById(anyInt());
        verify(gameTypesRepository, times(1)).findById(anyInt());
    }

    @Test
    void testCreateTournament_WithAdmins() {
        TournamentAdmin admin = TournamentAdmin.builder().userId(5).tournamentId(1).role(AdminRole.valueOf("SUBADMINISTRADOR")).build();
        Tournament tournament = Tournament.builder().id(1).categoryId(1).gameTypeId(2).admins(List.of(admin)).build();

        when(categoryRepository.findById(anyInt())).thenReturn(Mono.just(new Category(1, "Soccer")));
        when(gameTypesRepository.findById(anyInt())).thenReturn(Mono.just(new GameType(2, "Shooter")));
        when(tournamentRepository.save(any())).thenReturn(Mono.just(tournament));
        when(tournamentAdminRepository.save(any())).thenReturn(Mono.just(admin));

        StepVerifier.create(createTournamentUseCase.create(tournament))
                .expectNextMatches(t -> t.getId().equals(1))
                .verifyComplete();

        verify(tournamentAdminRepository, times(1)).save(any());
    }

    @Test
    void testCreateTournament_WithStages() {
        TournamentStage stage = TournamentStage.builder().name("Stage 1").build();
        Tournament tournament = Tournament.builder().id(1).categoryId(1).gameTypeId(2).stages(List.of(stage)).build();

        when(categoryRepository.findById(anyInt())).thenReturn(Mono.just(new Category(1, "Soccer")));
        when(gameTypesRepository.findById(anyInt())).thenReturn(Mono.just(new GameType(2, "Shooter")));
        when(tournamentRepository.save(any())).thenReturn(Mono.just(tournament));
        when(tournamentStageRepository.save(any())).thenReturn(Mono.just(stage));

        StepVerifier.create(createTournamentUseCase.create(tournament))
                .expectNextMatches(t -> t.getId().equals(1))
                .verifyComplete();

        verify(tournamentStageRepository, times(1)).save(any());
    }

    @Test
    void testCreateTournament_EmitEvent() {
        // Setup tournament with required data
        TournamentStage stage = TournamentStage.builder().name("Stage 1").build();
        Tournament tournament = Tournament.builder()
                .id(1)
                .categoryId(1)
                .gameTypeId(2)
                .status(TournamentStatus.PUBLICADO)
                .stages(List.of(stage))
                .build();

        // Mock the necessary repository calls
        when(categoryRepository.findById(anyInt())).thenReturn(Mono.just(new Category(1, "Soccer")));
        when(gameTypesRepository.findById(anyInt())).thenReturn(Mono.just(new GameType(2, "Shooter")));
        when(tournamentRepository.save(any())).thenReturn(Mono.just(tournament));
        when(tournamentStageRepository.save(any())).thenReturn(Mono.just(stage));

        // This is the key fix - return Mono.just(tournament.getId()) instead of Mono.empty()
        when(eventsGateway.emit(anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(createTournamentUseCase.create(tournament))
                .expectNextMatches(t -> t.getId().equals(1))
                .verifyComplete();

        // Verify our mocks were called
        verify(tournamentRepository, times(1)).save(any());
        verify(tournamentStageRepository, times(1)).save(any());
        verify(eventsGateway, times(1)).emit(anyInt());
    }

    @Test
    void testCreateTournament_Error() {
        Tournament tournament = Tournament.builder().id(1).categoryId(1).gameTypeId(2).build();

        when(categoryRepository.findById(anyInt())).thenReturn(Mono.just(new Category(1, "Soccer")));
        when(gameTypesRepository.findById(anyInt())).thenReturn(Mono.just(new GameType(2, "Shooter")));
        when(tournamentRepository.save(any())).thenReturn(Mono.empty());

        StepVerifier.create(createTournamentUseCase.create(tournament))
                .expectError(TechnicalException.class)
                .verify();
    }
}
