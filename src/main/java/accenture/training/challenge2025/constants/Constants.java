package accenture.training.challenge2025.constants;

public class Constants {
    // PATH_VARIABLES
    public static final String ID_PATH_VARIABLE = "/{id}";
    public static final String ORIGEN_PATH_VARIABLE = "/{origen}";
    public static final String ORIGEN_DESTINO_PATH_VARIABLE = ORIGEN_PATH_VARIABLE + "/{destino}";

    // ENDPOINTS
    public static final String API_BASE_PATH = "/api";
    public static final String PUNTO_DE_VENTA_ENDPOINT = API_BASE_PATH + "/pdv";
    public static final String COSTOS_ENDPOINT = API_BASE_PATH + "/costos";
    public static final String ACREDITACIONES_ENDPOINT = API_BASE_PATH + "/acreditaciones";
    public static final String COSTOS_CAMINO_ENDPOINT = "/camino" + ORIGEN_DESTINO_PATH_VARIABLE;

    // GENERAL
    public static final String PUNTO_DE_VENTA_UNKNOWN = "Desconocido";

    // MESSAGES
    public static final String COSTOS_CAMINO_NOT_FOUND = "No hay camino disponible";

    // EXCEPTION MESSAGES
    public static final String GENERIC_EXCEPTION = "Error inesperado en la aplicacion";
    public static final String PUNTO_DE_VENTA_NOT_FOUND_EXCEPTION = "El punto de venta no existe";
}
