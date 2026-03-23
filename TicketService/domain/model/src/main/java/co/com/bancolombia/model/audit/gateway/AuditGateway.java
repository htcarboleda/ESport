package co.com.bancolombia.model.audit.gateway;

import reactor.core.publisher.Mono;

public interface AuditGateway {
    Mono<Void> record(String eventType, Integer tournamentId,
                      String detail);
}
