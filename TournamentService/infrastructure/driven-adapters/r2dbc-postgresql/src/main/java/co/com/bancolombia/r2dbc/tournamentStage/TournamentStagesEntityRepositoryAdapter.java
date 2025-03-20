package co.com.bancolombia.r2dbc.tournamentStage;

import co.com.bancolombia.model.tournamentstage.TournamentStage;
import co.com.bancolombia.model.tournamentstage.gateways.TournamentStageRepository;
import co.com.bancolombia.r2dbc.entities.TournamentStageEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class TournamentStagesEntityRepositoryAdapter implements TournamentStageRepository {

    private final TournamentStagesEntityRepository tournamentStagesEntityRepository;

    public Mono<TournamentStage> save(TournamentStage tournamentStage) {

        return tournamentStagesEntityRepository.save(TournamentStageEntity.fromDomain(tournamentStage))
                .map(userEntity -> userEntity.toDomain());
    }
}