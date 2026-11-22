package logica;

import dao.AnalisisFacade;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Estadísticas del usuario para la vista de gráficas (GraficasBean).
 *
 * @author crstvbl
 */
@Stateless
@LocalBean
public class MGraficas {

    private static final Locale ES = new Locale("es", "MX");

    @EJB
    private AnalisisFacade analisisFacade;

    /** "Sanos" + cada enfermedad detectada → número de análisis. */
    public Map<String, Long> estadoGeneral(int idUsuario) {
        Map<String, Long> r = new LinkedHashMap<>();
        long sanos = analisisFacade.contarSanos(idUsuario);
        if (sanos > 0) {
            r.put("Sanos", sanos);
        }
        for (Object[] fila : analisisFacade.contarPorEnfermedad(idUsuario)) {
            r.put((String) fila[0], ((Number) fila[1]).longValue());
        }
        return r;
    }

    /** Especies más escaneadas (nombre científico abreviado "A. bisporus") → total. */
    public Map<String, Long> especiesIdentificadas(int idUsuario, int max) {
        Map<String, Long> r = new LinkedHashMap<>();
        for (Object[] fila : analisisFacade.contarPorHongo(idUsuario, max)) {
            r.put(abreviar((String) fila[0]), ((Number) fila[1]).longValue());
        }
        return r;
    }

    /** Etiquetas de los últimos n meses ("Enero", "Febrero"...), del más antiguo al actual. */
    public List<String> ultimosMeses(int n) {
        List<String> r = new ArrayList<>();
        YearMonth actual = YearMonth.now();
        for (int i = n - 1; i >= 0; i--) {
            String m = actual.minusMonths(i).getMonth().getDisplayName(TextStyle.FULL, ES);
            r.add(Character.toUpperCase(m.charAt(0)) + m.substring(1));
        }
        return r;
    }

    /** Enfermedad → conteo por mes de los últimos n meses (alineado con ultimosMeses(n)). */
    public Map<String, long[]> enfermedadesPorMes(int idUsuario, int n) {
        ZoneId zona = ZoneId.systemDefault();
        YearMonth inicio = YearMonth.now().minusMonths(n - 1L);
        Date desde = Date.from(inicio.atDay(1).atStartOfDay(zona).toInstant());

        Map<String, long[]> r = new LinkedHashMap<>();
        for (Object[] fila : analisisFacade.enfermedadesDesde(idUsuario, desde)) {
            LocalDate f = ((Date) fila[0]).toInstant().atZone(zona).toLocalDate();
            int idx = (int) java.time.temporal.ChronoUnit.MONTHS.between(inicio, YearMonth.from(f));
            if (idx >= 0 && idx < n) {
                r.computeIfAbsent((String) fila[1], k -> new long[n])[idx]++;
            }
        }
        return r;
    }

    public long total(Map<String, Long> datos) {
        return datos.values().stream().mapToLong(Long::longValue).sum();
    }

    private static String abreviar(String cientifico) {
        if (cientifico == null) {
            return "";
        }
        String[] p = cientifico.trim().split("\\s+");
        return p.length >= 2 ? p[0].charAt(0) + ". " + p[1] : cientifico;
    }
}
