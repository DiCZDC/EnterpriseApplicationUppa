/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import modelo.VersionModelo;

/**
 *
 * @author daniel
 */
@Stateless
public class VersionModeloFacade extends AbstractFacade<VersionModelo> {

    @PersistenceContext(unitName = "EnterpriseApplication2-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VersionModeloFacade() {
        super(VersionModelo.class);
    }

    public VersionModelo findByNombre(String nombre) {
        java.util.List<VersionModelo> r = em.createNamedQuery("VersionModelo.findByNombre", VersionModelo.class)
                .setParameter("nombre", nombre).getResultList();
        return r.isEmpty() ? null : r.get(0);
    }
}
