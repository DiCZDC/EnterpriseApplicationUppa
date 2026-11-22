/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import modelo.Usuario;

/**
 *
 * @author daniel
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {

    @PersistenceContext(unitName = "EnterpriseApplication2-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }

    public Usuario findByEmail(String email) {
        java.util.List<Usuario> r = em.createNamedQuery("Usuario.findByEmail", Usuario.class)
                .setParameter("email", email).getResultList();
        return r.isEmpty() ? null : r.get(0);
    }

    public Usuario findByNombreUsuario(String nombreUsuario) {
        java.util.List<Usuario> r = em.createNamedQuery("Usuario.findByNombreUsuario", Usuario.class)
                .setParameter("nombreUsuario", nombreUsuario).getResultList();
        return r.isEmpty() ? null : r.get(0);
    }
}
