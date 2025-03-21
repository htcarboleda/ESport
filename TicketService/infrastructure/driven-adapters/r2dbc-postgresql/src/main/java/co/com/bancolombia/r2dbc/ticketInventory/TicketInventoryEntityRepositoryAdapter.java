package co.com.bancolombia.r2dbc.ticketInventory;

import co.com.bancolombia.model.ticketsinventory.TicketInventory;
import co.com.bancolombia.model.ticketsinventory.gateways.TicketInventoryRepository;
import co.com.bancolombia.r2dbc.entities.TicketsInventoryEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketInventoryEntityRepositoryAdapter implements TicketInventoryRepository {

    private final TicketInventoryEntityRepository ticketInventoryEntityRepository;

    @Override
    public Mono<TicketInventory> save(TicketInventory inventarioTicket) {

        return ticketInventoryEntityRepository.save(TicketsInventoryEntity.fromDomain(inventarioTicket))
                .map(TicketsInventoryEntity::toDomain)
                .onErrorResume(e -> {
                    log.error("Error saving ticket inventory: {}", e.getMessage(), e);
                    return Mono.empty(); // Return empty instead of null
                });

    }
}
