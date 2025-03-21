package co.com.bancolombia.model.ticketsinventory;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TicketInventory {

    private Integer id;
    private Integer tournamentId;
    private Integer stageId;
    private String ticketType;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Integer soldQuantity;
    private Double basePrice;
    private Integer version;
    private String createdAt;
    private String updatedAt;




}
