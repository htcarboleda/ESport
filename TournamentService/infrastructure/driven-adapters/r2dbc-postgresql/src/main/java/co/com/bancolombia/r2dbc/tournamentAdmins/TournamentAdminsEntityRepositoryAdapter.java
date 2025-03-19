package co.com.bancolombia.r2dbc.tournamentAdmins;

import co.com.bancolombia.model.tournament.Tournament;
import co.com.bancolombia.model.tournament.gateways.TournamentRepository;
import co.com.bancolombia.model.tournamentadmin.TournamentAdmin;
import co.com.bancolombia.model.tournamentadmin.gateways.TournamentAdminRepository;
import co.com.bancolombia.r2dbc.entity.CategoryEntity;
import co.com.bancolombia.r2dbc.entity.GameTypeEntity;
import co.com.bancolombia.r2dbc.entity.TournamentAdminsEntity;
import co.com.bancolombia.r2dbc.entity.TournamentEntity;
import co.com.bancolombia.r2dbc.entity.UserEntity;
import co.com.bancolombia.r2dbc.tournament.TournamentEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class TournamentAdminsEntityRepositoryAdapter  implements TournamentAdminRepository {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final TournamentAdminsEntityRepository tournamentAdminsEntityRepository;


    public Mono<TournamentAdmin> save(TournamentAdmin tournamentAdmin) {

        log.info("*******************************************************Saving tournamentAdmin: {}", tournamentAdmin.toString());


        return tournamentAdminsEntityRepository.save(TournamentAdminsEntity.fromDomain(tournamentAdmin))
                .doOnNext(tournamentAdminsEntity -> log.info("TournamentAdmin saved: {}", tournamentAdminsEntity))
                .flatMap(tournamentAdminEntity -> {
                    Mono<UserEntity> userEntityMono = r2dbcEntityTemplate.selectOne(
                            Query.query(Criteria.where("id_user").is(tournamentAdminEntity.getUserId())), UserEntity.class);

                    return userEntityMono
                            .map(userEntity -> tournamentAdminEntity.toDomain(userEntity))
                            .switchIfEmpty(Mono.just(tournamentAdminEntity.toDomain(null)));
                });
    }



}