package co.com.bancolombia.model.exceptions;


import co.com.bancolombia.model.exceptions.message.SecurityErrorMessage;
import lombok.Getter;

@Getter
public class SecurityException extends RuntimeException{

    private final SecurityErrorMessage securityErrorMessage;

    public SecurityException(SecurityErrorMessage securityErrorMessage) {
        super(securityErrorMessage.getMessage());
        this.securityErrorMessage = securityErrorMessage;
    }

}