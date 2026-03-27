package co.com.bancolombia.model.audit;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEventRecord {

    private String eventId;
    private String eventType;
    private String detail;
    private String occurredAt;
    private Long   ttl;
}
