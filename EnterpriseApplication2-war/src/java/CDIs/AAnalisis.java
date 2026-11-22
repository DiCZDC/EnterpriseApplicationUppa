package CDIs;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import logica.MReconocimiento;
import modelo.Analisis;

/**
 * Análisis guardados del usuario: recientes (dashboard), historial y detalle (show).
 *
 * @author crstvbl
 */
@Named("aAnalisis")
@ViewScoped
public class AAnalisis implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private MReconocimiento mReconocimiento;

    @Inject
    private ASesion sesion;

    /** Parámetro ?id= de vistas/reconocimiento/show.xhtml */
    private Integer id;
    private Analisis analisis;
    private List<Analisis> recientes;
    private List<Analisis> historial;

    /** f:viewAction de show.xhtml */
    public void cargar() {
        if (id != null && sesion.isAutenticado()) {
            analisis = mReconocimiento.buscar(id, sesion.getIdUsuario());
        }
    }

    public List<Analisis> getRecientes() {
        if (recientes == null) {
            recientes = mReconocimiento.recientes(sesion.getIdUsuario(), 4);
        }
        return recientes;
    }

    public List<Analisis> getHistorial() {
        if (historial == null) {
            historial = mReconocimiento.historial(sesion.getIdUsuario());
        }
        return historial;
    }

    public String urlImagen(Analisis a) {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()
                + "/imagenes/" + a.getImagenAnalizada();
    }

    public String nivel(BigDecimal p) {
        if (p == null) {
            return "";
        }
        double v = p.doubleValue();
        return v >= 80 ? "Excelente" : v >= 60 ? "Buena" : v >= 50 ? "Regular" : "Baja";
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Analisis getAnalisis() { return analisis; }
}
