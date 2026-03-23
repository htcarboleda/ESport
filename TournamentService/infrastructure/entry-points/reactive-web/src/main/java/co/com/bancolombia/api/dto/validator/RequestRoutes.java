package co.com.bancolombia.api.dto.validator;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;


@Getter
@AllArgsConstructor
public enum RequestRoutes {
    HEALTH_CHECK_HEAD("/actuator/health", "HEAD"),
    HEALTH_CHECK_GET("/actuator/health", "GET");

    private final String path;
    private final String method;

    public static boolean isValidRoute(String path, String method) {
        return Arrays.stream(values()).anyMatch(route -> route.matches(path, method));
    }

    boolean matches(String path, String method) {
        return this.path.equals(path) && this.method.equalsIgnoreCase(method);
    }
}
