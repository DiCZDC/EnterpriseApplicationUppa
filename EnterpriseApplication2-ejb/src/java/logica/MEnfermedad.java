/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package logica;

import dao.EnfermedadFacade;
import dao.TratamientoFacade;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import modelo.Enfermedad;
import modelo.Hongo;
import modelo.Tratamiento;

/**
 * Lógica de enfermedades y tratamientos.
 *
 * @author daniel
 */
@Stateless
@LocalBean
public class MEnfermedad {

    /**
     * Etiqueta de clase del modelo de salud (uppa_v1_yolo11n.pt → {0: moho_verde, 1: sano})
     * → nombre científico de la enfermedad en la tabla "enfermedad".
     */
    private static final Map<String, String> ETIQUETAS_MODELO = new HashMap<>();

    static {
        ETIQUETAS_MODELO.put("moho_verde", InicializadorDatos.TRICHODERMA);
    }

    @EJB
    private EnfermedadFacade enfermedadFacade;

    @EJB
    private TratamientoFacade tratamientoFacade;

    public List<Enfermedad> listar() {
        return enfermedadFacade.findAll();
    }

    /** Traduce la etiqueta "estado" que devuelve la API a la Enfermedad registrada; null si es "sano" o desconocida. */
    public Enfermedad porEtiquetaModelo(String etiqueta) {
        if (etiqueta == null || etiqueta.isBlank() || "sano".equalsIgnoreCase(etiqueta)) {
            return null;
        }
        String clave = etiqueta.trim().toLowerCase();
        String cientifico = ETIQUETAS_MODELO.get(clave);
        if (cientifico != null) {
            Enfermedad e = enfermedadFacade.findByNombreCientifico(cientifico);
            if (e != null) {
                return e;
            }
        }
        // Etiquetas nuevas del modelo: se buscan por nombre común ("moho verde" → "Moho verde")
        String comun = clave.replace('_', ' ');
        comun = Character.toUpperCase(comun.charAt(0)) + comun.substring(1);
        return enfermedadFacade.findByNombreComun(comun);
    }

    public List<Tratamiento> tratamientos(Enfermedad enfermedad) {
        if (enfermedad == null || enfermedad.getIdEnfermedad() == null) {
            return Collections.emptyList();
        }
        return tratamientoFacade.findByEnfermedad(enfermedad.getIdEnfermedad());
    }

    /** Registra la relación hongo_enfermedad la primera vez que se detecta. */
    public void vincularHongo(Enfermedad enfermedad, Hongo hongo) {
        Enfermedad e = enfermedadFacade.find(enfermedad.getIdEnfermedad());
        if (e == null || hongo == null) {
            return;
        }
        boolean existe = e.getHongoCollection().stream()
                .anyMatch(h -> h.getIdHongo().equals(hongo.getIdHongo()));
        if (!existe) {
            e.getHongoCollection().add(hongo);
            enfermedadFacade.edit(e);
        }
    }
}
