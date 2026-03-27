package co.com.bancolombia.model.tournament.gateways;

import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface TournamentCacheGateway {
    Mono<Boolean> getExistence(String name, LocalDate startDate, LocalDate endDate);
    Mono<Void>    putExistence(String name, LocalDate startDate, LocalDate endDate, boolean exists);
    Mono<Void>    evict(String name, LocalDate startDate, LocalDate endDate);
}
