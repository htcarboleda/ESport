package co.com.bancolombia.events;

import co.com.bancolombia.events.commons.Constants;
import co.com.bancolombia.model.events.gateways.EventsGateway;
import co.com.bancolombia.model.tournament.Tournament;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.reactivecommons.api.domain.DomainEvent;
import org.reactivecommons.api.domain.DomainEventBus;
import org.reactivecommons.async.impl.config.annotations.EnableDomainEventBus;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.logging.Level;

import static reactor.core.publisher.Mono.from;

@Log
@Profile("local")
@RequiredArgsConstructor
@EnableDomainEventBus
public class ReactiveEventsGateway implements EventsGateway {

    private final DomainEventBus domainEventBus;

    public Mono<Void> emit(Tournament tournament) {
        log.log(Level.INFO, "[KAFKA] Enviando Evento: {0}: {1}", new String[]{Constants.SOME_EVENT_NAME, tournament.getId().toString()});

         return from(domainEventBus.emit(
                 new DomainEvent<>(Constants.SOME_EVENT_NAME, UUID.randomUUID().toString(), tournament.getId().toString())
         ));
    }


}
