package co.com.bancolombia.model.tournamentadmin.gateways;
import co.com.bancolombia.model.tournamentadmin.TournamentAdmin;
import reactor.core.publisher.Mono;

public interface TournamentAdminRepository {

    Mono<TournamentAdmin> save(TournamentAdmin tournamentAdmin);

}
