/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import modelo.Tratamiento;

/**
 *
 * @author daniel
 */
@Stateless
public class TratamientoFacade extends AbstractFacade<Tratamiento> {

    @PersistenceContext(unitName = "EnterpriseApplication2-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TratamientoFacade() {
        super(Tratamiento.class);
    }

    /** Tratamientos de una enfermedad ordenados por su id (orden de los pasos). */
    public java.util.List<Tratamiento> findByEnfermedad(int idEnfermedad) {
        return em.createQuery("SELECT t FROM Tratamiento t WHERE t.tratamientoPK.idEnfermedad = :e "
                + "ORDER BY t.tratamientoPK.idTratamiento", Tratamiento.class)
                .setParameter("e", idEnfermedad).getResultList();
    }
}
