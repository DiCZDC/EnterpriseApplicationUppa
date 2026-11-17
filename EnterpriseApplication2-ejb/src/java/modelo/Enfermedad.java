/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author daniel
 */
@Entity
@Table(name = "enfermedad")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Enfermedad.findAll", query = "SELECT e FROM Enfermedad e"),
    @NamedQuery(name = "Enfermedad.findByIdEnfermedad", query = "SELECT e FROM Enfermedad e WHERE e.idEnfermedad = :idEnfermedad"),
    @NamedQuery(name = "Enfermedad.findByNombreCientifico", query = "SELECT e FROM Enfermedad e WHERE e.nombreCientifico = :nombreCientifico"),
    @NamedQuery(name = "Enfermedad.findByNombreComun", query = "SELECT e FROM Enfermedad e WHERE e.nombreComun = :nombreComun"),
    @NamedQuery(name = "Enfermedad.findByInformacion", query = "SELECT e FROM Enfermedad e WHERE e.informacion = :informacion")})
public class Enfermedad implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_enfermedad")
    private Integer idEnfermedad;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "nombre_cientifico")
    private String nombreCientifico;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "nombre_comun")
    private String nombreComun;
    @Size(max = 2147483647)
    @Column(name = "informacion")
    private String informacion;
    @JoinTable(name = "hongo_enfermedad", joinColumns = {
        @JoinColumn(name = "id_enfermedad", referencedColumnName = "id_enfermedad")}, inverseJoinColumns = {
        @JoinColumn(name = "id_hongo", referencedColumnName = "id_hongo")})
    @ManyToMany
    private Collection<Hongo> hongoCollection;
    @OneToMany(mappedBy = "idEnfermedad")
    private Collection<Analisis> analisisCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "enfermedad")
    private Collection<Tratamiento> tratamientoCollection;

    public Enfermedad() {
    }

    public Enfermedad(Integer idEnfermedad) {
        this.idEnfermedad = idEnfermedad;
    }

    public Enfermedad(Integer idEnfermedad, String nombreCientifico, String nombreComun) {
        this.idEnfermedad = idEnfermedad;
        this.nombreCientifico = nombreCientifico;
        this.nombreComun = nombreComun;
    }

    public Integer getIdEnfermedad() {
        return idEnfermedad;
    }

    public void setIdEnfermedad(Integer idEnfermedad) {
        this.idEnfermedad = idEnfermedad;
    }

    public String getNombreCientifico() {
        return nombreCientifico;
    }

    public void setNombreCientifico(String nombreCientifico) {
        this.nombreCientifico = nombreCientifico;
    }

    public String getNombreComun() {
        return nombreComun;
    }

    public void setNombreComun(String nombreComun) {
        this.nombreComun = nombreComun;
    }

    public String getInformacion() {
        return informacion;
    }

    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }

    @XmlTransient
    public Collection<Hongo> getHongoCollection() {
        return hongoCollection;
    }

    public void setHongoCollection(Collection<Hongo> hongoCollection) {
        this.hongoCollection = hongoCollection;
    }

    @XmlTransient
    public Collection<Analisis> getAnalisisCollection() {
        return analisisCollection;
    }

    public void setAnalisisCollection(Collection<Analisis> analisisCollection) {
        this.analisisCollection = analisisCollection;
    }

    @XmlTransient
    public Collection<Tratamiento> getTratamientoCollection() {
        return tratamientoCollection;
    }

    public void setTratamientoCollection(Collection<Tratamiento> tratamientoCollection) {
        this.tratamientoCollection = tratamientoCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idEnfermedad != null ? idEnfermedad.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Enfermedad)) {
            return false;
        }
        Enfermedad other = (Enfermedad) object;
        if ((this.idEnfermedad == null && other.idEnfermedad != null) || (this.idEnfermedad != null && !this.idEnfermedad.equals(other.idEnfermedad))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.Enfermedad[ idEnfermedad=" + idEnfermedad + " ]";
    }
    
}
