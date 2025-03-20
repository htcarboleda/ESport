package co.com.bancolombia.model.tournamentstage.gateways;

import co.com.bancolombia.model.tournamentstage.TournamentStage;
import reactor.core.publisher.Mono;

public interface TournamentStageRepository {

    Mono<TournamentStage> save(TournamentStage tournamentStage);

}
