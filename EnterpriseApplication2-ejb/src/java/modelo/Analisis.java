/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author crstvbl
 */
@Entity
@Table(name = "analisis")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Analisis.findAll", query = "SELECT a FROM Analisis a"),
    @NamedQuery(name = "Analisis.findByIdAnalisis", query = "SELECT a FROM Analisis a WHERE a.analisisPK.idAnalisis = :idAnalisis"),
    @NamedQuery(name = "Analisis.findByIdHongo", query = "SELECT a FROM Analisis a WHERE a.analisisPK.idHongo = :idHongo"),
    @NamedQuery(name = "Analisis.findByIdVersion", query = "SELECT a FROM Analisis a WHERE a.analisisPK.idVersion = :idVersion"),
    @NamedQuery(name = "Analisis.findByFecha", query = "SELECT a FROM Analisis a WHERE a.fecha = :fecha"),
    @NamedQuery(name = "Analisis.findByImagenAnalizada", query = "SELECT a FROM Analisis a WHERE a.imagenAnalizada = :imagenAnalizada"),
    @NamedQuery(name = "Analisis.findByUbicacion", query = "SELECT a FROM Analisis a WHERE a.ubicacion = :ubicacion"),
    @NamedQuery(name = "Analisis.findByPrecisionIdentificacion", query = "SELECT a FROM Analisis a WHERE a.precisionIdentificacion = :precisionIdentificacion"),
    @NamedQuery(name = "Analisis.findByPrecisionDiagnostico", query = "SELECT a FROM Analisis a WHERE a.precisionDiagnostico = :precisionDiagnostico")})
public class Analisis implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected AnalisisPK analisisPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "imagen_analizada")
    private String imagenAnalizada;
    @Size(max = 255)
    @Column(name = "ubicacion")
    private String ubicacion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "precision_identificacion")
    private BigDecimal precisionIdentificacion;
    @Column(name = "precision_diagnostico")
    private BigDecimal precisionDiagnostico;
    @JoinColumn(name = "id_enfermedad", referencedColumnName = "id_enfermedad")
    @ManyToOne
    private Enfermedad idEnfermedad;
    @JoinColumn(name = "id_hongo", referencedColumnName = "id_hongo", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Hongo hongo;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    @ManyToOne(optional = false)
    private Usuario idUsuario;
    @JoinColumn(name = "id_version", referencedColumnName = "id_version", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private VersionModelo versionModelo;

    public Analisis() {
    }

    public Analisis(AnalisisPK analisisPK) {
        this.analisisPK = analisisPK;
    }

    public Analisis(AnalisisPK analisisPK, Date fecha, String imagenAnalizada) {
        this.analisisPK = analisisPK;
        this.fecha = fecha;
        this.imagenAnalizada = imagenAnalizada;
    }

    public Analisis(int idAnalisis, int idHongo, int idVersion) {
        this.analisisPK = new AnalisisPK(idAnalisis, idHongo, idVersion);
    }

    public AnalisisPK getAnalisisPK() {
        return analisisPK;
    }

    public void setAnalisisPK(AnalisisPK analisisPK) {
        this.analisisPK = analisisPK;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getImagenAnalizada() {
        return imagenAnalizada;
    }

    public void setImagenAnalizada(String imagenAnalizada) {
        this.imagenAnalizada = imagenAnalizada;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public BigDecimal getPrecisionIdentificacion() {
        return precisionIdentificacion;
    }

    public void setPrecisionIdentificacion(BigDecimal precisionIdentificacion) {
        this.precisionIdentificacion = precisionIdentificacion;
    }

    public BigDecimal getPrecisionDiagnostico() {
        return precisionDiagnostico;
    }

    public void setPrecisionDiagnostico(BigDecimal precisionDiagnostico) {
        this.precisionDiagnostico = precisionDiagnostico;
    }

    public Enfermedad getIdEnfermedad() {
        return idEnfermedad;
    }

    public void setIdEnfermedad(Enfermedad idEnfermedad) {
        this.idEnfermedad = idEnfermedad;
    }

    public Hongo getHongo() {
        return hongo;
    }

    public void setHongo(Hongo hongo) {
        this.hongo = hongo;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
    }

    public VersionModelo getVersionModelo() {
        return versionModelo;
    }

    public void setVersionModelo(VersionModelo versionModelo) {
        this.versionModelo = versionModelo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (analisisPK != null ? analisisPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Analisis)) {
            return false;
        }
        Analisis other = (Analisis) object;
        if ((this.analisisPK == null && other.analisisPK != null) || (this.analisisPK != null && !this.analisisPK.equals(other.analisisPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.Analisis[ analisisPK=" + analisisPK + " ]";
    }
    
}
