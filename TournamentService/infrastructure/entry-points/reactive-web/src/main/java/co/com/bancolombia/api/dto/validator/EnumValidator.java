package co.com.bancolombia.api.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<EnumValid, String> {

    private Enum<?>[] enumConstants;

    @Override
    public void initialize(EnumValid constraintAnnotation) {
        this.enumConstants = constraintAnnotation.enumClass().getEnumConstants();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.isEmpty()) {
            return false; // Rechazar cadena vacía
        }

        for (Enum<?> enumConstant : enumConstants) {
            if (enumConstant.name().equals(value)) {
                return true; // El valor es válido
            }
        }
        return false; // Valor no válido
    }
}
