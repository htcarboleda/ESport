package co.com.bancolombia.r2dbc.tournament;

import co.com.bancolombia.model.tournament.Tournament;
import co.com.bancolombia.model.tournament.gateways.TournamentRepository;
import co.com.bancolombia.r2dbc.entities.CategoryEntity;
import co.com.bancolombia.r2dbc.entities.GameTypeEntity;
import co.com.bancolombia.r2dbc.entities.TournamentEntity;
import co.com.bancolombia.r2dbc.entities.UserEntity;
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
public class TournamentEntityRepositoryAdapter implements TournamentRepository {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final TournamentEntityRepository tournamentEntityRepository;
    public static final String CATEGORY = "id_category";
    public static final String GAMETIME = "id_game_type";
    public static final String USER = "id_user";

    public Flux<Tournament> findAllPaged(int page, int size, Integer category, Integer gameType, Boolean isFree) {

        Criteria criteria = Criteria.empty();

        if (category != null) {
            criteria = criteria.and("fk_id_category").is(category);
        }
        if (gameType != null) {
            criteria = criteria.and("fk_id_game_type").is(gameType);
        }
        if (isFree != null) {
            criteria = criteria.and("is_free").is(isFree);
        }

        Query query = Query.query(criteria).limit(size).offset((long)page * size);

        return r2dbcEntityTemplate.select(query,TournamentEntity.class)
                .flatMap(tournamentEntity -> {
                    Mono<CategoryEntity> categoryEntityMono = r2dbcEntityTemplate.selectOne(
                            Query.query(Criteria.where(CATEGORY).is(tournamentEntity.getCategory())), CategoryEntity.class);
                    Mono<GameTypeEntity> gameTypeEntityMono = r2dbcEntityTemplate.selectOne(
                            Query.query(Criteria.where(GAMETIME).is(tournamentEntity.getGameType())), GameTypeEntity.class);
                    Mono<UserEntity> userEntityMono = r2dbcEntityTemplate.selectOne(
                            Query.query(Criteria.where(USER).is(tournamentEntity.getCreatorId())), UserEntity.class);

                    return Mono.zip(categoryEntityMono, gameTypeEntityMono, userEntityMono)
                            .map(tuple -> tournamentEntity.toDomain(tuple.getT1(), tuple.getT2(), tuple.getT3()));
                });
    }


    public Mono<Tournament> save(Tournament tournament) {

        return tournamentEntityRepository.save(TournamentEntity.fromDomain(tournament))
                .flatMap(tournamentEntity -> {
                    Mono<CategoryEntity> categoryEntityMono = r2dbcEntityTemplate.selectOne(
                            Query.query(Criteria.where(CATEGORY).is(tournamentEntity.getCategory())), CategoryEntity.class);
                    Mono<GameTypeEntity> gameTypeEntityMono = r2dbcEntityTemplate.selectOne(
                            Query.query(Criteria.where(GAMETIME).is(tournamentEntity.getGameType())), GameTypeEntity.class);
                    Mono<UserEntity> userEntityMono = r2dbcEntityTemplate.selectOne(
                            Query.query(Criteria.where(USER).is(tournamentEntity.getCreatorId())), UserEntity.class);

                    return Mono.zip(categoryEntityMono, gameTypeEntityMono, userEntityMono)
                            .map(tuple -> tournamentEntity.toDomain(tuple.getT1(), tuple.getT2(), tuple.getT3()));
                });
    }


    public Mono<Tournament> findById(Integer id) {

        return r2dbcEntityTemplate.select(TournamentEntity.class)
                .matching(Query.query(Criteria.where("id_tournament").is(id)))
                .one()
                .flatMap(tournamentEntity -> {
                    Mono<CategoryEntity> categoryEntityMono = r2dbcEntityTemplate.selectOne(
                            Query.query(Criteria.where(CATEGORY).is(tournamentEntity.getCategory())), CategoryEntity.class);
                    Mono<GameTypeEntity> gameTypeEntityMono = r2dbcEntityTemplate.selectOne(
                            Query.query(Criteria.where(GAMETIME).is(tournamentEntity.getGameType())), GameTypeEntity.class);
                    Mono<UserEntity> userEntityMono = r2dbcEntityTemplate.selectOne(
                            Query.query(Criteria.where(USER).is(tournamentEntity.getCreatorId())), UserEntity.class);

                    return Mono.zip(categoryEntityMono, gameTypeEntityMono, userEntityMono)
                            .map(tuple -> tournamentEntity.toDomain(tuple.getT1(), tuple.getT2(), tuple.getT3()));
                });
    }


}
