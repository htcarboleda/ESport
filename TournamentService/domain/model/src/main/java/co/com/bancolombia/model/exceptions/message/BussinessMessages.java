package co.com.bancolombia.model.exceptions.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BussinessMessages {

    CATEGORY_NOT_FOUND("ATTDB0001", "Categoria No encontrada", 404),
    GAMETYPE_NOT_FOUND("ATTDB0002", "Tipo de Juego No encontrado", 404),
    TOURNAMENT_NOT_FOUND("ATTDB0003", "Torneo No encontrado", 404),
    TOURNAMENT_EXISTS("ATTDB0004", "Torneo ya existe", 404);


    private final String code;
    private final String message;
    private final Integer status;

    private static final String DEFAULT_TITLE = "Bad Request";

    private static String getDefaultTitle(){
        return DEFAULT_TITLE;
    }
}
