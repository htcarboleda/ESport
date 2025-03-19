package co.com.bancolombia.model.gametype;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class GameType {

    private Integer id;
    private String code;
    private String name;
    private Integer maxPlayers;

    public GameType(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
