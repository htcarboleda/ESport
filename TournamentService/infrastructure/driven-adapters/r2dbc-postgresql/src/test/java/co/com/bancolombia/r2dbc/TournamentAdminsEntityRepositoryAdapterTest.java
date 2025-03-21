package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.enums.AdminRole;
import co.com.bancolombia.model.tournamentadmin.TournamentAdmin;
import co.com.bancolombia.r2dbc.entities.TournamentAdminsEntity;
import co.com.bancolombia.r2dbc.entities.UserEntity;
import co.com.bancolombia.r2dbc.tournamentadmins.TournamentAdminsEntityRepository;
import co.com.bancolombia.r2dbc.tournamentadmins.TournamentAdminsEntityRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TournamentAdminsEntityRepositoryAdapterTest {

    @Mock
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @Mock
    private TournamentAdminsEntityRepository tournamentAdminsEntityRepository;

    @InjectMocks
    private TournamentAdminsEntityRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_Success() {
        // Datos de prueba
        TournamentAdmin tournamentAdmin = TournamentAdmin.builder()
                .userId(1)
                .tournamentId(1)
                .role(AdminRole.valueOf("SUBADMINISTRADOR"))
                .isActive(true)
                .build();

        TournamentAdminsEntity tournamentAdminsEntity = TournamentAdminsEntity.builder()
                .userId(1)
                .tournamentId(1)
                .role(AdminRole.valueOf("SUBADMINISTRADOR"))
                .isActive(true)
                .build();

        UserEntity userEntity = new UserEntity(1, "User 3","Email 3","Username 3");

        when(tournamentAdminsEntityRepository.save(any(TournamentAdminsEntity.class)))
                .thenReturn(Mono.just(tournamentAdminsEntity));

        when(r2dbcEntityTemplate.selectOne(any(Query.class), eq(UserEntity.class)))
                .thenReturn(Mono.just(userEntity));

        // Ejecutamos la prueba - update expectation to check the user's ID instead
        StepVerifier.create(adapter.save(tournamentAdmin))
                .expectNextMatches(savedAdmin ->
                        savedAdmin.getUser() != null &&
                                savedAdmin.getUser().getId().equals(1) &&
                                "SUBADMINISTRADOR".equals(savedAdmin.getRole().name()))
                .verifyComplete();

        verify(tournamentAdminsEntityRepository, times(1)).save(any(TournamentAdminsEntity.class));
        verify(r2dbcEntityTemplate, times(1)).selectOne(any(Query.class), eq(UserEntity.class));
    }

    @Test
    void testSave_UserNotFound() {
        // Datos de prueba
        TournamentAdmin tournamentAdmin = TournamentAdmin.builder()
                .userId(1)
                .tournamentId(1)
                .userId(1)
                .role(AdminRole.valueOf("SUBADMINISTRADOR"))
                .isActive(true)
                .build();

        TournamentAdminsEntity tournamentAdminsEntity = TournamentAdminsEntity.fromDomain(tournamentAdmin);

        // Mockeamos la persistencia del torneo pero sin encontrar el usuario
        when(tournamentAdminsEntityRepository.save(any(TournamentAdminsEntity.class)))
                .thenReturn(Mono.just(tournamentAdminsEntity));

        when(r2dbcEntityTemplate.selectOne(any(Query.class), eq(UserEntity.class)))
                .thenReturn(Mono.empty());

        // Ejecutamos la prueba
        StepVerifier.create(adapter.save(tournamentAdmin))
                .expectComplete() // Si no se encuentra el usuario, el flujo termina sin error
                .verify();

        verify(tournamentAdminsEntityRepository, times(1)).save(any(TournamentAdminsEntity.class));
        verify(r2dbcEntityTemplate, times(1)).selectOne(any(Query.class), eq(UserEntity.class));
    }

    @Test
    void testSave_ErrorSavingTournamentAdmin() {
        // Datos de prueba
        TournamentAdmin tournamentAdmin = TournamentAdmin.builder()
                .userId(1)
                .tournamentId(1)
                .userId(1)
                .role(AdminRole.valueOf("SUBADMINISTRADOR"))
                .isActive(true)
                .build();

        // Simulamos un error en la base de datos al intentar guardar el torneo
        when(tournamentAdminsEntityRepository.save(any(TournamentAdminsEntity.class)))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // Ejecutamos la prueba
        StepVerifier.create(adapter.save(tournamentAdmin))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database error"))
                .verify();

        verify(tournamentAdminsEntityRepository, times(1)).save(any(TournamentAdminsEntity.class));
        verify(r2dbcEntityTemplate, times(0)).selectOne(any(Query.class), eq(UserEntity.class)); // No se consulta el usuario porque la persistencia falló
    }

    @Test
    void testSave_ErrorFetchingUser() {
        // Datos de prueba
        TournamentAdmin tournamentAdmin = TournamentAdmin.builder()
                .userId(1)
                .tournamentId(1)
                .userId(1)
                .role(AdminRole.valueOf("SUBADMINISTRADOR"))
                .isActive(true)
                .build();

        TournamentAdminsEntity tournamentAdminsEntity = TournamentAdminsEntity.fromDomain(tournamentAdmin);

        // Simulamos la persistencia correcta del torneo pero un error al buscar el usuario
        when(tournamentAdminsEntityRepository.save(any(TournamentAdminsEntity.class)))
                .thenReturn(Mono.just(tournamentAdminsEntity));

        when(r2dbcEntityTemplate.selectOne(any(Query.class), eq(UserEntity.class)))
                .thenReturn(Mono.error(new RuntimeException("User query error")));

        // Ejecutamos la prueba
        StepVerifier.create(adapter.save(tournamentAdmin))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("User query error"))
                .verify();

        verify(tournamentAdminsEntityRepository, times(1)).save(any(TournamentAdminsEntity.class));
        verify(r2dbcEntityTemplate, times(1)).selectOne(any(Query.class), eq(UserEntity.class));
    }
}
