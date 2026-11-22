package CDIs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Base64;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;
import logica.MReconocimiento;
import logica.ResultadoAnalisis;
import logica.UppaException;

/**
 * Pantalla de reconocimiento (vistas/reconocimiento/index.xhtml).
 * Equivale al componente Livewire file.upload de Laravel: sube/captura foto → analizar → resultado.
 *
 * @author crstvbl
 */
@Named("aReconocimiento")
@ViewScoped
public class AReconocimiento implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private MReconocimiento mReconocimiento;

    @Inject
    private ASesion sesion;

    private transient Part foto;
    private ResultadoAnalisis resultado;
    /** Vista previa cuando la foto no se guardó en disco (no se detectó hongo). */
    private String fotoDataUri;

    public void analizar() {
        if (foto == null || foto.getSize() == 0) {
            error("Selecciona o captura una foto antes de analizar.");
            return;
        }
        if (foto.getSize() > MReconocimiento.TAMANO_MAXIMO) {
            error("La imagen no debe superar los 10 MB.");
            return;
        }
        try {
            byte[] datos = leer(foto);
            resultado = mReconocimiento.analizar(datos, foto.getSubmittedFileName(), foto.getContentType(),
                    sesion.getIdUsuario(), null);
            fotoDataUri = resultado.getImagen() == null
                    ? "data:" + foto.getContentType() + ";base64," + Base64.getEncoder().encodeToString(datos)
                    : null;
        } catch (UppaException e) {
            error(e.getMessage());
        } catch (IOException e) {
            error("No se pudo leer la imagen enviada.");
        } finally {
            foto = null;
        }
    }

    public void nuevoEscaneo() {
        resultado = null;
        fotoDataUri = null;
        foto = null;
    }

    /** URL de la foto del resultado: servida por ImagenServlet o como data URI. */
    public String getUrlFoto() {
        if (resultado != null && resultado.getImagen() != null) {
            return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()
                    + "/imagenes/" + resultado.getImagen();
        }
        return fotoDataUri;
    }

    private static byte[] leer(Part p) throws IOException {
        try (InputStream in = p.getInputStream(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            in.transferTo(out);
            return out.toByteArray();
        }
    }

    private static void error(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, null));
    }

    public Part getFoto() { return foto; }
    public void setFoto(Part foto) { this.foto = foto; }
    public ResultadoAnalisis getResultado() { return resultado; }
}
