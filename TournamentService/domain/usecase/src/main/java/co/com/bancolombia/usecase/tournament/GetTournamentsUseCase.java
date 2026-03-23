package co.com.bancolombia.usecase.tournament;
import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.exceptions.message.ErrorMessage;
import co.com.bancolombia.model.tournament.Tournament;
import co.com.bancolombia.model.tournament.gateways.TournamentRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class GetTournamentsUseCase {

    private static final Logger logger = Logger.getLogger(GetTournamentsUseCase.class.getName());

    private final TournamentRepository tournamentRepository;

    public Flux<Tournament> findAll(int page, int size, Integer category, Integer gameType, Boolean isFree) {
        return tournamentRepository.findAllPaged(page, size, category, gameType, isFree);
    }

    public Mono<Tournament> findById(Integer id) {
        return tournamentRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorMessage.TOURNAMENT_NOT_FOUND)));
    }

}
