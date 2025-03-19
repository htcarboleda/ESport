package co.com.bancolombia.api.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TournamentsFilterRequest {

    @Size(min = 1)
    private  Integer page=0;

    @Size(min = 1, max = 100)
    private  Integer size=20;

    private Integer category;
    private Integer gameType;
    private Boolean isFree;


}
