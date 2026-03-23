package co.com.bancolombia.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
public class AwsConfig {

    @Value("${aws.region:us-east-1}")
    private String awsRegion;

    @Value("${aws.endpoint:}")
    private String awsEndpoint;

    // SnsClient es autoconfigured por Spring Cloud AWS usando:
    //   spring.cloud.aws.credentials.access-key / secret-key  (local)
    //   spring.cloud.aws.sns.endpoint                         (local → LocalStack)
    //   spring.cloud.aws.region.static                        (ambos entornos)
    // No se define bean manual aquí para evitar conflictos con la autoconfiguración.

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        var builder = DynamoDbClient.builder()
                .region(Region.of(awsRegion));

        // Si hay endpoint override (LocalStack o testing) lo usa
        if (awsEndpoint != null && !awsEndpoint.isBlank()) {
            builder.endpointOverride(URI.create(awsEndpoint))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create("test", "test")));
        } else {
            // En AWS real usa el rol IAM del EC2/ECS automáticamente
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }

        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(builder.build())
                .build();
    }
}
