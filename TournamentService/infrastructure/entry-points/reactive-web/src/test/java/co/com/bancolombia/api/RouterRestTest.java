package co.com.bancolombia.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.mockito.Mockito.*;

class RouterRestTest {

    private WebTestClient webTestClient;
    private Handler handler;

    @BeforeEach
    void setUp() {
        handler = mock(Handler.class);

        RouterRest routerRest = new RouterRest();
        RouterFunction<ServerResponse> routerFunction = routerRest.routerFunction(handler);

        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    void testGetTournaments() {
        when(handler.getTournaments(any())).thenReturn(ServerResponse.ok().bodyValue("List of tournaments"));

        webTestClient.get()
                .uri("/api/v1/tournaments")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("List of tournaments");

        verify(handler, times(1)).getTournaments(any());
    }

    @Test
    void testCreateTournament() {
        when(handler.createTournament(any())).thenReturn(ServerResponse.ok().bodyValue("Tournament Created"));

        webTestClient.post()
                .uri("/api/v1/tournaments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}") // Simula una solicitud vacía
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Tournament Created");

        verify(handler, times(1)).createTournament(any());
    }

    @Test
    void testGetTournamentById() {
        when(handler.getTournamentById(any())).thenReturn(ServerResponse.ok().bodyValue("Tournament Data"));

        webTestClient.get()
                .uri("/api/v1/tournaments/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Tournament Data");

        verify(handler, times(1)).getTournamentById(any());
    }

    @Test
    void testInvalidRoute() {
        webTestClient.put() // Método no permitido en esta ruta
                .uri("/api/v1/tournaments")
                .exchange()
                .expectStatus().is4xxClientError();
    }
}
