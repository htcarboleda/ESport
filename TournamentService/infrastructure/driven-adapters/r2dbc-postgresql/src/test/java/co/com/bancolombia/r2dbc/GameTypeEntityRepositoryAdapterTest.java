package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.gametype.GameType;
import co.com.bancolombia.r2dbc.entities.GameTypeEntity;
import co.com.bancolombia.r2dbc.entities.TournamentEntity;
import co.com.bancolombia.r2dbc.gametype.GameTypeEntityRepository;
import co.com.bancolombia.r2dbc.gametype.GameTypeEntityRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveSelectOperation;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameTypeEntityRepositoryAdapterTest {

    @Mock
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @Mock
    private GameTypeEntityRepository gameTypeEntityRepository;

    @InjectMocks
    private GameTypeEntityRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById_Success() {
        // Datos de prueba
        GameTypeEntity gameTypeEntity = new GameTypeEntity(1, "Game Type 1", "Alias 1", 10);
        GameType expectedGameType = gameTypeEntity.toDomain();

        // Mock the complete chain
        ReactiveSelectOperation.ReactiveSelect<GameTypeEntity> reactiveSelect = mock(ReactiveSelectOperation.ReactiveSelect.class);
        ReactiveSelectOperation.TerminatingSelect<GameTypeEntity> terminatingSelect = mock(ReactiveSelectOperation.TerminatingSelect.class);

        when(r2dbcEntityTemplate.select(GameTypeEntity.class)).thenReturn(reactiveSelect);
        when(reactiveSelect.matching(any(Query.class))).thenReturn(terminatingSelect);
        when(terminatingSelect.one()).thenReturn(Mono.just(gameTypeEntity));

        // Ejecutamos la prueba
        StepVerifier.create(adapter.findById(1))
                .expectNextMatches(gameType -> gameType.getId().equals(expectedGameType.getId()) &&
                        gameType.getName().equals(expectedGameType.getName()))
                .verifyComplete();

        verify(r2dbcEntityTemplate, times(1)).select(GameTypeEntity.class);
        verify(reactiveSelect, times(1)).matching(any(Query.class));
        verify(terminatingSelect, times(1)).one();
    }

    @Test
    void testFindById_NotFound() {
        // Simulamos que no se encuentra el game type en la base de datos
        ReactiveSelectOperation.ReactiveSelect<GameTypeEntity> reactiveSelect = mock(ReactiveSelectOperation.ReactiveSelect.class);
        ReactiveSelectOperation.TerminatingSelect<GameTypeEntity> terminatingSelect = mock(ReactiveSelectOperation.TerminatingSelect.class);

        when(r2dbcEntityTemplate.select(GameTypeEntity.class)).thenReturn(reactiveSelect);
        when(reactiveSelect.matching(any(Query.class))).thenReturn(terminatingSelect);
        when(terminatingSelect.one()).thenReturn(Mono.empty());

        // Ejecutamos la prueba
        StepVerifier.create(adapter.findById(1))
                .verifyComplete(); // Debe terminar sin emitir datos

        verify(r2dbcEntityTemplate, times(1)).select(GameTypeEntity.class);
        verify(reactiveSelect, times(1)).matching(any(Query.class));
        verify(terminatingSelect, times(1)).one();
    }

    @Test
    void testFindById_Error() {
        // Mock the complete chain
        ReactiveSelectOperation.ReactiveSelect<GameTypeEntity> reactiveSelect = mock(ReactiveSelectOperation.ReactiveSelect.class);
        ReactiveSelectOperation.TerminatingSelect<GameTypeEntity> terminatingSelect = mock(ReactiveSelectOperation.TerminatingSelect.class);

        when(r2dbcEntityTemplate.select(GameTypeEntity.class)).thenReturn(reactiveSelect);
        when(reactiveSelect.matching(any(Query.class))).thenReturn(terminatingSelect);
        when(terminatingSelect.one()).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Ejecutamos la prueba
        StepVerifier.create(adapter.findById(1))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database error"))
                .verify();

        verify(r2dbcEntityTemplate, times(1)).select(GameTypeEntity.class);
        verify(reactiveSelect, times(1)).matching(any(Query.class));
        verify(terminatingSelect, times(1)).one();
    }
}
