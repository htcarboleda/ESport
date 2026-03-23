package co.com.bancolombia.r2dbc.entities;

import co.com.bancolombia.model.enums.AdminRole;
import co.com.bancolombia.model.tournamentadmin.TournamentAdmin;
import co.com.bancolombia.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("tournament_admins")
public class TournamentAdminsEntity {

    @Id
    @Column("id_tournament_admin")
    private Integer id;

    @Column("fk_id_tournament")
    private Integer tournamentId;

    @Column("fk_id_user")
    private Integer userId;

    @Column("role")
    private AdminRole role;

    @Column("is_active")
    private Boolean isActive;


    public static TournamentAdminsEntity fromDomain(TournamentAdmin tournamentAdmin) {
        return TournamentAdminsEntity.builder()
                .id(tournamentAdmin.getId())
                .tournamentId(tournamentAdmin.getTournamentId())
                .userId(tournamentAdmin.getUserId())
                .role(tournamentAdmin.getRole())
                .isActive(tournamentAdmin.getIsActive())
                .build();
    }

    public TournamentAdmin toDomain(UserEntity userEntity) {
        return TournamentAdmin.builder()
                .id(this.getId())
                .tournamentId(this.getTournamentId())
                .user(new User(userEntity.getId(), userEntity.getFullName()))
                .role(this.getRole())
                .isActive(this.getIsActive())
                .build();
    }


}
