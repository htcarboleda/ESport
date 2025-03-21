package co.com.bancolombia.model.ticketsinventory.gateways;

import co.com.bancolombia.model.ticketsinventory.TicketInventory;
import reactor.core.publisher.Mono;

public interface TicketInventoryRepository {

    Mono<TicketInventory> save(TicketInventory inventarioTicket);

}