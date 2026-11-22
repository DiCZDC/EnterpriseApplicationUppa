/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import modelo.Enfermedad;

/**
 *
 * @author daniel
 */
@Stateless
public class EnfermedadFacade extends AbstractFacade<Enfermedad> {

    @PersistenceContext(unitName = "EnterpriseApplication2-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EnfermedadFacade() {
        super(Enfermedad.class);
    }

    public Enfermedad findByNombreCientifico(String nombreCientifico) {
        java.util.List<Enfermedad> r = em.createNamedQuery("Enfermedad.findByNombreCientifico", Enfermedad.class)
                .setParameter("nombreCientifico", nombreCientifico).getResultList();
        return r.isEmpty() ? null : r.get(0);
    }

    public Enfermedad findByNombreComun(String nombreComun) {
        java.util.List<Enfermedad> r = em.createNamedQuery("Enfermedad.findByNombreComun", Enfermedad.class)
                .setParameter("nombreComun", nombreComun).getResultList();
        return r.isEmpty() ? null : r.get(0);
    }
}
