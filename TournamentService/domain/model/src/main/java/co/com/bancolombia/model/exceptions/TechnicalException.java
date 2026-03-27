package co.com.bancolombia.model.exceptions;

import co.com.bancolombia.model.exceptions.message.TechnicalMessages;
import lombok.Getter;

@Getter
public class TechnicalException extends RuntimeException {

    private final TechnicalMessages errorMessage;

    public TechnicalException(TechnicalMessages errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

    public TechnicalException(Throwable cause, TechnicalMessages errorMessage) {
        super(errorMessage.getMessage(), cause);
        this.errorMessage = errorMessage;
    }
}