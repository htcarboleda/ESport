package co.com.bancolombia.r2dbc.ticketInventory;

import co.com.bancolombia.r2dbc.entities.TicketsInventoryEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TicketInventoryEntityRepository extends ReactiveCrudRepository<TicketsInventoryEntity, Long> {

}
