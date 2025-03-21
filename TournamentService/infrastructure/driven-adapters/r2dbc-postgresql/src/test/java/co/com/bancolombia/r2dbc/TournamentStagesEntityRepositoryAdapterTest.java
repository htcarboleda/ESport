package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.tournamentstage.TournamentStage;
import co.com.bancolombia.r2dbc.entities.TournamentStageEntity;
import co.com.bancolombia.r2dbc.tournamentstage.TournamentStagesEntityRepository;
import co.com.bancolombia.r2dbc.tournamentstage.TournamentStagesEntityRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TournamentStagesEntityRepositoryAdapterTest {

    @Mock
    private TournamentStagesEntityRepository tournamentStagesEntityRepository;

    @InjectMocks
    private TournamentStagesEntityRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_Success() {
        // Datos de prueba
        TournamentStage tournamentStage = TournamentStage.builder()
                .id(1)
                .name("Stage 1")
                .build();

        TournamentStageEntity entity = TournamentStageEntity.fromDomain(tournamentStage);

        // Mockeamos el comportamiento del repositorio
        when(tournamentStagesEntityRepository.save(any(TournamentStageEntity.class)))
                .thenReturn(Mono.just(entity));

        // Ejecutamos la prueba
        Mono<TournamentStage> result = adapter.save(tournamentStage);

        // Verificamos que el método se ejecuta correctamente
        StepVerifier.create(result)
                .expectNextMatches(savedStage -> savedStage.getId().equals(tournamentStage.getId()) &&
                        savedStage.getName().equals(tournamentStage.getName()))
                .verifyComplete();

        verify(tournamentStagesEntityRepository, times(1)).save(any(TournamentStageEntity.class));
    }

    @Test
    void testSave_Error() {
        // Datos de prueba
        TournamentStage tournamentStage = TournamentStage.builder()
                .id(1)
                .name("Stage 1")
                .build();

        // Simulamos un error en el repositorio
        when(tournamentStagesEntityRepository.save(any(TournamentStageEntity.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Ejecutamos la prueba
        Mono<TournamentStage> result = adapter.save(tournamentStage);

        // Verificamos que se maneja correctamente el error
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database error"))
                .verify();

        verify(tournamentStagesEntityRepository, times(1)).save(any(TournamentStageEntity.class));
    }
}
