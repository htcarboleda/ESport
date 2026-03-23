package co.com.bancolombia.api.dto.response;
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
