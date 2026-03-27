package co.com.bancolombia.model.exceptions;

import co.com.bancolombia.model.exceptions.message.BussinessMessages;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final BussinessMessages errorMessage;

    public BusinessException(BussinessMessages errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
}
