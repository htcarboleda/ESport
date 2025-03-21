package co.com.bancolombia.r2dbc.entities;

import co.com.bancolombia.model.ticketsinventory.TicketInventory;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table("tickets_inventory")
public class TicketsInventoryEntity {

    @Id
    @Column("id_tickets_inventory")
    private Integer id;

    @Column("fk_id_tournament")
    private Integer tournamentId;

    @Column("fk_id_stage")
    private Integer stageId;

    @Column("ticket_type")
    private String ticketType;

    @Column("total_quantity")
    private Integer totalQuantity;

    @Column("available_quantity")
    private Integer availableQuantity;

    @Column("reserved_quantity")
    private Integer reservedQuantity;

    @Column("sold_quantity")
    private Integer soldQuantity;

    @Column("base_price")
    private Double basePrice;

    @Column("version")
    private Integer version;

    public static TicketsInventoryEntity fromDomain(TicketInventory model) {
        return TicketsInventoryEntity.builder()
                .id(model.getId())
                .tournamentId(model.getTournamentId())
                .stageId(model.getStageId())
                .ticketType(model.getTicketType())
                .totalQuantity(model.getTotalQuantity())
                .availableQuantity(model.getAvailableQuantity())
                .reservedQuantity(model.getReservedQuantity())
                .soldQuantity(model.getSoldQuantity())
                .basePrice(model.getBasePrice())
                .version(model.getVersion())
                .build();
    }


    public TicketInventory toDomain() {
        return TicketInventory.builder()
                .id(id)
                .tournamentId(tournamentId)
                .stageId(stageId)
                .ticketType(ticketType)
                .totalQuantity(totalQuantity)
                .availableQuantity(availableQuantity)
                .reservedQuantity(reservedQuantity)
                .soldQuantity(soldQuantity)
                .basePrice(basePrice)
                .version(version)
                .build();
    }


}
