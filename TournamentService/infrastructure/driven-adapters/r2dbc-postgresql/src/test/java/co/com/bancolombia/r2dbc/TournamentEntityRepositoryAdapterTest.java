package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.enums.TournamentFormat;
import co.com.bancolombia.model.enums.TournamentStatus;
import co.com.bancolombia.model.tournament.Tournament;
import co.com.bancolombia.r2dbc.entities.CategoryEntity;
import co.com.bancolombia.r2dbc.entities.GameTypeEntity;
import co.com.bancolombia.r2dbc.entities.TournamentEntity;
import co.com.bancolombia.r2dbc.entities.UserEntity;
import co.com.bancolombia.r2dbc.tournament.TournamentEntityRepository;
import co.com.bancolombia.r2dbc.tournament.TournamentEntityRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveSelectOperation;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TournamentEntityRepositoryAdapterTest {

    @Mock
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @Mock
    private TournamentEntityRepository tournamentEntityRepository;

    @InjectMocks
    private TournamentEntityRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllPaged_Success() {
        // Complete all the required fields for the tournament entity
        TournamentEntity tournamentEntity = TournamentEntity.builder()
                .id(1)
                .name("Tournament 1")
                .description("Description 1")
                .category(1)
                .gameType(2)
                .creatorId(3)
                .startDate(LocalDate.parse("2021-10-01"))
                .endDate(LocalDate.parse("2021-10-20"))
                .format(TournamentFormat.valueOf("LIGA"))
                .isFree(true)
                .status(TournamentStatus.valueOf("PUBLICADO"))
                .build();

        CategoryEntity categoryEntity = new CategoryEntity(1, "Category 1", "Alias 1", "Description 1", 10);
        GameTypeEntity gameTypeEntity = new GameTypeEntity(2, "Game Type 2", "Alias 2", 10);
        UserEntity userEntity = new UserEntity(3, "User 3", "Email 3", "Username 3");

        // Mock with the correct method signature
        when(r2dbcEntityTemplate.select(any(Query.class), eq(TournamentEntity.class)))
                .thenReturn(Flux.just(tournamentEntity));

        when(r2dbcEntityTemplate.selectOne(any(Query.class), eq(CategoryEntity.class)))
                .thenReturn(Mono.just(categoryEntity));
        when(r2dbcEntityTemplate.selectOne(any(Query.class), eq(GameTypeEntity.class)))
                .thenReturn(Mono.just(gameTypeEntity));
        when(r2dbcEntityTemplate.selectOne(any(Query.class), eq(UserEntity.class)))
                .thenReturn(Mono.just(userEntity));

        StepVerifier.create(adapter.findAllPaged(0, 10, 1, 2, true))
                .expectNextMatches(tournament -> tournament.getId().equals(1))
                .verifyComplete();

        verify(r2dbcEntityTemplate, times(1)).select(any(Query.class), eq(TournamentEntity.class));
    }

    @Test
    void testFindAllPaged_Empty() {
        when(r2dbcEntityTemplate.select(any(Query.class), eq(TournamentEntity.class)))
                .thenReturn(Flux.empty());

        StepVerifier.create(adapter.findAllPaged(0, 10, 1, 2, true))
                .verifyComplete();

        verify(r2dbcEntityTemplate, times(1)).select(any(Query.class), eq(TournamentEntity.class));
    }

    @Test
    void testFindAllPaged_Error() {
        when(r2dbcEntityTemplate.select(any(Query.class), eq(TournamentEntity.class)))
                .thenReturn(Flux.error(new RuntimeException("Database error")));

        StepVerifier.create(adapter.findAllPaged(0, 10, 1, 2, true))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database error"))
                .verify();

        verify(r2dbcEntityTemplate, times(1)).select(any(Query.class), eq(TournamentEntity.class));
    }

    @Test
    void testSave_Success() {
        Tournament tournament = Tournament.builder().id(1).name("Tournament 1").categoryId(1).gameTypeId(2).creatorId(1).build();

        TournamentEntity entity = TournamentEntity.fromDomain(tournament);

        when(r2dbcEntityTemplate.selectOne(any(Query.class), eq(CategoryEntity.class)))
                .thenReturn(Mono.just(new CategoryEntity(1, "Category 1", "Alias 1", "Description 1", 10)));

        when(r2dbcEntityTemplate.selectOne(any(Query.class), eq(GameTypeEntity.class)))
                .thenReturn(Mono.just(new GameTypeEntity(2, "Game Type 2", "Alias 2", 10)));

        when(r2dbcEntityTemplate.selectOne(any(Query.class), eq(UserEntity.class)))
                .thenReturn(Mono.just(new UserEntity(3, "User 3", "Email 3", "Username 3")));

        when(tournamentEntityRepository.save(any(TournamentEntity.class)))
                .thenReturn(Mono.just(entity));

        StepVerifier.create(adapter.save(tournament))
                .expectNextMatches(savedTournament -> savedTournament.getId().equals(1))
                .verifyComplete();

        verify(tournamentEntityRepository, times(1)).save(any(TournamentEntity.class));
    }

    @Test
    void testSave_Error() {
        Tournament tournament = Tournament.builder().id(1).name("Tournament 1").build();

        when(tournamentEntityRepository.save(any(TournamentEntity.class)))
                .thenReturn(Mono.error(new RuntimeException("Save error")));

        StepVerifier.create(adapter.save(tournament))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Save error"))
                .verify();

        verify(tournamentEntityRepository, times(1)).save(any(TournamentEntity.class));
    }

    @Test
    void testFindById_Success() {
        TournamentEntity tournamentEntity = TournamentEntity.builder().id(1).name("Tournament 1").category(1).gameType(2).creatorId(2).build();
        CategoryEntity categoryEntity = new CategoryEntity(1, "Category 1","Alias 1","Description 1", 10);
        GameTypeEntity gameTypeEntity = new GameTypeEntity(2, "Game Type 2","Alias 2",10);
        UserEntity userEntity = new UserEntity(3, "User 3","Email 3","Username 3");

        // Mock the complete chain
        ReactiveSelectOperation.ReactiveSelect<TournamentEntity> reactiveSelect = mock(ReactiveSelectOperation.ReactiveSelect.class);
        ReactiveSelectOperation.TerminatingSelect<TournamentEntity> terminatingSelect = mock(ReactiveSelectOperation.TerminatingSelect.class);

        when(r2dbcEntityTemplate.select(TournamentEntity.class)).thenReturn(reactiveSelect);
        when(reactiveSelect.matching(any(Query.class))).thenReturn(terminatingSelect);
        when(terminatingSelect.one()).thenReturn(Mono.just(tournamentEntity));

        // Keep your other mocks
        when(r2dbcEntityTemplate.selectOne(any(Query.class), eq(CategoryEntity.class)))
                .thenReturn(Mono.just(categoryEntity));
        when(r2dbcEntityTemplate.selectOne(any(Query.class), eq(GameTypeEntity.class)))
                .thenReturn(Mono.just(gameTypeEntity));
        when(r2dbcEntityTemplate.selectOne(any(Query.class), eq(UserEntity.class)))
                .thenReturn(Mono.just(userEntity));

        StepVerifier.create(adapter.findById(1))
                .expectNextMatches(tournament -> tournament.getId().equals(1))
                .verifyComplete();

        verify(r2dbcEntityTemplate, times(1)).select(TournamentEntity.class);
        verify(reactiveSelect, times(1)).matching(any(Query.class));
        verify(terminatingSelect, times(1)).one();
    }

    @Test
    void testFindById_NotFound() {
        // Mock the complete chain
        ReactiveSelectOperation.ReactiveSelect<TournamentEntity> reactiveSelect = mock(ReactiveSelectOperation.ReactiveSelect.class);
        ReactiveSelectOperation.TerminatingSelect<TournamentEntity> terminatingSelect = mock(ReactiveSelectOperation.TerminatingSelect.class);

        when(r2dbcEntityTemplate.select(TournamentEntity.class)).thenReturn(reactiveSelect);
        when(reactiveSelect.matching(any(Query.class))).thenReturn(terminatingSelect);
        when(terminatingSelect.one()).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById(1))
                .verifyComplete();

        verify(r2dbcEntityTemplate, times(1)).select(TournamentEntity.class);
        verify(reactiveSelect, times(1)).matching(any(Query.class));
        verify(terminatingSelect, times(1)).one();
    }

    @Test
    void testFindById_Error() {
        // Mock the complete chain
        ReactiveSelectOperation.ReactiveSelect<TournamentEntity> reactiveSelect = mock(ReactiveSelectOperation.ReactiveSelect.class);
        ReactiveSelectOperation.TerminatingSelect<TournamentEntity> terminatingSelect = mock(ReactiveSelectOperation.TerminatingSelect.class);

        when(r2dbcEntityTemplate.select(TournamentEntity.class)).thenReturn(reactiveSelect);
        when(reactiveSelect.matching(any(Query.class))).thenReturn(terminatingSelect);
        when(terminatingSelect.one()).thenReturn(Mono.error(new RuntimeException("Database error")));

        StepVerifier.create(adapter.findById(1))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database error"))
                .verify();

        verify(r2dbcEntityTemplate, times(1)).select(TournamentEntity.class);
        verify(reactiveSelect, times(1)).matching(any(Query.class));
        verify(terminatingSelect, times(1)).one();
    }

}
