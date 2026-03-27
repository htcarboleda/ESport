package co.com.bancolombia.redis;

import co.com.bancolombia.model.gametype.gateways.GameTypesCacheGateway;
import co.com.bancolombia.redis.commons.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Repository
@RequiredArgsConstructor
@Qualifier("reactiveStringRedisTemplate")
public class GameTypesCacheAdapter implements GameTypesCacheGateway {

    private static final String KEY_PREFIX = Constants.GAMETYPE_KEY_PREFIX;
    private final ReactiveRedisTemplate<String, String> redisTemplate;

    @Value("${cache.game_type.ttl-seconds:300}")
    private long ttlSeconds;

    @Override
    public Mono<Boolean> getExistence(Integer id) {
        return redisTemplate.opsForValue()
                .get(KEY_PREFIX + id)
                .map(Boolean::parseBoolean)
                .doOnNext(value -> log.info("[REDIS] game_type={} -> exists={}", id, value))
                .doOnSuccess(value -> {
                    if (value == null) log.debug("[REDIS MISS] game_type={}", id);
                });
    }

    @Override
    public Mono<Void> putExistence(Integer id, boolean exists) {
        return redisTemplate.opsForValue()
                .set(KEY_PREFIX + id, String.valueOf(exists),
                        Duration.ofSeconds(ttlSeconds))
                .then();
    }

    @Override
    public Mono<Void> evict(Integer id) {
        return redisTemplate.delete(KEY_PREFIX + id).then();
    }
}
