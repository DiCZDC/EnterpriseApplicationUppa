/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import modelo.Rol;

/**
 *
 * @author daniel
 */
@Stateless
public class RolFacade extends AbstractFacade<Rol> {

    @PersistenceContext(unitName = "EnterpriseApplication2-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RolFacade() {
        super(Rol.class);
    }

    public Rol findByNombre(String nombre) {
        java.util.List<Rol> r = em.createNamedQuery("Rol.findByNombre", Rol.class)
                .setParameter("nombre", nombre).getResultList();
        return r.isEmpty() ? null : r.get(0);
    }
}
