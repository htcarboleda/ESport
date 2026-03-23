package co.com.bancolombia.r2dbc.ticketInventory;

import co.com.bancolombia.r2dbc.entities.TicketsInventoryEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TicketInventoryEntityRepository extends ReactiveCrudRepository<TicketsInventoryEntity, Long> {
    // Spring Data R2DBC genera la query automáticamente por convención de nombre
    Mono<Boolean> existsByStageId(Integer stageId);


}
