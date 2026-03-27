package co.com.bancolombia.model.gametype.gateways;

import reactor.core.publisher.Mono;

public interface GameTypesCacheGateway {
    Mono<Boolean> getExistence(Integer id);
    Mono<Void>    putExistence(Integer id, boolean exists);
    Mono<Void>    evict(Integer id);
}
