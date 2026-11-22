/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author crstvbl
 */
@Entity
@Table(name = "usuario_rol")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UsuarioRol.findAll", query = "SELECT u FROM UsuarioRol u"),
    @NamedQuery(name = "UsuarioRol.findByIdUsario", query = "SELECT u FROM UsuarioRol u WHERE u.usuarioRolPK.idUsario = :idUsario"),
    @NamedQuery(name = "UsuarioRol.findByIdRol", query = "SELECT u FROM UsuarioRol u WHERE u.usuarioRolPK.idRol = :idRol"),
    @NamedQuery(name = "UsuarioRol.findByFechaAsignacion", query = "SELECT u FROM UsuarioRol u WHERE u.fechaAsignacion = :fechaAsignacion")})
public class UsuarioRol implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UsuarioRolPK usuarioRolPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_asignacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAsignacion;
    @JoinColumn(name = "id_rol", referencedColumnName = "id_rol", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Rol rol;
    @JoinColumn(name = "id_usario", referencedColumnName = "id_usuario", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Usuario usuario;

    public UsuarioRol() {
    }

    public UsuarioRol(UsuarioRolPK usuarioRolPK) {
        this.usuarioRolPK = usuarioRolPK;
    }

    public UsuarioRol(UsuarioRolPK usuarioRolPK, Date fechaAsignacion) {
        this.usuarioRolPK = usuarioRolPK;
        this.fechaAsignacion = fechaAsignacion;
    }

    public UsuarioRol(int idUsario, int idRol) {
        this.usuarioRolPK = new UsuarioRolPK(idUsario, idRol);
    }

    public UsuarioRolPK getUsuarioRolPK() {
        return usuarioRolPK;
    }

    public void setUsuarioRolPK(UsuarioRolPK usuarioRolPK) {
        this.usuarioRolPK = usuarioRolPK;
    }

    public Date getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(Date fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (usuarioRolPK != null ? usuarioRolPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UsuarioRol)) {
            return false;
        }
        UsuarioRol other = (UsuarioRol) object;
        if ((this.usuarioRolPK == null && other.usuarioRolPK != null) || (this.usuarioRolPK != null && !this.usuarioRolPK.equals(other.usuarioRolPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.UsuarioRol[ usuarioRolPK=" + usuarioRolPK + " ]";
    }
    
}
