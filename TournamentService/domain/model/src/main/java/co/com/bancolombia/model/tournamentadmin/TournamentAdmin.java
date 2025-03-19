package co.com.bancolombia.model.tournamentadmin;
import co.com.bancolombia.model.enums.AdminRole;
import co.com.bancolombia.model.user.User;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
//import lombok.NoArgsConstructor;


@Data
//@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TournamentAdmin {

    private Integer id;
    private Integer tournamentId;
    private Integer userId;
    private AdminRole role;
    private Boolean isActive;

    private User user;

}
