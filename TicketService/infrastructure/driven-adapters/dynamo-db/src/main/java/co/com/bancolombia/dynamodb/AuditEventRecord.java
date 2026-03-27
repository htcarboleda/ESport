package co.com.bancolombia.dynamodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEventRecord {

    private String eventId;
    private String eventType;
    private String detail;
    private String occurredAt;
    private Long ttl;

    @DynamoDbPartitionKey
    public String getEventId() {
        return eventId;
    }

    @DynamoDbAttribute("ttl")
    public Long getTtl() {
        return ttl;
    }
}
