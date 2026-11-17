/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author daniel
 */
@Entity
@Table(name = "tratamiento")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tratamiento.findAll", query = "SELECT t FROM Tratamiento t"),
    @NamedQuery(name = "Tratamiento.findByIdTratamiento", query = "SELECT t FROM Tratamiento t WHERE t.tratamientoPK.idTratamiento = :idTratamiento"),
    @NamedQuery(name = "Tratamiento.findByIdEnfermedad", query = "SELECT t FROM Tratamiento t WHERE t.tratamientoPK.idEnfermedad = :idEnfermedad"),
    @NamedQuery(name = "Tratamiento.findByRequisitos", query = "SELECT t FROM Tratamiento t WHERE t.requisitos = :requisitos"),
    @NamedQuery(name = "Tratamiento.findByTriaje", query = "SELECT t FROM Tratamiento t WHERE t.triaje = :triaje"),
    @NamedQuery(name = "Tratamiento.findByPasos", query = "SELECT t FROM Tratamiento t WHERE t.pasos = :pasos"),
    @NamedQuery(name = "Tratamiento.findByFuente", query = "SELECT t FROM Tratamiento t WHERE t.fuente = :fuente")})
public class Tratamiento implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TratamientoPK tratamientoPK;
    @Size(max = 2147483647)
    @Column(name = "requisitos")
    private String requisitos;
    @Size(max = 20)
    @Column(name = "triaje")
    private String triaje;
    @Size(max = 2147483647)
    @Column(name = "pasos")
    private String pasos;
    @Size(max = 255)
    @Column(name = "fuente")
    private String fuente;
    @JoinColumn(name = "id_enfermedad", referencedColumnName = "id_enfermedad", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Enfermedad enfermedad;

    public Tratamiento() {
    }

    public Tratamiento(TratamientoPK tratamientoPK) {
        this.tratamientoPK = tratamientoPK;
    }

    public Tratamiento(int idTratamiento, int idEnfermedad) {
        this.tratamientoPK = new TratamientoPK(idTratamiento, idEnfermedad);
    }

    public TratamientoPK getTratamientoPK() {
        return tratamientoPK;
    }

    public void setTratamientoPK(TratamientoPK tratamientoPK) {
        this.tratamientoPK = tratamientoPK;
    }

    public String getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(String requisitos) {
        this.requisitos = requisitos;
    }

    public String getTriaje() {
        return triaje;
    }

    public void setTriaje(String triaje) {
        this.triaje = triaje;
    }

    public String getPasos() {
        return pasos;
    }

    public void setPasos(String pasos) {
        this.pasos = pasos;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public Enfermedad getEnfermedad() {
        return enfermedad;
    }

    public void setEnfermedad(Enfermedad enfermedad) {
        this.enfermedad = enfermedad;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tratamientoPK != null ? tratamientoPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tratamiento)) {
            return false;
        }
        Tratamiento other = (Tratamiento) object;
        if ((this.tratamientoPK == null && other.tratamientoPK != null) || (this.tratamientoPK != null && !this.tratamientoPK.equals(other.tratamientoPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.Tratamiento[ tratamientoPK=" + tratamientoPK + " ]";
    }
    
}
