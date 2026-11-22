/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package CDIs;

import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import logica.MEnfermedad;
import modelo.Enfermedad;
import modelo.Tratamiento;

/**
 * Enfermedades y sus tratamientos para las vistas.
 *
 * @author daniel
 */
@Named(value = "aEnfermedad")
@RequestScoped
public class AEnfermedad {

    @EJB
    private MEnfermedad mEnfermedad;

    /**
     * Creates a new instance of AEnfermedad
     */
    public AEnfermedad() {
    }

    public List<Enfermedad> getEnfermedades() {
        return mEnfermedad.listar();
    }

    /** Uso en EL: #{aEnfermedad.tratamientos(analisis.idEnfermedad)} */
    public List<Tratamiento> tratamientos(Enfermedad enfermedad) {
        return mEnfermedad.tratamientos(enfermedad);
    }
}
