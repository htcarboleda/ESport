package co.com.bancolombia.r2dbc.gametype;

import co.com.bancolombia.model.gametype.GameType;
import co.com.bancolombia.r2dbc.entities.GameTypeEntity;
import co.com.bancolombia.model.gametype.gateways.GameTypesRepository;
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
public class GameTypeEntityRepositoryAdapter implements GameTypesRepository {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final GameTypeEntityRepository gameTypeEntityRepository;

    public Mono<GameType> findById(Integer id) {

        return r2dbcEntityTemplate.select(GameTypeEntity.class)
                .matching(Query.query(Criteria.where("id_game_type").is(id)))
                .one()
                .map(GameTypeEntity::toDomain);
    }
}