package co.com.bancolombia.events.dto.createTournament;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTournamentEvent {

    private String name;
    private String eventId;
    private DataTournament data;

}