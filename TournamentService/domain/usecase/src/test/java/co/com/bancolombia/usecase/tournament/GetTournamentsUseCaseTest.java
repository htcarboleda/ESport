package co.com.bancolombia.usecase.tournament;

import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.exceptions.message.ErrorMessage;
import co.com.bancolombia.model.tournament.Tournament;
import co.com.bancolombia.model.tournament.gateways.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GetTournamentsUseCaseTest {

    @Mock
    private TournamentRepository tournamentRepository;

    @InjectMocks
    private GetTournamentsUseCase getTournamentsUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll_Success() {
        // Datos de prueba
        Tournament tournament = Tournament.builder().id(1).name("Tournament 1").build();

        // Mockeamos el repositorio para devolver una lista de torneos
        when(tournamentRepository.findAllPaged(anyInt(), anyInt(), any(), any(), any()))
                .thenReturn(Flux.just(tournament));

        // Ejecutamos la prueba
        StepVerifier.create(getTournamentsUseCase.findAll(0, 10, null, null, true))
                .expectNextMatches(t -> t.getId().equals(1) && t.getName().equals("Tournament 1"))
                .verifyComplete();

        verify(tournamentRepository, times(1)).findAllPaged(anyInt(), anyInt(), any(), any(), any());
    }

    @Test
    void testFindAll_Empty() {
        // Simulamos que no hay torneos en la base de datos
        when(tournamentRepository.findAllPaged(anyInt(), anyInt(), any(), any(), any()))
                .thenReturn(Flux.empty());

        // Ejecutamos la prueba
        StepVerifier.create(getTournamentsUseCase.findAll(0, 10, null, null, true))
                .verifyComplete(); // Debe completar sin emitir datos

        verify(tournamentRepository, times(1)).findAllPaged(anyInt(), anyInt(), any(), any(), any());
    }

    @Test
    void testFindAll_Error() {
        // Simulamos un error en la base de datos
        when(tournamentRepository.findAllPaged(anyInt(), anyInt(), any(), any(), any()))
                .thenReturn(Flux.error(new RuntimeException("Database error")));

        // Ejecutamos la prueba
        StepVerifier.create(getTournamentsUseCase.findAll(0, 10, null, null, true))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database error"))
                .verify();

        verify(tournamentRepository, times(1)).findAllPaged(anyInt(), anyInt(), any(), any(), any());
    }

    @Test
    void testFindById_Success() {
        // Datos de prueba
        Tournament tournament = Tournament.builder().id(1).name("Tournament 1").build();

        // Mockeamos el repositorio para devolver un torneo encontrado por ID
        when(tournamentRepository.findById(anyInt())).thenReturn(Mono.just(tournament));

        // Ejecutamos la prueba
        StepVerifier.create(getTournamentsUseCase.findById(1))
                .expectNextMatches(t -> t.getId().equals(1) && t.getName().equals("Tournament 1"))
                .verifyComplete();

        verify(tournamentRepository, times(1)).findById(anyInt());
    }

    @Test
    void testFindById_NotFound() {
        // Simulamos que el torneo no se encuentra en la base de datos
        when(tournamentRepository.findById(anyInt())).thenReturn(Mono.empty());

        // Ejecutamos la prueba
        StepVerifier.create(getTournamentsUseCase.findById(1))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getErrorMessage().equals(ErrorMessage.TOURNAMENT_NOT_FOUND))
                .verify();

        verify(tournamentRepository, times(1)).findById(anyInt());
    }

    @Test
    void testFindById_Error() {
        // Simulamos un error en la base de datos
        when(tournamentRepository.findById(anyInt()))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Ejecutamos la prueba
        StepVerifier.create(getTournamentsUseCase.findById(1))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database error"))
                .verify();

        verify(tournamentRepository, times(1)).findById(anyInt());
    }
}
