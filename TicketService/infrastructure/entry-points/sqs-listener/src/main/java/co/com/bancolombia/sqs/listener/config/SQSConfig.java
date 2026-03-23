package co.com.bancolombia.sqs.listener.config;

import co.com.bancolombia.sqs.listener.SqsTicketConsumer;
import co.com.bancolombia.sqs.listener.helper.SQSListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Configuration
@EnableConfigurationProperties(SQSProperties.class)
public class SQSConfig {

    @Bean
    public Function<Message, Mono<Void>> sqsMessageProcessor(SqsTicketConsumer consumer) {
        return consumer::process;
    }

    @Bean
    public SQSListener sqsListener(
            @Qualifier("sqsAsyncClient") SqsAsyncClient client,
            SQSProperties properties,
            Function<Message, Mono<Void>> sqsMessageProcessor) {
        return SQSListener.builder()
                .client(client)
                .properties(properties)
                .processor(sqsMessageProcessor)
                .build()
                .start();
    }
}
