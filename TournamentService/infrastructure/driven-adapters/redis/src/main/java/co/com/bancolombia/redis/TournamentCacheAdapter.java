package co.com.bancolombia.redis;

import co.com.bancolombia.model.tournament.gateways.TournamentCacheGateway;
import co.com.bancolombia.redis.commons.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;

@Slf4j
@Repository
@RequiredArgsConstructor
@Qualifier("reactiveStringRedisTemplate")
public class TournamentCacheAdapter implements TournamentCacheGateway {

    private static final String KEY_PREFIX = Constants.TOURNAMENT_KEY_PREFIX;
    private final ReactiveRedisTemplate<String, String> redisTemplate;

    @Value("${cache.tournament.ttl-seconds:300}")
    private long ttlSeconds;

    // Clave única compuesta: nombre + fechas
    private String buildKey(String name, LocalDate startDate, LocalDate endDate) {
        return KEY_PREFIX + name + ":" + startDate + ":" + endDate;
    }

    @Override
    public Mono<Boolean> getExistence(String name, LocalDate startDate, LocalDate endDate) {
        String key = buildKey(name, startDate, endDate);
        return redisTemplate.opsForValue()
                .get(key)
                .map(Boolean::parseBoolean)
                .doOnNext(value -> log.info("[REDIS HIT] tournament key={} -> exists={}", key, value))
                .doOnSuccess(value -> {
                    if (value == null) log.debug("[REDIS MISS] tournament key={}", key);
                });
    }

    @Override
    public Mono<Void> putExistence(String name, LocalDate startDate, LocalDate endDate, boolean exists) {
        String key = buildKey(name, startDate, endDate);
        log.debug("[REDIS PUT] tournament key={} exists={}", key, exists);
        return redisTemplate.opsForValue()
                .set(key, String.valueOf(exists), Duration.ofSeconds(ttlSeconds))
                .then();
    }

    @Override
    public Mono<Void> evict(String name, LocalDate startDate, LocalDate endDate) {
        String key = buildKey(name, startDate, endDate);
        log.debug("[REDIS EVICT] tournament key={}", key);
        return redisTemplate.delete(key).then();
    }
}
