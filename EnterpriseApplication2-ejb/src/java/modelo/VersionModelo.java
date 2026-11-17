/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "version_modelo")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "VersionModelo.findAll", query = "SELECT v FROM VersionModelo v"),
    @NamedQuery(name = "VersionModelo.findByIdVersion", query = "SELECT v FROM VersionModelo v WHERE v.idVersion = :idVersion"),
    @NamedQuery(name = "VersionModelo.findByNombre", query = "SELECT v FROM VersionModelo v WHERE v.nombre = :nombre"),
    @NamedQuery(name = "VersionModelo.findByVersion", query = "SELECT v FROM VersionModelo v WHERE v.version = :version"),
    @NamedQuery(name = "VersionModelo.findByEpocasEntrenamiento", query = "SELECT v FROM VersionModelo v WHERE v.epocasEntrenamiento = :epocasEntrenamiento"),
    @NamedQuery(name = "VersionModelo.findByTamanoDataset", query = "SELECT v FROM VersionModelo v WHERE v.tamanoDataset = :tamanoDataset"),
    @NamedQuery(name = "VersionModelo.findByPrecisionPromedio", query = "SELECT v FROM VersionModelo v WHERE v.precisionPromedio = :precisionPromedio")})
public class VersionModelo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_version")
    private Integer idVersion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "version")
    private String version;
    @Basic(optional = false)
    @NotNull
    @Column(name = "epocas_entrenamiento")
    private int epocasEntrenamiento;
    @Basic(optional = false)
    @NotNull
    @Column(name = "tamano_dataset")
    private int tamanoDataset;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "precision_promedio")
    private BigDecimal precisionPromedio;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "versionModelo")
    private Collection<Analisis> analisisCollection;

    public VersionModelo() {
    }

    public VersionModelo(Integer idVersion) {
        this.idVersion = idVersion;
    }

    public VersionModelo(Integer idVersion, String nombre, String version, int epocasEntrenamiento, int tamanoDataset, BigDecimal precisionPromedio) {
        this.idVersion = idVersion;
        this.nombre = nombre;
        this.version = version;
        this.epocasEntrenamiento = epocasEntrenamiento;
        this.tamanoDataset = tamanoDataset;
        this.precisionPromedio = precisionPromedio;
    }

    public Integer getIdVersion() {
        return idVersion;
    }

    public void setIdVersion(Integer idVersion) {
        this.idVersion = idVersion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getEpocasEntrenamiento() {
        return epocasEntrenamiento;
    }

    public void setEpocasEntrenamiento(int epocasEntrenamiento) {
        this.epocasEntrenamiento = epocasEntrenamiento;
    }

    public int getTamanoDataset() {
        return tamanoDataset;
    }

    public void setTamanoDataset(int tamanoDataset) {
        this.tamanoDataset = tamanoDataset;
    }

    public BigDecimal getPrecisionPromedio() {
        return precisionPromedio;
    }

    public void setPrecisionPromedio(BigDecimal precisionPromedio) {
        this.precisionPromedio = precisionPromedio;
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
        hash += (idVersion != null ? idVersion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VersionModelo)) {
            return false;
        }
        VersionModelo other = (VersionModelo) object;
        if ((this.idVersion == null && other.idVersion != null) || (this.idVersion != null && !this.idVersion.equals(other.idVersion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.VersionModelo[ idVersion=" + idVersion + " ]";
    }
    
}
