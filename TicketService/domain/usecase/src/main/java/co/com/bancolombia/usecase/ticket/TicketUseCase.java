package co.com.bancolombia.usecase.ticket;

import co.com.bancolombia.model.audit.gateway.AuditGateway;
import co.com.bancolombia.model.ticketsinventory.TicketInventory;
import co.com.bancolombia.model.tournamentstage.TournamentStage;
import co.com.bancolombia.model.tournamentstage.gateways.TournamentStageRepository;
import lombok.RequiredArgsConstructor;
import co.com.bancolombia.model.ticketsinventory.gateways.TicketInventoryRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@RequiredArgsConstructor
public class TicketUseCase {

    private static final Logger logger = Logger.getLogger(TicketUseCase.class.getName());

    private final TicketInventoryRepository ticketInventoryRepository;
    private final TournamentStageRepository tournamentStageRepository;
    private final AuditGateway auditTrail;

    public Mono<Void> createTicketsInventory(Integer idTournament) {
        return tournamentStageRepository.findByIdTournament(idTournament)
                .flatMap(this::createInventoryForStage)
                .then();
    }

    private Mono<Void> createInventoryForStage(TournamentStage stage) {
        return buildTicketDefinitions(stage)
                .flatMap(ticket -> ticketInventoryRepository.save(ticket)
                        .doOnSuccess(saved -> logger.info(
                                "[INVENTORY] Ticket creado type= "+ saved.getTicketType()
                                        +"stageId= " + stage.getId()))
                        .doOnError(e -> logger.warning(
                                "[INVENTORY] Error creando ticket type= "+ ticket.getTicketType()
                                        +" stageId= "+ stage.getId() +" : "+ e.getMessage())))
                .flatMap(saved -> auditTrail.record("Created_bd_tickets_inventory",
                                saved.toString())
                        .thenReturn(saved))
                .then();
    }

    // Construccion de Tickets
    private Flux<TicketInventory> buildTicketDefinitions(TournamentStage stage) {
        return Flux.fromIterable(resolveTicketSlots(stage));
    }

    private List<TicketInventory> resolveTicketSlots(TournamentStage stage) {
        List<TicketInventory> tickets = new ArrayList<>();

        addIfPositive(tickets, stage, "PARTICIPANT", stage.getPaidParticipantSlots(), stage.getParticipantPrice());
        addIfPositive(tickets, stage, "PARTICIPANT", stage.getFreeParticipantSlots(), 0.0);
        addIfPositive(tickets, stage, "SPECTATOR",   stage.getPaidSpectatorSlots(),   stage.getSpectatorPrice());
        addIfPositive(tickets, stage, "SPECTATOR",   stage.getFreeSpectatorSlots(),   0.0);

        return tickets;
    }

    private void addIfPositive(List<TicketInventory> tickets, TournamentStage stage,
                               String type, int slots, double price) {
        if (slots <= 0) return;

        tickets.add(TicketInventory.builder()
                .tournamentId(stage.getTournamentId())
                .stageId(stage.getId())
                .ticketType(type)
                .totalQuantity(slots)
                .availableQuantity(0)
                .reservedQuantity(0)
                .soldQuantity(0)
                .basePrice(price)
                .version(1)
                .build());
    }

}
