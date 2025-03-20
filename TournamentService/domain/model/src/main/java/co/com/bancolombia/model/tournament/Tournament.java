package co.com.bancolombia.model.tournament;
import co.com.bancolombia.model.enums.TournamentFormat;
import co.com.bancolombia.model.enums.TournamentStatus;
import co.com.bancolombia.model.category.Category;
import co.com.bancolombia.model.gametype.GameType;
import co.com.bancolombia.model.tournamentadmin.TournamentAdmin;
import co.com.bancolombia.model.tournamentstage.TournamentStage;
import co.com.bancolombia.model.user.User;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Tournament {

    private Integer id;
    private String name;
    private String description;
    private Integer categoryId;
    private Integer gameTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private TournamentFormat format;
    private Boolean isFree;
    private TournamentStatus status;
    private Integer creatorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Category category;
    private GameType gameType;
    private User creator;

    @Builder.Default
    private List<TournamentAdmin> admins = new ArrayList<>();

    @Builder.Default
    private List<TournamentStage> stages = new ArrayList<>();
}
