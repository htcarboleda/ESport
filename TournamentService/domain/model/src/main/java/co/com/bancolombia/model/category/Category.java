package co.com.bancolombia.model.category;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Category {
    private Integer id;
    private String code;
    private String alias;
    private String description;
    private Integer maxFreeParticipants;

    public Category(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

}
