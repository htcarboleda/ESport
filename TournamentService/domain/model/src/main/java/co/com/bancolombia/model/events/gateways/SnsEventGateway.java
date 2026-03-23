package co.com.bancolombia.model.events.gateways;

import co.com.bancolombia.model.tournament.Tournament;
import reactor.core.publisher.Mono;

public interface SnsEventGateway {
    Mono<Void> publishTournamentCreated(Tournament tournament);
    Mono<Void> publishTournamentStatusChanged(Integer tournamentId, String newStatus);

}
