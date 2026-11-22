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
 * @author crstvbl
 */
@Entity
@Table(name = "hongo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Hongo.findAll", query = "SELECT h FROM Hongo h"),
    @NamedQuery(name = "Hongo.findByIdHongo", query = "SELECT h FROM Hongo h WHERE h.idHongo = :idHongo"),
    @NamedQuery(name = "Hongo.findByFamilia", query = "SELECT h FROM Hongo h WHERE h.familia = :familia"),
    @NamedQuery(name = "Hongo.findByNombreCientifico", query = "SELECT h FROM Hongo h WHERE h.nombreCientifico = :nombreCientifico"),
    @NamedQuery(name = "Hongo.findByNombreComun", query = "SELECT h FROM Hongo h WHERE h.nombreComun = :nombreComun"),
    @NamedQuery(name = "Hongo.findByRegion", query = "SELECT h FROM Hongo h WHERE h.region = :region"),
    @NamedQuery(name = "Hongo.findByInformacion", query = "SELECT h FROM Hongo h WHERE h.informacion = :informacion")})
public class Hongo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_hongo")
    private Integer idHongo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "familia")
    private String familia;
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
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "region")
    private String region;
    @Size(max = 2147483647)
    @Column(name = "informacion")
    private String informacion;
    @ManyToMany(mappedBy = "hongoCollection")
    private Collection<Enfermedad> enfermedadCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hongo")
    private Collection<Analisis> analisisCollection;

    public Hongo() {
    }

    public Hongo(Integer idHongo) {
        this.idHongo = idHongo;
    }

    public Hongo(Integer idHongo, String familia, String nombreCientifico, String nombreComun, String region) {
        this.idHongo = idHongo;
        this.familia = familia;
        this.nombreCientifico = nombreCientifico;
        this.nombreComun = nombreComun;
        this.region = region;
    }

    public Integer getIdHongo() {
        return idHongo;
    }

    public void setIdHongo(Integer idHongo) {
        this.idHongo = idHongo;
    }

    public String getFamilia() {
        return familia;
    }

    public void setFamilia(String familia) {
        this.familia = familia;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getInformacion() {
        return informacion;
    }

    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }

    @XmlTransient
    public Collection<Enfermedad> getEnfermedadCollection() {
        return enfermedadCollection;
    }

    public void setEnfermedadCollection(Collection<Enfermedad> enfermedadCollection) {
        this.enfermedadCollection = enfermedadCollection;
    }

    @XmlTransient
    public Collection<Analisis> getAnalisisCollection() {
        return analisisCollection;
    }

    public void setAnalisisCollection(Collection<Analisis> analisisCollection) {
        this.analisisCollection = analisisCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idHongo != null ? idHongo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Hongo)) {
            return false;
        }
        Hongo other = (Hongo) object;
        if ((this.idHongo == null && other.idHongo != null) || (this.idHongo != null && !this.idHongo.equals(other.idHongo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.Hongo[ idHongo=" + idHongo + " ]";
    }
    
}
