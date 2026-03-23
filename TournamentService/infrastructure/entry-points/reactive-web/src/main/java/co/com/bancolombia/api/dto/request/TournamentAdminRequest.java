package co.com.bancolombia.api.dto.request;
import co.com.bancolombia.model.enums.AdminRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentAdminRequest {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Integer userId;

    @NotNull(message = "El rol es obligatorio")
    private AdminRole role;
}