package co.com.bancolombia.api.dto.request;
import co.com.bancolombia.model.enums.TournamentFormat;
import co.com.bancolombia.model.enums.TournamentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentCreateRequest {

    @NotNull(message = "La categoría es obligatoria")
    private Integer category;

    @NotNull(message = "El tipo de juego es obligatorio")
    private Integer gameType;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 5, max = 100, message = "El nombre debe tener entre 5 y 100 caracteres")
    private String name;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate endDate;

    @NotNull(message = "El formato es obligatorio")
    private TournamentFormat format;

    @NotNull(message = "El campo isFree es obligatorio")
    private Boolean isFree;

    @NotNull(message = "El estado del torneo es obligatorio")
    private TournamentStatus status;

    // Lista de administradores
    private List<TournamentAdminRequest> additionalAdmins;

    // Lista de etapas
    private List<TournamentStages> stages;

    @AssertTrue(message = "La fecha de fin debe ser igual o posterior a la fecha de inicio")
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return !endDate.isBefore(startDate);
    }

    @AssertTrue(message = "Si el torneo es publicado, debe tener al menos una etapa")
    public boolean isValidStages() {
        if(TournamentStatus.PUBLICADO.equals(status)){
            return stages != null && !stages.isEmpty();
        } else {
            return true;
        }
    }
}