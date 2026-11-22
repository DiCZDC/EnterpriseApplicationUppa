package servlets;

import CDIs.ASesion;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logica.Configuracion;
import logica.MReconocimiento;

/**
 * Sirve las fotos analizadas guardadas en disco (Configuracion.directorioImagenes()).
 * Solo entrega imágenes que pertenecen a un análisis del usuario en sesión.
 *
 * @author crstvbl
 */
@WebServlet(name = "ImagenServlet", urlPatterns = {"/imagenes/*"})
public class ImagenServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Pattern NOMBRE = Pattern.compile("^[0-9a-fA-F-]{36}\\.(jpg|png|webp)$");

    @EJB
    private MReconocimiento mReconocimiento;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String info = req.getPathInfo();
        String nombre = info == null ? "" : info.substring(1);
        Object idUsuario = req.getSession(false) == null ? null
                : req.getSession(false).getAttribute(ASesion.ATRIBUTO_USUARIO);

        if (!NOMBRE.matcher(nombre).matches() || idUsuario == null
                || !mReconocimiento.imagenPerteneceA(nombre, (Integer) idUsuario)) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Path archivo = Configuracion.directorioImagenes().resolve(nombre);
        if (!Files.isRegularFile(archivo)) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String ext = nombre.substring(nombre.lastIndexOf('.') + 1);
        res.setContentType("jpg".equals(ext) ? "image/jpeg" : "image/" + ext);
        res.setContentLengthLong(Files.size(archivo));
        res.setHeader("Cache-Control", "private, max-age=86400");
        Files.copy(archivo, res.getOutputStream());
    }
}
