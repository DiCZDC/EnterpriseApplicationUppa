package logica;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuración externa de Úppa (equivalente al .env de Laravel).
 *
 * Orden de resolución de cada valor:
 *   1. Propiedad de sistema de la JVM de Payara  (asadmin create-system-properties uppa.api.url=...)
 *   2. Variable de entorno                       (UPPA_API_URL=...)
 *   3. Valor por defecto
 *
 * @author crstvbl
 */
public final class Configuracion {

    private Configuracion() {
    }

    /** URL base del microservicio Python (FastAPI + YOLO). Equivale a UPPA_API_URL de Laravel. */
    public static String apiUrl() {
        String url = valor("uppa.api.url", "UPPA_API_URL", "http://localhost:8000");
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    /** Tiempo máximo de espera de la API en segundos. */
    public static int apiTimeoutSegundos() {
        try {
            return Integer.parseInt(valor("uppa.api.timeout", "UPPA_API_TIMEOUT", "60"));
        } catch (NumberFormatException e) {
            return 60;
        }
    }

    /** Carpeta donde se guardan las fotos analizadas. */
    public static Path directorioImagenes() {
        String defecto = Paths.get(System.getProperty("user.home"), "uppa-imagenes").toString();
        return Paths.get(valor("uppa.imagenes.dir", "UPPA_IMAGENES_DIR", defecto));
    }

    private static String valor(String propiedad, String variableEntorno, String defecto) {
        String v = System.getProperty(propiedad);
        if (v == null || v.isBlank()) {
            v = System.getenv(variableEntorno);
        }
        return (v == null || v.isBlank()) ? defecto : v.trim();
    }
}
