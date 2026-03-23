package co.com.bancolombia.model.ticketsinventory.gateways;

import reactor.core.publisher.Mono;

public class TicketInventoryEntityRepository
    extends ReactiveCrudRepository<TicketsInventoryEntity, Long> {
        // Spring Data R2DBC genera la query automáticamente por convención de nombre
        Mono<Boolean> existsByStageId(Integer stageId);

    }
