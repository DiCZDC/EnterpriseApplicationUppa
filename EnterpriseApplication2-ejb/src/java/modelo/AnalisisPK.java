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
public class AnalisisPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id_analisis")
    private int idAnalisis;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_hongo")
    private int idHongo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_version")
    private int idVersion;

    public AnalisisPK() {
    }

    public AnalisisPK(int idAnalisis, int idHongo, int idVersion) {
        this.idAnalisis = idAnalisis;
        this.idHongo = idHongo;
        this.idVersion = idVersion;
    }

    public int getIdAnalisis() {
        return idAnalisis;
    }

    public void setIdAnalisis(int idAnalisis) {
        this.idAnalisis = idAnalisis;
    }

    public int getIdHongo() {
        return idHongo;
    }

    public void setIdHongo(int idHongo) {
        this.idHongo = idHongo;
    }

    public int getIdVersion() {
        return idVersion;
    }

    public void setIdVersion(int idVersion) {
        this.idVersion = idVersion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idAnalisis;
        hash += (int) idHongo;
        hash += (int) idVersion;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AnalisisPK)) {
            return false;
        }
        AnalisisPK other = (AnalisisPK) object;
        if (this.idAnalisis != other.idAnalisis) {
            return false;
        }
        if (this.idHongo != other.idHongo) {
            return false;
        }
        if (this.idVersion != other.idVersion) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.AnalisisPK[ idAnalisis=" + idAnalisis + ", idHongo=" + idHongo + ", idVersion=" + idVersion + " ]";
    }
    
}
