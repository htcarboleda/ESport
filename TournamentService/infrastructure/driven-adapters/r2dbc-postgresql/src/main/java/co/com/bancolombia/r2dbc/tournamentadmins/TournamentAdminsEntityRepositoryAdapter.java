package co.com.bancolombia.r2dbc.tournamentadmins;

import co.com.bancolombia.model.tournamentadmin.TournamentAdmin;
import co.com.bancolombia.model.tournamentadmin.gateways.TournamentAdminRepository;
import co.com.bancolombia.r2dbc.entities.TournamentAdminsEntity;
import co.com.bancolombia.r2dbc.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class TournamentAdminsEntityRepositoryAdapter  implements TournamentAdminRepository {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final TournamentAdminsEntityRepository tournamentAdminsEntityRepository;

    public Mono<TournamentAdmin> save(TournamentAdmin tournamentAdmin) {
        return tournamentAdminsEntityRepository.save(TournamentAdminsEntity.fromDomain(tournamentAdmin))
                .flatMap(tournamentAdminEntity -> {
                    Mono<UserEntity> userEntityMono = r2dbcEntityTemplate.selectOne(
                            Query.query(Criteria.where("id_user").is(tournamentAdminEntity.getUserId())), UserEntity.class);

                    return userEntityMono
                            .map(tournamentAdminEntity::toDomain)
                            ;
                });
    }



}