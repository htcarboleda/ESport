package co.com.bancolombia.api.exceptions;


import co.com.bancolombia.api.dto.validator.ObjectValidationException;
import co.com.bancolombia.api.exceptions.error.ErrorResponse;
import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.exceptions.TechnicalException;
import co.com.bancolombia.model.exceptions.message.ErrorMessage;
import co.com.bancolombia.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.MethodNotAllowedException;
import reactor.core.publisher.Mono;

@Slf4j
@Order(-2)
@Component
public class ExceptionHandler extends AbstractErrorWebExceptionHandler {

    public ExceptionHandler(
            ErrorAttributes errorAttributes,
            WebProperties resources,
            ApplicationContext applicationContext,
            ServerCodecConfigurer serverCodecConfigurer
    ) {
        super(errorAttributes, resources.getResources(), applicationContext);
        this.setMessageWriters(serverCodecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        return Mono.error(getError(request))

                .onErrorResume(MethodNotAllowedException.class, error -> {
                    log.warn(Constants.METHOD_NOT_ALLOWED_EXCEPTION, error);
                    ErrorResponse rs = ErrorResponse.builder()
                            .message(error.getMessage())
                            .build();
                    return ServerResponse
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(rs);
                })

                .onErrorResume(ObjectValidationException.class, error -> {
                    log.error(Constants.OBJECT_VALIDATION_EXCEPTION, error);
                    ErrorResponse errorRs = ErrorResponse.builder()
                            .message(error.getMessage())
                            .build();
                    return ServerResponse
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(errorRs);
                })

                .onErrorResume(BusinessException.class, error -> {
                    log.warn(Constants.BUSINESS_EXCEPTION, error);
                    ErrorResponse rs = ErrorResponse.builder()
                            .message(error.getErrorMessage().getMessage())
                            .build();
                    return ServerResponse
                            .status(error.getErrorMessage().getStatus())
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(rs);
                })

                .onErrorResume(TechnicalException.class, error -> {
                    log.warn(Constants.TECHNICAL_EXCEPTION, error);
                    ErrorResponse rs = ErrorResponse.builder()
                            .message(error.getErrorMessage().getMessage())
                            .build();
                    return ServerResponse
                            .status(error.getErrorMessage().getStatus())
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(rs);
                })

                .onErrorResume(Exception.class, error -> {
                    log.error(Constants.EXCEPTION, error);
                    ErrorMessage errorMessage = ErrorMessage.EXTERNAL_MESSAGE_ERROR;
                    ErrorResponse rs = ErrorResponse.builder()
                            .message(errorMessage.getMessage())
                            .build();
                    return ServerResponse
                            .status(errorMessage.getStatus())
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(rs);
                })

                .cast(ServerResponse.class);

    }
}