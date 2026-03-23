package co.com.bancolombia.api.dto.validator;
import co.com.bancolombia.api.exceptions.error.ErrorItem;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ObjectValidator {

    private final Validator validator;

    public <T> void validate(T object) {
        var errors = validator.validate(object);
        if (!errors.isEmpty()) {
            List<ErrorItem> listErrors = errors.stream()
                    .map(tConstraintViolation ->
                            ErrorItem.builder()
                                    .detail(tConstraintViolation.getMessage())
                                    .build()
                    )
                    .toList();
            throw new ObjectValidationException(listErrors);
        }
    }
}
