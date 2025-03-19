package co.com.bancolombia.api.dto.validator;

import jakarta.validation.Payload;

public @interface EnumValid {

    String message() default "Valor Invalido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    Class<? extends Enum<?>> enumClass();
}
