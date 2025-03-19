package co.com.bancolombia.r2dbc.gameType;

import co.com.bancolombia.r2dbc.entity.GameTypeEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface GameTypeEntityRepository extends ReactiveCrudRepository<GameTypeEntity, Long> {
}
