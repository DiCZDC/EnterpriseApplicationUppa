package filtros;

import CDIs.ASesion;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Protege las vistas internas (equivale al middleware ['auth'] de Laravel):
 * sin sesión iniciada se redirige a /auth/login.xhtml.
 *
 * @author crstvbl
 */
@WebFilter(filterName = "FiltroAutenticacion", urlPatterns = {"/faces/vistas/*", "/vistas/*", "/imagenes/*"})
public class FiltroAutenticacion extends HttpFilter {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpSession s = req.getSession(false);
        if (s != null && s.getAttribute(ASesion.ATRIBUTO_USUARIO) != null) {
            // Las páginas privadas no deben quedar en la caché del navegador tras cerrar sesión
            res.setHeader("Cache-Control", "no-store");
            chain.doFilter(req, res);
            return;
        }
        if (req.getRequestURI().startsWith(req.getContextPath() + "/imagenes/")) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        res.sendRedirect(req.getContextPath() + "/faces/auth/login.xhtml");
    }
}
