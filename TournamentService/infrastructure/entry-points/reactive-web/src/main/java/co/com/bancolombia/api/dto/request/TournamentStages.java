package co.com.bancolombia.api.dto.request;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentStages {

    private Integer tournamentId;
    private Integer stageId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 5, max = 255, message = "El nombre debe tener entre 5 y 255 caracteres")
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "La fecha de finalizacion es obligatoria")
    private LocalDate endDate;

    @NotNull(message = "El precio de tickets para participantes es obligatorio")
    private BigDecimal participantPrice;

    @NotNull(message = "El precio de tickets para espectadores es obligatorio")
    private BigDecimal spectatorPrice;

    @NotNull(message = "Definir el máximo de tickets para participantes es obligatorio")
    private Integer maxParticipantTickets;

    @NotNull(message = "Definir el máximo de tickets para espectadores es obligatorio")
    private Integer maxSpectatorTickets;

    @NotNull(message = "Definir el numero máximo de participantes gratis es obligatorio")
    private Integer freeParticipantSlots;

    @NotNull(message = "Definir el numero máximo de participantes pago es obligatorio")
    private Integer paidParticipantSlots;

    @NotNull(message = "Definir el numero máximo de espectadores gratis es obligatorio")
    private Integer freeSpectatorSlots;

    @NotNull(message = "Definir el numero máximo de espectadores pago es obligatorio")
    private Integer paidSpectatorSlots;


    @AssertTrue(message = "La fecha de fin debe ser igual o posterior a la fecha de inicio")
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return !endDate.isBefore(startDate);
    }

}

