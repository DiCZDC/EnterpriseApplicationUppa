package logica;

import dao.CredencialFacade;
import dao.RolFacade;
import dao.UsuarioFacade;
import dao.UsuarioRolFacade;
import java.util.Date;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import modelo.Credencial;
import modelo.Rol;
import modelo.Usuario;
import modelo.UsuarioRol;

/**
 * Registro y autenticación de usuarios (reemplaza Laravel Fortify).
 * Usa las tablas usuario, credencial, rol y usuario_rol.
 *
 * @author crstvbl
 */
@Stateless
@LocalBean
public class MUsuario {

    /** Igual que el RateLimiter de Fortify: 5 intentos → bloqueo de 1 minuto. */
    private static final int MAX_INTENTOS = 5;
    private static final long BLOQUEO_MS = 60_000L;

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern NOMBRE_USUARIO = Pattern.compile("^[A-Za-z0-9._-]{3,100}$");

    @EJB
    private UsuarioFacade usuarioFacade;
    @EJB
    private CredencialFacade credencialFacade;
    @EJB
    private RolFacade rolFacade;
    @EJB
    private UsuarioRolFacade usuarioRolFacade;

    public Usuario registrar(String nombre, String nombreUsuario, String email, String contrasena,
            String confirmacion) throws UppaException {

        nombre = limpiar(nombre);
        nombreUsuario = limpiar(nombreUsuario);
        email = limpiar(email) == null ? null : limpiar(email).toLowerCase();

        if (nombre == null || nombre.length() > 150) {
            throw new UppaException("Escribe tu nombre (máximo 150 caracteres).");
        }
        if (nombreUsuario == null || !NOMBRE_USUARIO.matcher(nombreUsuario).matches()) {
            throw new UppaException("El nombre de usuario debe tener de 3 a 100 caracteres: letras, números, punto, guion o guion bajo.");
        }
        if (email == null || email.length() > 255 || !EMAIL.matcher(email).matches()) {
            throw new UppaException("Escribe un correo electrónico válido.");
        }
        validarContrasena(contrasena, confirmacion);
        if (usuarioFacade.findByEmail(email) != null) {
            throw new UppaException("Ya existe una cuenta con ese correo electrónico.");
        }
        if (usuarioFacade.findByNombreUsuario(nombreUsuario) != null) {
            throw new UppaException("Ese nombre de usuario ya está en uso.");
        }

        Date ahora = new Date();
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setNombreUsuario(nombreUsuario);
        u.setEmail(email);
        u.setFechaRegistro(ahora);
        u.setEstado("activo");
        u.setEmailVerificado(false);
        usuarioFacade.create(u);
        usuarioFacade.flush(); // obtiene id_usuario (IDENTITY)

        String salt = Contrasenas.nuevoSalt();
        Credencial c = new Credencial(u.getIdUsuario(), Contrasenas.hash(contrasena, salt), salt,
                Contrasenas.ALGORITMO, false, 0);
        c.setFechaCambioPassword(ahora);
        c.setUsuario(u);
        credencialFacade.create(c);

        Rol rol = rolFacade.findByNombre(InicializadorDatos.ROL_USUARIO);
        if (rol != null) {
            UsuarioRol ur = new UsuarioRol(u.getIdUsuario(), rol.getIdRol());
            ur.setFechaAsignacion(ahora);
            ur.setUsuario(u);
            ur.setRol(rol);
            usuarioRolFacade.create(ur);
        }
        return u;
    }

    /** @param identificador correo electrónico o nombre de usuario */
    public Usuario autenticar(String identificador, String contrasena) throws UppaException {
        String id = limpiar(identificador);
        if (id == null || contrasena == null || contrasena.isEmpty()) {
            throw new UppaException("Escribe tu correo (o usuario) y tu contraseña.");
        }
        Usuario u = id.contains("@") ? usuarioFacade.findByEmail(id.toLowerCase()) : usuarioFacade.findByNombreUsuario(id);
        Credencial c = u == null ? null : credencialFacade.find(u.getIdUsuario());
        if (c == null) {
            throw new UppaException("Estas credenciales no coinciden con nuestros registros.");
        }

        Date ahora = new Date();
        if (c.getBloqueadoHasta() != null && c.getBloqueadoHasta().after(ahora)) {
            long seg = Math.max(1, (c.getBloqueadoHasta().getTime() - ahora.getTime()) / 1000);
            throw new UppaException("Demasiados intentos. Inténtalo de nuevo en " + seg + " segundos.");
        }
        if (!"activo".equalsIgnoreCase(u.getEstado())) {
            throw new UppaException("Tu cuenta no está activa.");
        }

        if (!Contrasenas.verificar(contrasena, c.getSalt(), c.getPasswordHash())) {
            int intentos = c.getIntentosFallidos() + 1;
            if (intentos >= MAX_INTENTOS) {
                c.setBloqueadoHasta(new Date(ahora.getTime() + BLOQUEO_MS));
                intentos = 0;
            }
            c.setIntentosFallidos(intentos);
            credencialFacade.edit(c);
            throw new UppaException("Estas credenciales no coinciden con nuestros registros.");
        }

        c.setIntentosFallidos(0);
        c.setBloqueadoHasta(null);
        c.setUltimoAcceso(ahora);
        credencialFacade.edit(c);
        return u;
    }

    private static void validarContrasena(String contrasena, String confirmacion) throws UppaException {
        if (contrasena == null || contrasena.length() < 8) {
            throw new UppaException("La contraseña debe tener al menos 8 caracteres.");
        }
        if (!contrasena.matches(".*[A-Za-z].*") || !contrasena.matches(".*\\d.*")) {
            throw new UppaException("La contraseña debe incluir letras y números.");
        }
        if (!contrasena.equals(confirmacion)) {
            throw new UppaException("La confirmación de la contraseña no coincide.");
        }
    }

    private static String limpiar(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
