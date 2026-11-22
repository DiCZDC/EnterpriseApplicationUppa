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
public class MetodoPagoPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id_metodo_pago")
    private int idMetodoPago;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_usuario")
    private int idUsuario;

    public MetodoPagoPK() {
    }

    public MetodoPagoPK(int idMetodoPago, int idUsuario) {
        this.idMetodoPago = idMetodoPago;
        this.idUsuario = idUsuario;
    }

    public int getIdMetodoPago() {
        return idMetodoPago;
    }

    public void setIdMetodoPago(int idMetodoPago) {
        this.idMetodoPago = idMetodoPago;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idMetodoPago;
        hash += (int) idUsuario;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MetodoPagoPK)) {
            return false;
        }
        MetodoPagoPK other = (MetodoPagoPK) object;
        if (this.idMetodoPago != other.idMetodoPago) {
            return false;
        }
        if (this.idUsuario != other.idUsuario) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.MetodoPagoPK[ idMetodoPago=" + idMetodoPago + ", idUsuario=" + idUsuario + " ]";
    }
    
}
