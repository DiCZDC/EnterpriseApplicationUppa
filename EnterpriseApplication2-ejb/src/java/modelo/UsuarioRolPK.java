/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author crstvbl
 */
@Embeddable
public class UsuarioRolPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id_usario")
    private int idUsario;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_rol")
    private int idRol;

    public UsuarioRolPK() {
    }

    public UsuarioRolPK(int idUsario, int idRol) {
        this.idUsario = idUsario;
        this.idRol = idRol;
    }

    public int getIdUsario() {
        return idUsario;
    }

    public void setIdUsario(int idUsario) {
        this.idUsario = idUsario;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idUsario;
        hash += (int) idRol;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UsuarioRolPK)) {
            return false;
        }
        UsuarioRolPK other = (UsuarioRolPK) object;
        if (this.idUsario != other.idUsario) {
            return false;
        }
        if (this.idRol != other.idRol) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.UsuarioRolPK[ idUsario=" + idUsario + ", idRol=" + idRol + " ]";
    }
    
}
