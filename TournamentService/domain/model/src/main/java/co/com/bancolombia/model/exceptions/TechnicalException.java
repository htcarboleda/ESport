package co.com.bancolombia.model.exceptions;

import co.com.bancolombia.model.exceptions.message.ErrorMessage;
import lombok.Getter;

@Getter
public class TechnicalException extends RuntimeException {

    private final ErrorMessage errorMessage;

    public TechnicalException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

    public TechnicalException(Throwable cause, ErrorMessage errorMessage) {
        super(errorMessage.getMessage(), cause);
        this.errorMessage = errorMessage;
    }
}