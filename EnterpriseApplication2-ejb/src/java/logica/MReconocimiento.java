package logica;

import dao.AnalisisFacade;
import dao.HongoFacade;
import dao.UsuarioFacade;
import dao.VersionModeloFacade;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import modelo.Analisis;
import modelo.AnalisisPK;
import modelo.Enfermedad;
import modelo.Hongo;
import modelo.Usuario;
import modelo.VersionModelo;

/**
 * Flujo principal de reconocimiento de hongos (equivale a ReconocimientoController +
 * componente Livewire file.upload de Laravel), ahora con persistencia en "analisis".
 *
 * @author crstvbl
 */
@Stateless
@LocalBean
public class MReconocimiento {

    private static final Logger LOG = Logger.getLogger(MReconocimiento.class.getName());

    /** Tamaño máximo de imagen: 10 MB (igual que la validación de Laravel max:10240). */
    public static final long TAMANO_MAXIMO = 10L * 1024 * 1024;

    @EJB
    private MUppaApi api;
    @EJB
    private MEnfermedad mEnfermedad;
    @EJB
    private HongoFacade hongoFacade;
    @EJB
    private AnalisisFacade analisisFacade;
    @EJB
    private UsuarioFacade usuarioFacade;
    @EJB
    private VersionModeloFacade versionModeloFacade;

    /**
     * Envía la imagen a la API, la enriquece con la BD y, si se identificó un hongo,
     * guarda la foto en disco y registra el análisis del usuario.
     */
    public ResultadoAnalisis analizar(byte[] imagen, String nombreArchivo, String contentType,
            Integer idUsuario, String ubicacion) throws UppaException {

        String extension = validarImagen(imagen, contentType);
        ResultadoAnalisis r = api.catalogarHongo(imagen, nombreArchivo, contentType);

        Hongo hongo = r.getIdEspecie() != null ? hongoFacade.find(r.getIdEspecie()) : null;
        r.setHongo(hongo);

        Enfermedad enfermedad = Boolean.FALSE.equals(r.getSano()) ? mEnfermedad.porEtiquetaModelo(r.getEstado()) : null;
        r.setEnfermedad(enfermedad);
        r.setTratamientos(mEnfermedad.tratamientos(enfermedad));

        if (r.isDetectado() && hongo != null && idUsuario != null) {
            Usuario usuario = usuarioFacade.find(idUsuario);
            VersionModelo version = versionModeloFacade.findByNombre(InicializadorDatos.VERSION_ESPECIES);
            if (usuario != null && version != null) {
                String archivo = guardarImagen(imagen, extension);

                Analisis a = new Analisis(new AnalisisPK(analisisFacade.siguienteId(),
                        hongo.getIdHongo(), version.getIdVersion()));
                a.setFecha(new Date());
                a.setImagenAnalizada(archivo);
                a.setUbicacion(recortar(ubicacion, 255));
                a.setPrecisionIdentificacion(decimal(r.getConfianzaPct()));
                a.setPrecisionDiagnostico(decimal(r.getConfianzaSalud()));
                a.setHongo(hongo);
                a.setVersionModelo(version);
                a.setIdUsuario(usuario);
                a.setIdEnfermedad(enfermedad);
                analisisFacade.create(a);

                if (enfermedad != null) {
                    mEnfermedad.vincularHongo(enfermedad, hongo);
                }
                r.setIdAnalisis(a.getAnalisisPK().getIdAnalisis());
                r.setImagen(archivo);
            } else {
                LOG.warning("No se guardó el análisis: falta el usuario o la versión del modelo en la BD.");
            }
        }
        return r;
    }

    public List<Analisis> recientes(int idUsuario, int max) {
        return analisisFacade.findByUsuario(idUsuario, max);
    }

    public List<Analisis> historial(int idUsuario) {
        return analisisFacade.findByUsuario(idUsuario, 0);
    }

    public Analisis buscar(int idAnalisis, int idUsuario) {
        return analisisFacade.findByIdYUsuario(idAnalisis, idUsuario);
    }

    public boolean imagenPerteneceA(String imagen, int idUsuario) {
        return analisisFacade.existeImagenDeUsuario(imagen, idUsuario);
    }

    /** Valida tamaño y tipo (jpg, jpeg, png, webp) y devuelve la extensión a usar. */
    private static String validarImagen(byte[] imagen, String contentType) throws UppaException {
        if (imagen == null || imagen.length == 0) {
            throw new UppaException("Selecciona o captura una foto antes de analizar.");
        }
        if (imagen.length > TAMANO_MAXIMO) {
            throw new UppaException("La imagen no debe superar los 10 MB.");
        }
        String tipo = contentType == null ? "" : contentType.toLowerCase();
        switch (tipo) {
            case "image/jpeg":
            case "image/jpg":
            case "image/pjpeg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/webp":
                return "webp";
            default:
                throw new UppaException("Formato no admitido. Usa una imagen JPG, PNG o WEBP.");
        }
    }

    private static String guardarImagen(byte[] imagen, String extension) throws UppaException {
        try {
            Path dir = Configuracion.directorioImagenes();
            Files.createDirectories(dir);
            String nombre = UUID.randomUUID() + "." + extension;
            Files.write(dir.resolve(nombre), imagen);
            return nombre;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "No se pudo guardar la imagen", e);
            throw new UppaException("No se pudo guardar la imagen en el servidor.", e);
        }
    }

    private static BigDecimal decimal(Double v) {
        return v == null ? null : BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP);
    }

    private static String recortar(String s, int max) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return s.length() > max ? s.substring(0, max) : s;
    }
}
