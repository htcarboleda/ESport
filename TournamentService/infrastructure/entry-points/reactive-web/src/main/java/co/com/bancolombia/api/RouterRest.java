package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.validator.RequestRoutes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.MethodNotAllowedException;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET("/api/v1/tournaments"), handler::getTournaments)
                .andRoute(POST("/api/v1/tournaments"), handler::createTournament)
                .andRoute(GET("/api/v1/tournaments/{id}"), handler::getTournamentById)
               // .and(route(GET(" /api/v1/tournaments/{id}:"), handler::getTournamentById))
                .andRoute(
                        RequestPredicates.all(),
                        request -> filterRequests(request, handler::createTournament)
                );
    }

    private Mono<ServerResponse> filterRequests(ServerRequest request, HandlerFunction<ServerResponse> next) {
        if (!RequestRoutes.isValidRoute(request.path(), request.method().name())) {
            return Mono.error(() -> new MethodNotAllowedException(request.method(), List.of()));
        }
        return next.handle(request);
    }
}
