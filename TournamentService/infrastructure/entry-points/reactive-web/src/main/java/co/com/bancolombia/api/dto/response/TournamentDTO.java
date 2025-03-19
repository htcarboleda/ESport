package co.com.bancolombia.api.dto.response;

import co.com.bancolombia.model.category.Category;
import co.com.bancolombia.model.enums.TournamentFormat;
import co.com.bancolombia.model.enums.TournamentStatus;
import co.com.bancolombia.model.gametype.GameType;
import co.com.bancolombia.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TournamentDTO {

    private Integer id;
    private String name;
    private String description;
    private String category;
    private String gameType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String format;
    private Boolean isFree;
    private String creator;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
