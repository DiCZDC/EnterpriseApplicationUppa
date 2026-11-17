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
 * @author daniel
 */
@Embeddable
public class PagoPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id_pago")
    private int idPago;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_suscripcion")
    private int idSuscripcion;

    public PagoPK() {
    }

    public PagoPK(int idPago, int idSuscripcion) {
        this.idPago = idPago;
        this.idSuscripcion = idSuscripcion;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public int getIdSuscripcion() {
        return idSuscripcion;
    }

    public void setIdSuscripcion(int idSuscripcion) {
        this.idSuscripcion = idSuscripcion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idPago;
        hash += (int) idSuscripcion;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PagoPK)) {
            return false;
        }
        PagoPK other = (PagoPK) object;
        if (this.idPago != other.idPago) {
            return false;
        }
        if (this.idSuscripcion != other.idSuscripcion) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.PagoPK[ idPago=" + idPago + ", idSuscripcion=" + idSuscripcion + " ]";
    }
    
}
