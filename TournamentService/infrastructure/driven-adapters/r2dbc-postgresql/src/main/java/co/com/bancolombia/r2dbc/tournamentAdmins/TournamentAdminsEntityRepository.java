package co.com.bancolombia.r2dbc.tournamentAdmins;

import co.com.bancolombia.r2dbc.entities.TournamentAdminsEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TournamentAdminsEntityRepository extends ReactiveCrudRepository<TournamentAdminsEntity, Long> {
}
