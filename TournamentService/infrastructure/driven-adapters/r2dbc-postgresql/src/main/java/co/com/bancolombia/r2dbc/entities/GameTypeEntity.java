package co.com.bancolombia.r2dbc.entities;


import co.com.bancolombia.model.gametype.GameType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("game_types")
public class GameTypeEntity {

    @Id
    @Column("id_game_type")
    private Integer id;

    @Column("code")
    private String code;

    @Column("name")
    private String name;

    @Column("max_players")
    private Integer maxPlayers;



    public static GameTypeEntity fromDomain(GameType gameType) {
        return GameTypeEntity.builder()
                .id(gameType.getId())
                .code(gameType.getCode())
                .name(gameType.getName())
                .maxPlayers(gameType.getMaxPlayers())
                .build();
    }

    public GameType toDomain() {
        return GameType.builder()
                .id(this.getId())
                .code(this.getCode())
                .name(this.getName())
                .maxPlayers(this.getMaxPlayers())
                .build();
    }




}
