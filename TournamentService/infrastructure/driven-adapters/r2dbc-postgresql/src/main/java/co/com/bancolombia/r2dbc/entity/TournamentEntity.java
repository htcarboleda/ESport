package co.com.bancolombia.r2dbc.entity;

import co.com.bancolombia.model.enums.TournamentFormat;
import co.com.bancolombia.model.enums.TournamentStatus;
import co.com.bancolombia.model.gametype.GameType;
import co.com.bancolombia.model.category.Category;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.tournament.Tournament;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table("tournaments")
public class TournamentEntity {

    @Id
    @Column("id_tournament")
    private Integer id;

    @Column("fk_id_category")
    private Integer category;

    @Column("fk_id_game_type")
    private Integer gameType;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("start_date")
    private LocalDate startDate;

    @Column("end_date")
    private LocalDate endDate;

    @Column("format")
    private TournamentFormat format;

    @Column("is_free")
    private Boolean isFree;

    @Column("status")
    private TournamentStatus status;

    @Column("fk_id_creator")
    private Integer creatorId;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;


    public Tournament toDomain(CategoryEntity categoryEntity, GameTypeEntity gameTypeEntity, UserEntity userEntity) {
        return Tournament.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .format(this.format)
                .isFree(this.isFree)
                .status(this.status)
                .category(new Category(categoryEntity.getId(), categoryEntity.getDescription()))
                .gameType(new GameType(gameTypeEntity.getId(), gameTypeEntity.getName()))
                .creator(new User(userEntity.getId(), userEntity.getFullName()))
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }




    public static TournamentEntity fromDomain(Tournament tournament) {
        return TournamentEntity.builder()
                .id(tournament.getId())
                .category(tournament.getCategoryId())
                .gameType(tournament.getGameTypeId())
                .name(tournament.getName())
                .description(tournament.getDescription())
                .startDate(tournament.getStartDate())
                .endDate(tournament.getEndDate())
                .format(tournament.getFormat())
                .isFree(tournament.getIsFree())
                .status(tournament.getStatus())
                .creatorId(tournament.getCreatorId())
                .createdAt(tournament.getCreatedAt())
                .updatedAt(tournament.getUpdatedAt())
                .build();
    }


}
