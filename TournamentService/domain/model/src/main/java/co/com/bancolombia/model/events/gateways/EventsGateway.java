package co.com.bancolombia.model.events.gateways;

import co.com.bancolombia.model.tournament.Tournament;
import reactor.core.publisher.Mono;

public interface EventsGateway {
    Mono<Void> emit(Tournament tournament);
}
