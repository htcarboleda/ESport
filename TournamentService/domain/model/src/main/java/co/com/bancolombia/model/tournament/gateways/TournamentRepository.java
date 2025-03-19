package co.com.bancolombia.model.tournament.gateways;

import co.com.bancolombia.model.enums.TournamentStatus;
import co.com.bancolombia.model.tournament.Tournament;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TournamentRepository {

    Flux<Tournament> findAllPaged(int page, int size, Integer category, Integer gameType, Boolean isFree);

    Mono<Tournament> save(Tournament tournament);

    Mono<Tournament> findById(Integer id);
}
