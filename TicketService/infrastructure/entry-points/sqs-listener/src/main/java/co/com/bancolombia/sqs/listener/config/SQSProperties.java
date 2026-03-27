package co.com.bancolombia.sqs.listener.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "entrypoint.sqs")
public record SQSProperties(
        String region,
        String endpoint,
        String queueUrl,
        @DefaultValue("20") int waitTimeSeconds,
        @DefaultValue("30") int visibilityTimeoutSeconds,
        @DefaultValue("10") int maxNumberOfMessages,
        @DefaultValue("1")  int numberOfThreads,
        @DefaultValue("0")  int startupDelaySeconds) {
}
