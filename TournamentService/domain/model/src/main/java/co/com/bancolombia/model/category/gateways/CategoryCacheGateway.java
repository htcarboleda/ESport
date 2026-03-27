package co.com.bancolombia.model.category.gateways;

import reactor.core.publisher.Mono;

public interface CategoryCacheGateway {
    Mono<Boolean> getExistence(Integer id);
    Mono<Void>    putExistence(Integer id, boolean exists);
    Mono<Void>    evict(Integer id);
}
