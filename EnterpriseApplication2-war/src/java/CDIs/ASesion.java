package CDIs;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import logica.MUsuario;
import logica.UppaException;
import modelo.Usuario;

/**
 * Sesión del usuario: inicio de sesión, registro y cierre (reemplaza Laravel Fortify).
 *
 * @author crstvbl
 */
@Named("aSesion")
@SessionScoped
public class ASesion implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Atributo de sesión HTTP que revisa FiltroAutenticacion. */
    public static final String ATRIBUTO_USUARIO = "uppa.idUsuario";

    @EJB
    private MUsuario mUsuario;

    // Usuario autenticado (solo datos básicos, no la entidad completa)
    private Integer idUsuario;
    private String nombre;
    private String nombreUsuario;
    private String email;

    // Formulario de inicio de sesión
    private String identificador;
    private String contrasena;

    // Formulario de registro
    private String regNombre;
    private String regNombreUsuario;
    private String regEmail;
    private String regContrasena;
    private String regConfirmacion;

    public String iniciarSesion() {
        try {
            Usuario u = mUsuario.autenticar(identificador, contrasena);
            establecer(u);
            identificador = null;
            return "/vistas/dashboard?faces-redirect=true";
        } catch (UppaException e) {
            error(e.getMessage());
            return null;
        } finally {
            contrasena = null;
        }
    }

    public String registrar() {
        try {
            Usuario u = mUsuario.registrar(regNombre, regNombreUsuario, regEmail, regContrasena, regConfirmacion);
            establecer(u);
            regNombre = regNombreUsuario = regEmail = null;
            return "/vistas/dashboard?faces-redirect=true";
        } catch (UppaException e) {
            error(e.getMessage());
            return null;
        } finally {
            regContrasena = regConfirmacion = null;
        }
    }

    public String cerrarSesion() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/index?faces-redirect=true";
    }

    private void establecer(Usuario u) {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        // Evita fijación de sesión: nuevo id de sesión tras autenticarse
        ((HttpServletRequest) ec.getRequest()).changeSessionId();
        idUsuario = u.getIdUsuario();
        nombre = u.getNombre();
        nombreUsuario = u.getNombreUsuario();
        email = u.getEmail();
        ec.getSessionMap().put(ATRIBUTO_USUARIO, idUsuario);
    }

    private static void error(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, null));
    }

    public boolean isAutenticado() {
        return idUsuario != null;
    }

    /** Iniciales del nombre (equivale a User::initials() de Laravel). */
    public String getIniciales() {
        if (nombre == null || nombre.isBlank()) {
            return "?";
        }
        StringBuilder sb = new StringBuilder();
        for (String p : nombre.trim().split("\\s+")) {
            if (sb.length() < 2 && !p.isEmpty()) {
                sb.append(Character.toUpperCase(p.charAt(0)));
            }
        }
        return sb.toString();
    }

    public Integer getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getEmail() { return email; }

    public String getIdentificador() { return identificador; }
    public void setIdentificador(String identificador) { this.identificador = identificador; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public String getRegNombre() { return regNombre; }
    public void setRegNombre(String regNombre) { this.regNombre = regNombre; }
    public String getRegNombreUsuario() { return regNombreUsuario; }
    public void setRegNombreUsuario(String regNombreUsuario) { this.regNombreUsuario = regNombreUsuario; }
    public String getRegEmail() { return regEmail; }
    public void setRegEmail(String regEmail) { this.regEmail = regEmail; }
    public String getRegContrasena() { return regContrasena; }
    public void setRegContrasena(String regContrasena) { this.regContrasena = regContrasena; }
    public String getRegConfirmacion() { return regConfirmacion; }
    public void setRegConfirmacion(String regConfirmacion) { this.regConfirmacion = regConfirmacion; }
}
