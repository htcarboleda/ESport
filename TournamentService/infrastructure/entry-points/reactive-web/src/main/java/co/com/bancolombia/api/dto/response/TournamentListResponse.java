package co.com.bancolombia.api.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TournamentListResponse {

    private List<TournamentDTO> items;
    private Integer totalItems;
    private Integer totalPages;
    private Integer currentPage;
}
