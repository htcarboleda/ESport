package co.com.bancolombia.sqs.listener.helper;

import co.com.bancolombia.sqs.listener.commons.Constants;
import co.com.bancolombia.sqs.listener.config.SQSProperties;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

@Log4j2
@Builder
public class SQSListener {
    private final SqsAsyncClient client;
    private final SQSProperties properties;
    private final Function<Message, Mono<Void>> processor;
    private String operation;

    public SQSListener start() {
        this.operation = "MessageFrom:" + properties.queueUrl();
        log.info("[SQSListener] Iniciando polling en cola: {}", properties.queueUrl());
        ExecutorService service = Executors.newFixedThreadPool(properties.numberOfThreads());
        Flux<Void> flow = listenRetryRepeat().publishOn(Schedulers.fromExecutorService(service));
        for (var i = 0; i < properties.numberOfThreads(); i++) {
            flow.subscribe();
        }
        return this;
    }

    private Flux<Void> listenRetryRepeat() {
        return listen()
                .doOnError(e -> log.error("[SQSListener] Error en cola {}: {}", properties.queueUrl(), e.getMessage(), e))
                .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(5))
                        .maxBackoff(Duration.ofSeconds(30))
                        .doBeforeRetry(s -> log.warn("[SQSListener] Reintentando conexión a SQS (intento {})", s.totalRetries() + 1)))
                .repeat();
    }

    private Flux<Void> listen() {
        return getMessages()
                .flatMap(message -> processor.apply(message)
                        .name(Constants.ASYNC_OPERATION)
                        .tag(Constants.OPERATION, operation)
                        .metrics()
                        .then(confirm(message)))
                .onErrorContinue((e, o) -> log.error("Error listening sqs message", e));
    }

    private Mono<Void> confirm(Message message) {
        return Mono.fromCallable(() -> getDeleteMessageRequest(message.receiptHandle()))
                .flatMap(request -> Mono.fromFuture(client.deleteMessage(request)))
                .then();
    }

    private Flux<Message> getMessages() {
        return Mono.fromCallable(this::getReceiveMessageRequest)
                .flatMap(request -> Mono.fromFuture(client.receiveMessage(request)))
                .doOnNext(response -> log.info("[SQSListener] {} mensaje(s) recibido(s) de {}", response.messages().size(), properties.queueUrl()))
                .flatMapMany(response -> Flux.fromIterable(response.messages()));
    }

    private ReceiveMessageRequest getReceiveMessageRequest() {
        return ReceiveMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .maxNumberOfMessages(properties.maxNumberOfMessages())
                .waitTimeSeconds(properties.waitTimeSeconds())
                .visibilityTimeout(properties.visibilityTimeoutSeconds())
                .build();
    }

    private DeleteMessageRequest getDeleteMessageRequest(String receiptHandle) {
        return DeleteMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .receiptHandle(receiptHandle)
                .build();
    }
}
