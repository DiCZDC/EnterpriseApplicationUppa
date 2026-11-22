package logica;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import modelo.Enfermedad;
import modelo.Hongo;
import modelo.Tratamiento;

/**
 * Resultado de un reconocimiento: respuesta JSON de la API Python (POST /analizar)
 * enriquecida con las entidades de la BD (Hongo, Enfermedad, Tratamientos).
 *
 * Equivale al array $resultado del componente Livewire file.upload de Laravel.
 *
 * @author crstvbl
 */
public class ResultadoAnalisis implements Serializable {

    private static final long serialVersionUID = 1L;

    // ── Campos devueltos por la API ──
    private boolean detectado;
    private String especie;
    private Double confianza;
    private Integer idEspecie;
    private Boolean cultivada;
    private Boolean sano;
    private String estado;
    private Double confianzaSalud;
    private String mensajeEspecie;
    private String mensajeSalud;
    private String respuestaCruda;

    // ── Datos enriquecidos desde la BD ──
    private Hongo hongo;
    private Enfermedad enfermedad;
    private List<Tratamiento> tratamientos = new ArrayList<>();
    private Integer idAnalisis;
    private String imagen;

    /** Porcentaje 0-100 (la API ya lo envía en %, pero se normaliza por si llega como 0-1). */
    public Double getConfianzaPct() {
        if (confianza == null) {
            return null;
        }
        return (confianza > 0 && confianza <= 1) ? confianza * 100 : confianza;
    }

    public String getNivelSeguridad() {
        Double p = getConfianzaPct();
        if (p == null) {
            return "";
        }
        if (p >= 80) {
            return "Excelente";
        }
        if (p >= 60) {
            return "Buena";
        }
        if (p >= 50) {
            return "Regular";
        }
        return "Baja";
    }

    /** Clase CSS asociada al nivel de seguridad (nivel-excelente, nivel-buena...). */
    public String getClaseNivel() {
        return "nivel-" + getNivelSeguridad().toLowerCase();
    }

    /** Igual que Laravel: si la API no determina el estado, se asume sano. */
    public boolean isSaludable() {
        return sano == null || sano;
    }

    /** Se muestran los consejos de "Toma una mejor foto". */
    public boolean isFotoDeficiente() {
        Double p = getConfianzaPct();
        return !detectado || (p != null && p < 60);
    }

    public String getEstadoLegible() {
        if (enfermedad != null) {
            return enfermedad.getNombreComun();
        }
        if (estado == null || estado.isBlank()) {
            return "Sano";
        }
        String s = estado.replace('_', ' ');
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    // ── Getters / setters ──
    public boolean isDetectado() { return detectado; }
    public void setDetectado(boolean detectado) { this.detectado = detectado; }
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    public Double getConfianza() { return confianza; }
    public void setConfianza(Double confianza) { this.confianza = confianza; }
    public Integer getIdEspecie() { return idEspecie; }
    public void setIdEspecie(Integer idEspecie) { this.idEspecie = idEspecie; }
    public Boolean getCultivada() { return cultivada; }
    public void setCultivada(Boolean cultivada) { this.cultivada = cultivada; }
    public Boolean getSano() { return sano; }
    public void setSano(Boolean sano) { this.sano = sano; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Double getConfianzaSalud() { return confianzaSalud; }
    public void setConfianzaSalud(Double confianzaSalud) { this.confianzaSalud = confianzaSalud; }
    public String getMensajeEspecie() { return mensajeEspecie; }
    public void setMensajeEspecie(String mensajeEspecie) { this.mensajeEspecie = mensajeEspecie; }
    public String getMensajeSalud() { return mensajeSalud; }
    public void setMensajeSalud(String mensajeSalud) { this.mensajeSalud = mensajeSalud; }
    public String getRespuestaCruda() { return respuestaCruda; }
    public void setRespuestaCruda(String respuestaCruda) { this.respuestaCruda = respuestaCruda; }
    public Hongo getHongo() { return hongo; }
    public void setHongo(Hongo hongo) { this.hongo = hongo; }
    public Enfermedad getEnfermedad() { return enfermedad; }
    public void setEnfermedad(Enfermedad enfermedad) { this.enfermedad = enfermedad; }
    public List<Tratamiento> getTratamientos() { return tratamientos; }
    public void setTratamientos(List<Tratamiento> tratamientos) { this.tratamientos = tratamientos; }
    public Integer getIdAnalisis() { return idAnalisis; }
    public void setIdAnalisis(Integer idAnalisis) { this.idAnalisis = idAnalisis; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
}
