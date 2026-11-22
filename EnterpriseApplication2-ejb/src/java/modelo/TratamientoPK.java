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
public class TratamientoPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id_tratamiento")
    private int idTratamiento;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_enfermedad")
    private int idEnfermedad;

    public TratamientoPK() {
    }

    public TratamientoPK(int idTratamiento, int idEnfermedad) {
        this.idTratamiento = idTratamiento;
        this.idEnfermedad = idEnfermedad;
    }

    public int getIdTratamiento() {
        return idTratamiento;
    }

    public void setIdTratamiento(int idTratamiento) {
        this.idTratamiento = idTratamiento;
    }

    public int getIdEnfermedad() {
        return idEnfermedad;
    }

    public void setIdEnfermedad(int idEnfermedad) {
        this.idEnfermedad = idEnfermedad;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idTratamiento;
        hash += (int) idEnfermedad;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TratamientoPK)) {
            return false;
        }
        TratamientoPK other = (TratamientoPK) object;
        if (this.idTratamiento != other.idTratamiento) {
            return false;
        }
        if (this.idEnfermedad != other.idEnfermedad) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.TratamientoPK[ idTratamiento=" + idTratamiento + ", idEnfermedad=" + idEnfermedad + " ]";
    }
    
}
