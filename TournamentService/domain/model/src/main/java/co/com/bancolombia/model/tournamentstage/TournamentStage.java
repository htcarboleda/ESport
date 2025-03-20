package co.com.bancolombia.model.tournamentstage;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;


@Data
//@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TournamentStage {

    private Integer id;
    private Integer tournamentId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal participantPrice;
    private BigDecimal spectatorPrice;
    private Integer maxParticipantTickets;
    private Integer maxSpectatorTickets;
    private Integer freeParticipantSlots;
    private Integer paidParticipantSlots;
    private Integer freeSpectatorSlots;
    private Integer paidSpectatorSlots;
    private Timestamp createdAt;
    private Timestamp updatedAt;

}
