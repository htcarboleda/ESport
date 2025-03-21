package co.com.bancolombia.usecase.ticket;

import co.com.bancolombia.model.ticketsinventory.TicketInventory;
import co.com.bancolombia.model.tournamentstage.TournamentStage;
import co.com.bancolombia.model.tournamentstage.gateways.TournamentStageRepository;
import lombok.RequiredArgsConstructor;
import co.com.bancolombia.model.ticketsinventory.gateways.TicketInventoryRepository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.logging.Logger;


@RequiredArgsConstructor
public class TicketUseCase {

    private static final Logger logger = Logger.getLogger(TicketUseCase.class.getName());

    private final TicketInventoryRepository ticketInventoryRepository;
    private final TournamentStageRepository tournamentStageRepository;

    public Mono<Void> createTicketsInventory(Integer idTournament) {

        return tournamentStageRepository.findByIdTournament(idTournament)
                .flatMap(this::createInventoryTicketsForStage)
                .then();
    }

    // Crear tipos de inventario para cada etapa
    private Mono<Void> createInventoryTicketsForStage(TournamentStage stage) {

        List<Mono<TicketInventory>> inventoryMonos = createInventoryTickets(stage);

        // Ejecutar todas las operaciones en paralelo
        return Mono.when(inventoryMonos);
    }

    private List<Mono<TicketInventory>> createInventoryTickets(TournamentStage stage) {

        List<Mono<TicketInventory>> inventories = new java.util.ArrayList<>();

        // Tickets para participantes pago
        if(stage.getPaidParticipantSlots() > 0) {
            TicketInventory paidParticipant = TicketInventory.builder()
                    .tournamentId(stage.getTournamentId())
                    .stageId(stage.getId())
                    .ticketType("PARTICIPANT")
                    .totalQuantity(stage.getPaidParticipantSlots())
                    .availableQuantity(0)
                    .reservedQuantity(0)
                    .soldQuantity(0)
                    .basePrice(stage.getParticipantPrice())
                    .version(1)
                    .build();
            Mono<TicketInventory> savedMono = ticketInventoryRepository.save(paidParticipant);
            if (savedMono != null) {
                inventories.add(savedMono);
            } else {
                logger.warning("Valor null");
            }
        }

        // Tickets para participantes gratis
        if(stage.getFreeParticipantSlots() > 0) {
            TicketInventory freeParticipant = TicketInventory.builder()
                    .tournamentId(stage.getTournamentId())
                    .stageId(stage.getId())
                    .ticketType("PARTICIPANT")
                    .totalQuantity(stage.getFreeParticipantSlots())
                    .availableQuantity(0)
                    .reservedQuantity(0)
                    .soldQuantity(0)
                    .basePrice(0.0)
                    .version(1)
                    .build();
            Mono<TicketInventory> savedMono = ticketInventoryRepository.save(freeParticipant);
            if (savedMono != null) {
                inventories.add(savedMono);
            } else {
                logger.warning("Valor null");
            }
        }


        // Tickets para espectadores pago
        if(stage.getPaidSpectatorSlots() > 0) {
            TicketInventory paidSpectator = TicketInventory.builder()
                    .tournamentId(stage.getTournamentId())
                    .stageId(stage.getId())
                    .ticketType("SPECTATOR")
                    .totalQuantity(stage.getPaidSpectatorSlots())
                    .availableQuantity(0)
                    .reservedQuantity(0)
                    .soldQuantity(0)
                    .basePrice(stage.getSpectatorPrice())
                    .version(1)
                    .build();
            Mono<TicketInventory> savedMono = ticketInventoryRepository.save(paidSpectator);
            if (savedMono != null) {
                inventories.add(savedMono);
            } else {
                logger.warning("Valor null");
            }
        }

        // Tickets para espectadores gratis
        if(stage.getFreeSpectatorSlots() > 0) {
            TicketInventory freeSpectator = TicketInventory.builder()
                    .tournamentId(stage.getTournamentId())
                    .stageId(stage.getId())
                    .ticketType("SPECTATOR")
                    .totalQuantity(stage.getFreeSpectatorSlots())
                    .availableQuantity(0)
                    .reservedQuantity(0)
                    .soldQuantity(0)
                    .basePrice(0.0)
                    .version(1)
                    .build();
            Mono<TicketInventory> savedMono = ticketInventoryRepository.save(freeSpectator);
            if (savedMono != null) {
                inventories.add(savedMono);
            } else {
                logger.warning("Valor null");
            }
        }

        return inventories;
    }

}
