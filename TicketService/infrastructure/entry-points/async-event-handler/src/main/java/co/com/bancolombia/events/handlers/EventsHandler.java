package co.com.bancolombia.events.handlers;

import co.com.bancolombia.usecase.ticket.TicketUseCase;
import lombok.AllArgsConstructor;
import org.reactivecommons.api.domain.DomainEvent;
import org.reactivecommons.async.impl.config.annotations.EnableEventListeners;
import reactor.core.publisher.Mono;


@AllArgsConstructor
@EnableEventListeners
public class EventsHandler {

    private final TicketUseCase ticketUseCase;

    public Mono<Void> handleEventA(DomainEvent<Object> event) {
        return ticketUseCase.createTicketsInventory((Integer) event.getData());
    }

}
