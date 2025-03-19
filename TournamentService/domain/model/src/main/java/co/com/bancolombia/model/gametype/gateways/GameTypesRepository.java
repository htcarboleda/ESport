package co.com.bancolombia.model.gametype.gateways;

import co.com.bancolombia.model.gametype.GameType;
import reactor.core.publisher.Mono;

public interface GameTypesRepository {
    Mono<GameType> findById(Integer id);
}
