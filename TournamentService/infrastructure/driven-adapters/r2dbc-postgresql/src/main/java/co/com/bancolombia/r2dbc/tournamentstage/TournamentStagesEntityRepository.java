package co.com.bancolombia.r2dbc.tournamentstage;

import co.com.bancolombia.r2dbc.entities.TournamentStageEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TournamentStagesEntityRepository extends ReactiveCrudRepository<TournamentStageEntity, Long> {
}
