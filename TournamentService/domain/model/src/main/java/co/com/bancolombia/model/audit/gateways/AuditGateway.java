package co.com.bancolombia.model.audit.gateways;

import reactor.core.publisher.Mono;

public interface AuditGateway {
    Mono<Void> record(String eventType,  String detail);
}