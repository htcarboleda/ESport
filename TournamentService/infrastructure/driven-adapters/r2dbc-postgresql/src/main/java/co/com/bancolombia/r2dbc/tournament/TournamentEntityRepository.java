package co.com.bancolombia.r2dbc.tournament;

import co.com.bancolombia.r2dbc.entities.TournamentEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TournamentEntityRepository extends ReactiveCrudRepository<TournamentEntity, Long> {
}
