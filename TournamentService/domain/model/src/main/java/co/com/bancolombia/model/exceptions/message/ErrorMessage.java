package co.com.bancolombia.model.exceptions.message;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorMessage {

    CATEGORY_NOT_FOUND("ATTDB0001", "Categoria No encontrada", 404),
    GAMETYPE_NOT_FOUND("ATTDB0002", "Tipo de Juego No encontrado", 404),

    INVALID_REQUEST("ATTDB0001", "Petición inválida", 500),
    INVALID_HEADERS("ATTDB0002", "Encabezados inválidos", 500),
    INVALID_SIGNATURE("ATTDB0011", "Firma de la petición inválida", 500),
    IN_PROGRESS_TRANSACTION("ATTDB0003", "La petición está en progreso", 425),
    TOKEN_CREDENTIALS_ERROR("ATTDB0005", "No pudo obtener el token de acceso", 500),
    CARD_INFORMATION_ERROR("ATTDB0006", "No pudo obtener la información de la tarjeta", 500),
    USER_INFORMATION_ERROR("ATTDB0008", "No pudo obtener la información del usuario", 500),
    FINACLE_ERROR("ATTDB0007", "Error en la respuesta del proveedor del servicio", 502),
    AFFINITY_GROUP_ID_ERROR("ATTDBB0010", "Error al homologar el valor del affinity group id", 500),
    GENERATION_ERROR("ATTDB0012", "Error interno del servidor", 500),
    MAPPER_ERROR("ATTDT0005", "Error interno del servidor", 500),
    EXTERNAL_MESSAGE_ERROR("ATTDT9999", "Servicio no disponible.", 503);

    private final String code;
    private final String message;
    private final Integer status;
}