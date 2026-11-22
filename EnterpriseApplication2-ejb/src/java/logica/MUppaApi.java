package logica;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

/**
 * Cliente del microservicio Python "Úppa API" (FastAPI + YOLO, carpeta uppa-api/).
 * Equivale a App\Services\UppaApiService::catalogarHongo() de Laravel:
 * envía la imagen como multipart (campo "file") a {UPPA_API_URL}/analizar.
 *
 * @author crstvbl
 */
@Stateless
@LocalBean
public class MUppaApi {

    private static final Logger LOG = Logger.getLogger(MUppaApi.class.getName());

    private static final HttpClient CLIENTE = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .version(HttpClient.Version.HTTP_1_1)
            .build();

    /** GET / → true si la API responde. */
    public boolean disponible() {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(Configuracion.apiUrl() + "/"))
                    .timeout(Duration.ofSeconds(5)).GET().build();
            return CLIENTE.send(req, HttpResponse.BodyHandlers.discarding()).statusCode() == 200;
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /** POST /analizar con la imagen. Devuelve el resultado sin enriquecer con la BD. */
    public ResultadoAnalisis catalogarHongo(byte[] imagen, String nombreArchivo, String contentType) throws UppaException {
        String limite = "----UppaBoundary" + UUID.randomUUID().toString().replace("-", "");
        byte[] cuerpo = construirMultipart(limite, imagen, nombreArchivo, contentType);
        String url = Configuracion.apiUrl() + "/analizar";

        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(Configuracion.apiTimeoutSegundos()))
                .header("Content-Type", "multipart/form-data; boundary=" + limite)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(cuerpo))
                .build();

        HttpResponse<String> resp;
        try {
            resp = CLIENTE.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (ConnectException e) {
            throw new UppaException("No se pudo conectar con la API de reconocimiento (" + url
                    + "). Verifica que el servicio Python esté en ejecución.", e);
        } catch (HttpTimeoutException e) {
            throw new UppaException("La API de reconocimiento tardó demasiado en responder.", e);
        } catch (IOException e) {
            throw new UppaException("Error al contactar la API: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new UppaException("La petición a la API fue interrumpida.", e);
        }

        if (resp.statusCode() / 100 != 2) {
            LOG.log(Level.WARNING, "API Úppa respondió {0}: {1}", new Object[]{resp.statusCode(), resp.body()});
            throw new UppaException("Error al contactar la API: " + resp.statusCode() + " - " + resp.body());
        }
        return parsear(resp.body());
    }

    private static byte[] construirMultipart(String limite, byte[] datos, String nombreArchivo, String contentType) {
        String nombre = (nombreArchivo == null || nombreArchivo.isBlank()) ? "foto.jpg"
                : nombreArchivo.replaceAll("[\"\\r\\n]", "_");
        String tipo = (contentType == null || contentType.isBlank()) ? "application/octet-stream" : contentType;
        ByteArrayOutputStream out = new ByteArrayOutputStream(datos.length + 512);
        String cabecera = "--" + limite + "\r\n"
                + "Content-Disposition: form-data; name=\"file\"; filename=\"" + nombre + "\"\r\n"
                + "Content-Type: " + tipo + "\r\n\r\n";
        out.writeBytes(cabecera.getBytes(StandardCharsets.UTF_8));
        out.writeBytes(datos);
        out.writeBytes(("\r\n--" + limite + "--\r\n").getBytes(StandardCharsets.UTF_8));
        return out.toByteArray();
    }

    static ResultadoAnalisis parsear(String json) throws UppaException {
        JsonObject o;
        try (JsonReader r = Json.createReader(new StringReader(json))) {
            o = r.readObject();
        } catch (RuntimeException e) {
            throw new UppaException("La API devolvió una respuesta no válida.", e);
        }
        ResultadoAnalisis res = new ResultadoAnalisis();
        res.setRespuestaCruda(json);
        res.setDetectado(Boolean.TRUE.equals(bool(o, "detectado")));
        res.setEspecie(texto(o, "especie"));
        res.setConfianza(numero(o, "confianza"));
        Double id = numero(o, "id_especie");
        res.setIdEspecie(id == null ? null : id.intValue());
        res.setCultivada(bool(o, "cultivada"));
        res.setSano(bool(o, "sano"));
        res.setEstado(texto(o, "estado"));
        res.setConfianzaSalud(numero(o, "confianza_salud"));
        res.setMensajeEspecie(texto(o, "mensaje_especie"));
        res.setMensajeSalud(texto(o, "mensaje_salud"));
        return res;
    }

    private static String texto(JsonObject o, String k) {
        JsonValue v = o.get(k);
        return (v == null || v.getValueType() == JsonValue.ValueType.NULL) ? null
                : (v.getValueType() == JsonValue.ValueType.STRING ? o.getString(k) : v.toString());
    }

    private static Double numero(JsonObject o, String k) {
        JsonValue v = o.get(k);
        return (v instanceof JsonNumber) ? ((JsonNumber) v).doubleValue() : null;
    }

    private static Boolean bool(JsonObject o, String k) {
        JsonValue v = o.get(k);
        if (v == JsonValue.TRUE) {
            return Boolean.TRUE;
        }
        if (v == JsonValue.FALSE) {
            return Boolean.FALSE;
        }
        return null;
    }
}
