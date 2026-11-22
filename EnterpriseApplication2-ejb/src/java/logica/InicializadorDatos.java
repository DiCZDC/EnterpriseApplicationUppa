package logica;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import modelo.Enfermedad;
import modelo.Rol;
import modelo.Tratamiento;
import modelo.TratamientoPK;
import modelo.VersionModelo;

/**
 * Carga los datos de catálogo al desplegar, solo si las tablas están vacías
 * (equivale a los seeders de Laravel: EspecieSeeder + datos fijos del front).
 *
 *  - hongo:          210 especies de datos/especies.csv. id_hongo = id de clase del modelo YOLO
 *                    (my_model.pt), por eso se insertan con id explícito.
 *  - enfermedad:     Trichoderma (moho verde), única clase de enfermedad del modelo de salud.
 *  - tratamiento:    recomendaciones que mostraba el front de Laravel.
 *  - version_modelo: los dos modelos YOLO del backend Python.
 *  - rol:            usuario y administrador.
 *
 * @author crstvbl
 */
@Singleton
@Startup
public class InicializadorDatos {

    private static final Logger LOG = Logger.getLogger(InicializadorDatos.class.getName());

    public static final String ROL_USUARIO = "usuario";
    public static final String ROL_ADMIN = "administrador";
    public static final String VERSION_ESPECIES = "Detector de especies (my_model.pt)";
    public static final String VERSION_SALUD = "Detector de salud (uppa_v1_yolo11n.pt)";
    public static final String TRICHODERMA = "Trichoderma spp.";

    @PersistenceContext(unitName = "EnterpriseApplication2-ejbPU")
    private EntityManager em;

    @PostConstruct
    public void inicializar() {
        try {
            roles();
            versiones();
            enfermedades();
            hongos();
        } catch (RuntimeException e) {
            // No se bloquea el despliegue: se registra y la app sigue arrancando.
            LOG.log(Level.SEVERE, "No se pudieron cargar los datos iniciales de Úppa. "
                    + "¿Existe la BD 'uppa' con el esquema de db/01_esquema.sql?", e);
        }
    }

    private boolean vacia(String entidad) {
        return em.createQuery("SELECT COUNT(e) FROM " + entidad + " e", Long.class).getSingleResult() == 0;
    }

    private void roles() {
        if (!vacia("Rol")) {
            return;
        }
        Rol u = new Rol();
        u.setNombre(ROL_USUARIO);
        u.setDescripcion("Usuario registrado: puede analizar hongos y consultar su historial.");
        em.persist(u);
        Rol a = new Rol();
        a.setNombre(ROL_ADMIN);
        a.setDescripcion("Administrador de catálogos (hongos, enfermedades y tratamientos).");
        em.persist(a);
        LOG.info("Úppa: roles iniciales creados.");
    }

    private void versiones() {
        if (!vacia("VersionModelo")) {
            return;
        }
        // Épocas y precisión (metrics/precision(B)) leídas de los checkpoints .pt; tamaño del dataset desconocido (0).
        em.persist(new VersionModelo(null, VERSION_ESPECIES, "YOLO 8.4.47", 10, 0, new BigDecimal("53.00")));
        em.persist(new VersionModelo(null, VERSION_SALUD, "YOLO11n v1", 50, 0, new BigDecimal("94.20")));
        LOG.info("Úppa: versiones de modelo creadas.");
    }

    private void enfermedades() {
        if (!vacia("Enfermedad")) {
            return;
        }
        Enfermedad e = new Enfermedad(null, TRICHODERMA, "Moho verde");
        e.setInformacion("Hongo competidor (Trichoderma) que produce un moho verde sobre el sustrato y los "
                + "cuerpos fructíferos. Se propaga rápidamente por esporas, sobre todo con alta humedad y poca ventilación.");
        em.persist(e);
        em.flush();

        int id = e.getIdEnfermedad();
        tratamiento(1, id, "Urgente", "Bolsa o recipiente sellable, guantes.",
                "Aislamiento inmediato: separa este espécimen de los demás hongos sanos de inmediato. "
                + "El moho verde (Trichoderma) se esparce rápidamente a través de esporas.");
        tratamiento(2, id, "Alta", "Alcohol al 70 % o solución de hipoclorito, paños limpios.",
                "Limpieza y desinfección: limpia tus herramientas y el área de contacto para evitar "
                + "infectar próximos cultivos o recolecciones cercanas.");
        tratamiento(3, id, "Preventiva", "Termómetro e higrómetro.",
                "Monitoreo continuo: si forma parte de un cultivo, verifica la temperatura y humedad, "
                + "ya que el moho verde suele prosperar con poca ventilación y mucha humedad.");
        LOG.info("Úppa: enfermedades y tratamientos creados.");
    }

    private void tratamiento(int idTratamiento, int idEnfermedad, String triaje, String requisitos, String pasos) {
        Tratamiento t = new Tratamiento(new TratamientoPK(idTratamiento, idEnfermedad));
        t.setTriaje(triaje);
        t.setRequisitos(requisitos);
        t.setPasos(pasos);
        t.setFuente("Úppa — recomendaciones generales");
        t.setEnfermedad(em.find(Enfermedad.class, idEnfermedad));
        em.persist(t);
    }

    private void hongos() {
        if (!vacia("Hongo")) {
            return;
        }
        InputStream in = InicializadorDatos.class.getResourceAsStream("/datos/especies.csv");
        if (in == null) {
            LOG.warning("Úppa: no se encontró /datos/especies.csv en el EJB; la tabla hongo queda vacía.");
            return;
        }
        int n = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            br.readLine(); // cabecera: ID,Nombre Científico,Nombre Común,Familia,Zonas de Crecimiento,Ambientes Comunes
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }
                String[] c = linea.split(",", -1);
                if (c.length < 6) {
                    continue;
                }
                em.createNativeQuery("INSERT INTO hongo (id_hongo, familia, nombre_cientifico, nombre_comun, region, informacion) "
                        + "VALUES (?, ?, ?, ?, ?, ?)")
                        .setParameter(1, Integer.parseInt(c[0].trim()))
                        .setParameter(2, c[3].trim())
                        .setParameter(3, c[1].trim())
                        .setParameter(4, c[2].isBlank() ? c[1].trim() : c[2].trim())
                        .setParameter(5, c[4].isBlank() ? "Desconocida" : c[4].trim())
                        .setParameter(6, c[5].isBlank() ? null : "Ambientes comunes: " + c[5].trim())
                        .executeUpdate();
                n++;
            }
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Error leyendo especies.csv", e);
        }
        // Ajusta la secuencia para que futuros INSERT sin id no choquen con los ids explícitos
        em.createNativeQuery("SELECT setval('hongo_id_hongo_seq', (SELECT MAX(id_hongo) FROM hongo))").getSingleResult();
        LOG.log(Level.INFO, "Úppa: {0} especies de hongo cargadas.", n);
    }
}
