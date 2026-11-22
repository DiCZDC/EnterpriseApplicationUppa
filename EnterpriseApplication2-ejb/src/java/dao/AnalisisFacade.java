/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import modelo.Analisis;

/**
 *
 * @author daniel
 */
@Stateless
public class AnalisisFacade extends AbstractFacade<Analisis> {

    @PersistenceContext(unitName = "EnterpriseApplication2-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AnalisisFacade() {
        super(Analisis.class);
    }

    /** id_analisis no tiene secuencia en la BD (forma parte de una PK compuesta): se calcula MAX + 1. */
    public int siguienteId() {
        Integer max = em.createQuery("SELECT MAX(a.analisisPK.idAnalisis) FROM Analisis a", Integer.class)
                .getSingleResult();
        return max == null ? 1 : max + 1;
    }

    /** Análisis de un usuario, del más reciente al más antiguo. max <= 0 = sin límite. */
    public List<Analisis> findByUsuario(int idUsuario, int max) {
        javax.persistence.TypedQuery<Analisis> q = em.createQuery(
                "SELECT a FROM Analisis a WHERE a.idUsuario.idUsuario = :u ORDER BY a.fecha DESC", Analisis.class)
                .setParameter("u", idUsuario);
        if (max > 0) {
            q.setMaxResults(max);
        }
        return q.getResultList();
    }

    /** Busca un análisis por su id lógico, restringido al usuario propietario. */
    public Analisis findByIdYUsuario(int idAnalisis, int idUsuario) {
        List<Analisis> r = em.createQuery(
                "SELECT a FROM Analisis a WHERE a.analisisPK.idAnalisis = :id AND a.idUsuario.idUsuario = :u", Analisis.class)
                .setParameter("id", idAnalisis)
                .setParameter("u", idUsuario)
                .getResultList();
        return r.isEmpty() ? null : r.get(0);
    }

    public boolean existeImagenDeUsuario(String imagen, int idUsuario) {
        Long n = em.createQuery(
                "SELECT COUNT(a) FROM Analisis a WHERE a.imagenAnalizada = :img AND a.idUsuario.idUsuario = :u", Long.class)
                .setParameter("img", imagen)
                .setParameter("u", idUsuario)
                .getSingleResult();
        return n > 0;
    }

    // ───────────── Consultas para las gráficas ─────────────

    public long contarSanos(int idUsuario) {
        return em.createQuery(
                "SELECT COUNT(a) FROM Analisis a WHERE a.idEnfermedad IS NULL AND a.idUsuario.idUsuario = :u", Long.class)
                .setParameter("u", idUsuario)
                .getSingleResult();
    }

    /** Filas [nombreComunEnfermedad (String), total (Long)]. */
    public List<Object[]> contarPorEnfermedad(int idUsuario) {
        return em.createQuery(
                "SELECT e.nombreComun, COUNT(a) FROM Analisis a JOIN a.idEnfermedad e "
                + "WHERE a.idUsuario.idUsuario = :u GROUP BY e.nombreComun ORDER BY COUNT(a) DESC", Object[].class)
                .setParameter("u", idUsuario)
                .getResultList();
    }

    /** Filas [nombreCientificoHongo (String), total (Long)], las max especies más escaneadas. */
    public List<Object[]> contarPorHongo(int idUsuario, int max) {
        return em.createQuery(
                "SELECT h.nombreCientifico, COUNT(a) FROM Analisis a JOIN a.hongo h "
                + "WHERE a.idUsuario.idUsuario = :u GROUP BY h.nombreCientifico ORDER BY COUNT(a) DESC", Object[].class)
                .setParameter("u", idUsuario)
                .setMaxResults(max)
                .getResultList();
    }

    /** Filas [fecha (Date), nombreComunEnfermedad (String)] de análisis con enfermedad desde una fecha. */
    public List<Object[]> enfermedadesDesde(int idUsuario, Date desde) {
        return em.createQuery(
                "SELECT a.fecha, e.nombreComun FROM Analisis a JOIN a.idEnfermedad e "
                + "WHERE a.idUsuario.idUsuario = :u AND a.fecha >= :desde", Object[].class)
                .setParameter("u", idUsuario)
                .setParameter("desde", desde)
                .getResultList();
    }
}
