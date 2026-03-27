package co.com.bancolombia.api;

import co.com.bancolombia.api.Util.BodyFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.MethodNotAllowedException;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    private final BodyFilter bodyFilter;

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        RouterFunction<ServerResponse> readRoutes =
                route(GET("/api/v1/tournaments"), handler::getTournaments)
                        .andRoute(GET("/api/v1/tournaments/{id}"), handler::getTournamentById);

        RouterFunction<ServerResponse> writeRoutes =
                route(POST("/api/v1/tournaments"), handler::createTournament)
                        .filter(bodyFilter);

        return readRoutes
                .and(writeRoutes)
                .andRoute(RequestPredicates.all(), request ->
                        Mono.error(new MethodNotAllowedException(
                                request.method(), List.of(HttpMethod.GET, HttpMethod.POST)
                        ))
                );
    }
}
