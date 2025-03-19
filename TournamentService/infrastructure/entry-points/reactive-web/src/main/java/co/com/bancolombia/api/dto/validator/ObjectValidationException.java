package co.com.bancolombia.api.dto.validator;

import co.com.bancolombia.api.exceptions.error.ErrorItem;
import lombok.Getter;

import java.util.List;

@Getter
public class ObjectValidationException extends RuntimeException {

    private final transient List<ErrorItem> errorList;

    public ObjectValidationException(List<ErrorItem> errors) {
        super("Validar la información: " + errors);
        this.errorList = errors;
    }
}
