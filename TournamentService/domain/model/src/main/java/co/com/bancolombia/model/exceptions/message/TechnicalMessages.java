package co.com.bancolombia.model.exceptions.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TechnicalMessages {

    UNEXPECTED_EXCEPTION(
            "SP500",
            "Ha ocurrido un error interno controlado en el servidor, inténtelo mas tarde.",
            500,
            getDefaultTitle()),
    INVALID_RESPONSE("SP502",
            "El mensaje de respuesta recibido del servidor es inválido.",
            502,
            "Bad Gateway"),
    TIMEOUT_EXCEPTION("SP504",
            "El proveedor no respondió en tiempo esperado.",
            504,
            "Gateway Timeout");

    private final String code;
    private final String message;
    private final Integer status;
    private final String title;

    private static final String DEFAULT_TITLE = "Bad Request";

    private static String getDefaultTitle(){
        return DEFAULT_TITLE;
    }
}
