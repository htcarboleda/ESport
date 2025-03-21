package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.TournamentCreateRequest;
import co.com.bancolombia.api.dto.request.TournamentsFilterRequest;
import co.com.bancolombia.api.dto.response.TournamentDTO;
import co.com.bancolombia.api.dto.response.TournamentListResponse;
import co.com.bancolombia.api.dto.validator.ObjectValidator;
import co.com.bancolombia.model.tournament.Tournament;
import co.com.bancolombia.usecase.tournament.CreateTournamentUseCase;
import co.com.bancolombia.usecase.tournament.GetTournamentsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class HandlerTest {

    private GetTournamentsUseCase getTournamentsUseCase;
    private CreateTournamentUseCase createTournamentUseCase;
    private Handler handler;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        getTournamentsUseCase = mock(GetTournamentsUseCase.class);
        createTournamentUseCase = mock(CreateTournamentUseCase.class);
        handler = new Handler(getTournamentsUseCase, createTournamentUseCase, mock(ObjectValidator.class));

        webTestClient = WebTestClient.bindToRouterFunction(new RouterRest().routerFunction(handler)).build();
    }

    @Test
    void testGetTournaments_Success() {
        // Datos de prueba
        TournamentsFilterRequest request = new TournamentsFilterRequest(1, 5, 1, 2, true);
        Tournament tournament = Tournament.builder().id(1).name("Tournament 1").build();

        when(getTournamentsUseCase.findAll(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean()))
                .thenReturn(Flux.just(tournament));

        webTestClient.method(org.springframework.http.HttpMethod.GET)
                .uri("/api/v1/tournaments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TournamentListResponse.class)
                .value(response -> {
                    assert response.getTotalItems() == 1;
                });

        verify(getTournamentsUseCase, times(1)).findAll(anyInt(), anyInt(), anyInt(), anyInt(), anyBoolean());
    }

    @Test
    void testCreateTournament_Success() {
        TournamentCreateRequest request = new TournamentCreateRequest();
        request.setName("New Tournament");

        Tournament tournament = Tournament.builder().id(1).name("New Tournament").build();

        when(createTournamentUseCase.create(any())).thenReturn(Mono.just(tournament));

        webTestClient.post()
                .uri("/api/v1/tournaments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TournamentDTO.class)
                .value(response -> {
                    assert response.getId() == 1;
                });

        verify(createTournamentUseCase, times(1)).create(any());
    }

    @Test
    void testCreateTournament_Failure() {
        TournamentCreateRequest request = new TournamentCreateRequest();
        when(createTournamentUseCase.create(any())).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/api/v1/tournaments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().is5xxServerError();

        verify(createTournamentUseCase, times(1)).create(any());
    }

    @Test
    void testGetTournamentById_Success() {
        Tournament tournament = Tournament.builder().id(1).name("Tournament 1").build();

        when(getTournamentsUseCase.findById(1)).thenReturn(Mono.just(tournament));

        webTestClient.get()
                .uri("/api/v1/tournaments/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(TournamentDTO.class)
                .value(response -> {
                  assert response.getId() == 1;
                });

        verify(getTournamentsUseCase, times(1)).findById(1);
    }

    @Test
    void testGetTournamentById_NotFound() {
        when(getTournamentsUseCase.findById(1)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/tournaments/1")
                .exchange()
                .expectStatus().isNotFound();

        verify(getTournamentsUseCase, times(1)).findById(1);
    }

    @Test
    void testGetTournamentById_Error() {
        when(getTournamentsUseCase.findById(anyInt()))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        webTestClient.get()
                .uri("/api/v1/tournaments/1")
                .exchange()
                .expectStatus().is5xxServerError();

        verify(getTournamentsUseCase, times(1)).findById(anyInt());
    }
}
