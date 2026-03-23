package co.com.bancolombia.dynamodb;

import co.com.bancolombia.model.audit.gateway.AuditGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DynamoAuditTrailAdapter implements AuditGateway {

    private final DynamoDbEnhancedClient dynamoClient;

    @Value("${aws.dynamodb.audit-table}")
    private String tableName;

    @Override
    public Mono<Void> record(String eventType, Integer tournamentId, String detail) {



        return Mono.fromRunnable(() -> {
                var table = dynamoClient.table(tableName,
                        TableSchema.fromBean(AuditEventRecord.class));

                var record = AuditEventRecord.builder()
                        .eventId(UUID.randomUUID().toString())
                        .eventType(eventType)
                        .tournamentId(tournamentId.toString())
                        .detail(detail)
                        .occurredAt(Instant.now().toString())
                        // TTL: 30 días desde ahora (DynamoDB limpia automáticamente)
                        .ttl(Instant.now().plusSeconds(2_592_000).getEpochSecond())
                        .build();

                table.putItem(record);
                log.info("***************************************[DynamoDB] ✅ Audit: {} tournamentId={}", eventType, tournamentId);
        })
        .subscribeOn(Schedulers.boundedElastic())
        .doOnError(e -> log.error("[DynamoDB] ❌ Error guardando audit", e))
        .onErrorResume(e -> Mono.empty()) // 👈 evita romper el flujo
        .then();
    }

}