package co.com.bancolombia.model.tournamentstage.gateways;

import co.com.bancolombia.model.tournamentstage.TournamentStage;
import reactor.core.publisher.Flux;

public interface TournamentStageRepository {

    Flux<TournamentStage> findByIdTournament(Integer idTournament);

}
