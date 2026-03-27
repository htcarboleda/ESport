package co.com.bancolombia.r2dbc.tournament;

import co.com.bancolombia.r2dbc.commons.Constants;
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

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class TournamentEntityRepositoryAdapter implements TournamentRepository {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final TournamentEntityRepository tournamentEntityRepository;


    public Flux<Tournament> findAllPaged(int page, int size, Integer category, Integer gameType, Boolean isFree) {

        Criteria criteria = Criteria.empty();

        if (category != null) {
            criteria = criteria.and(Constants.TOURNAMENT_ID_CATEGORY).is(category);
        }
        if (gameType != null) {
            criteria = criteria.and(Constants.TOURNAMENT_ID_GAME).is(gameType);
        }
        if (isFree != null) {
            criteria = criteria.and(Constants.TOURNAMENT_IS_FREE).is(isFree);
        }

        Query query = Query.query(criteria).limit(size).offset((long)page * size);

        return r2dbcEntityTemplate.select(query,TournamentEntity.class)
                .flatMap(tournamentEntity -> {
                    Mono<CategoryEntity> categoryEntityMono = r2dbcEntityTemplate.selectOne(
                            Query.query(Criteria.where(Constants.CATEGORY_ID).is(tournamentEntity.getCategory())), CategoryEntity.class);
                    Mono<GameTypeEntity> gameTypeEntityMono = r2dbcEntityTemplate.selectOne(
                            Query.query(Criteria.where(Constants.GAMETIME_ID).is(tournamentEntity.getGameType())), GameTypeEntity.class);
                    Mono<UserEntity> userEntityMono = r2dbcEntityTemplate.selectOne(
                            Query.query(Criteria.where(Constants.USER_ID).is(tournamentEntity.getCreatorId())), UserEntity.class);

                    return Mono.zip(categoryEntityMono, gameTypeEntityMono, userEntityMono)
                            .map(tuple -> tournamentEntity.toDomain(tuple.getT1(), tuple.getT2(), tuple.getT3()));
                });
    }


    public Mono<Tournament> save(Tournament tournament) {

        return tournamentEntityRepository.save(TournamentEntity.fromDomain(tournament))
                .flatMap(tournamentEntity -> {
                    Mono<CategoryEntity> categoryEntityMono = r2dbcEntityTemplate.selectOne(
                            Query.query(Criteria.where(Constants.CATEGORY_ID).is(tournamentEntity.getCategory())), CategoryEntity.class);
                    Mono<GameTypeEntity> gameTypeEntityMono = r2dbcEntityTemplate.selectOne(
                            Query.query(Criteria.where(Constants.GAMETIME_ID).is(tournamentEntity.getGameType())), GameTypeEntity.class);
                    Mono<UserEntity> userEntityMono = r2dbcEntityTemplate.selectOne(
                            Query.query(Criteria.where(Constants.USER_ID).is(tournamentEntity.getCreatorId())), UserEntity.class);

                    return Mono.zip(categoryEntityMono, gameTypeEntityMono, userEntityMono)
                            .map(tuple -> tournamentEntity.toDomain(tuple.getT1(), tuple.getT2(), tuple.getT3()));
                });
    }


    public Mono<Tournament> findById(Integer id) {

        return r2dbcEntityTemplate.select(TournamentEntity.class)
                .matching(Query.query(Criteria.where(Constants.TOURNAMENT_ID).is(id)))
                .one()
                .flatMap(tournamentEntity -> {
                    Mono<CategoryEntity> categoryEntityMono = r2dbcEntityTemplate.selectOne(
                            Query.query(Criteria.where(Constants.CATEGORY_ID).is(tournamentEntity.getCategory())), CategoryEntity.class);
                    Mono<GameTypeEntity> gameTypeEntityMono = r2dbcEntityTemplate.selectOne(
                            Query.query(Criteria.where(Constants.GAMETIME_ID).is(tournamentEntity.getGameType())), GameTypeEntity.class);
                    Mono<UserEntity> userEntityMono = r2dbcEntityTemplate.selectOne(
                            Query.query(Criteria.where(Constants.USER_ID).is(tournamentEntity.getCreatorId())), UserEntity.class);

                    return Mono.zip(categoryEntityMono, gameTypeEntityMono, userEntityMono)
                            .map(tuple -> tournamentEntity.toDomain(tuple.getT1(), tuple.getT2(), tuple.getT3()));
                });
    }

    @Override
    public Mono<Boolean> existsByName(String name, LocalDate startDate, LocalDate endDate) {
        return r2dbcEntityTemplate.exists(
                Query.query(
                        Criteria.where(Constants.TOURNAMENT_NAME).is(name)
                                .and(Constants.TOURNAMENT_START_DATE).is(startDate)
                                .and(Constants.TOURNAMENT_END_DATE).is(endDate)
                ),
                TournamentEntity.class
        );
    }


}
