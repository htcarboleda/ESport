package co.com.bancolombia.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String NOT_FOUND = "Unknown";

    // ------------- EXCEPTIONS -------------
    public static final String OBJECT_VALIDATION_EXCEPTION = "ObjectValidationException";
    public static final String BUSINESS_EXCEPTION = "BusinessException";
    public static final String TECHNICAL_EXCEPTION = "TechnicalException";
    public static final String SECURITY_EXCEPTION = "SecurityException";
    public static final String METHOD_NOT_ALLOWED_EXCEPTION = "MethodNotAllowedException";
    public static final String EXCEPTION = "Exception";

}
