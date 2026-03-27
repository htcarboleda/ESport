package co.com.bancolombia.api.Util;

import co.com.bancolombia.model.exceptions.SecurityException;
import co.com.bancolombia.model.exceptions.message.SecurityErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class BodyFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {

        return request.bodyToMono(String.class)
                .switchIfEmpty(Mono.error(new SecurityException(SecurityErrorMessage.INVALID_BODY)))
                .onErrorResume(ex -> {
                    if (ex instanceof SecurityException) {
                        return Mono.error(ex);
                    }
                    log.warn("[BodyFilter] Error al leer el body: {}", ex.getMessage());
                    return Mono.error(new SecurityException(SecurityErrorMessage.INVALID_REQUEST));
                })
                .flatMap(rawBody -> {
                    if (rawBody.isBlank()) {
                        return Mono.error(new SecurityException(SecurityErrorMessage.INVALID_BODY));
                    }
                    ServerRequest rebuiltRequest = ServerRequest.from(request)
                            .body(rawBody)
                            .build();
                    return next.handle(rebuiltRequest);
                });
    }
}
