package co.com.bancolombia.model.exceptions.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SecurityErrorMessage {

    INVALID_BODY("AOISB003", "Missing body", 400),
    INVALID_REQUEST("AOISB004", "Invalid Request", 400);

    private final String code;
    private final String message;
    private final Integer status;

    private static final String DEFAULT_TITLE = "Forbidden";

    private static String getDefaultTitle(){
        return DEFAULT_TITLE;
    }
}
