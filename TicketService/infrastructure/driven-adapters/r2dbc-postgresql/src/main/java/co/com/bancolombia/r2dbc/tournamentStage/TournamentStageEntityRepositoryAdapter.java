package co.com.bancolombia.r2dbc.tournamentStage;

import co.com.bancolombia.model.tournamentstage.TournamentStage;
import co.com.bancolombia.model.tournamentstage.gateways.TournamentStageRepository;
import co.com.bancolombia.r2dbc.entities.TournamentStageEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
public class TournamentStageEntityRepositoryAdapter implements TournamentStageRepository {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final TournamentStageEntityRepository tournamentStageEntityRepository;

    @Override
    public Flux<TournamentStage> findByIdTournament(Integer idTournament) {

        return r2dbcEntityTemplate.select(TournamentStageEntity.class)
                .matching(Query.query(Criteria.where("fk_id_tournament").is(idTournament)))
                .all()
                .map(TournamentStageEntity::toDomain)   ;
    }



}
