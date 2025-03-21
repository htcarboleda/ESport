package co.com.bancolombia.model.tournamentstage;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
//@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TournamentStage {

    private Integer id;
    private Integer tournamentId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double participantPrice;
    private Double spectatorPrice;
    private Integer maxParticipantTickets;
    private Integer maxSpectatorTickets;
    private Integer freeParticipantSlots;
    private Integer paidParticipantSlots;
    private Integer freeSpectatorSlots;
    private Integer paidSpectatorSlots;
}
