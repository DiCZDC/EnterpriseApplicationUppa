/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author daniel
 */
@Entity
@Table(name = "metodo_pago")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MetodoPago.findAll", query = "SELECT m FROM MetodoPago m"),
    @NamedQuery(name = "MetodoPago.findByIdMetodoPago", query = "SELECT m FROM MetodoPago m WHERE m.metodoPagoPK.idMetodoPago = :idMetodoPago"),
    @NamedQuery(name = "MetodoPago.findByIdUsuario", query = "SELECT m FROM MetodoPago m WHERE m.metodoPagoPK.idUsuario = :idUsuario"),
    @NamedQuery(name = "MetodoPago.findByTipo", query = "SELECT m FROM MetodoPago m WHERE m.tipo = :tipo"),
    @NamedQuery(name = "MetodoPago.findByTokenPasarela", query = "SELECT m FROM MetodoPago m WHERE m.tokenPasarela = :tokenPasarela"),
    @NamedQuery(name = "MetodoPago.findByMarca", query = "SELECT m FROM MetodoPago m WHERE m.marca = :marca"),
    @NamedQuery(name = "MetodoPago.findByUltimosCuatro", query = "SELECT m FROM MetodoPago m WHERE m.ultimosCuatro = :ultimosCuatro"),
    @NamedQuery(name = "MetodoPago.findByFechaExpiracion", query = "SELECT m FROM MetodoPago m WHERE m.fechaExpiracion = :fechaExpiracion"),
    @NamedQuery(name = "MetodoPago.findByEsPredeterminado", query = "SELECT m FROM MetodoPago m WHERE m.esPredeterminado = :esPredeterminado"),
    @NamedQuery(name = "MetodoPago.findByActivo", query = "SELECT m FROM MetodoPago m WHERE m.activo = :activo")})
public class MetodoPago implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected MetodoPagoPK metodoPagoPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "tipo")
    private String tipo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "token_pasarela")
    private String tokenPasarela;
    @Size(max = 30)
    @Column(name = "marca")
    private String marca;
    @Size(max = 4)
    @Column(name = "ultimos_cuatro")
    private String ultimosCuatro;
    @Size(max = 7)
    @Column(name = "fecha_expiracion")
    private String fechaExpiracion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "es_predeterminado")
    private boolean esPredeterminado;
    @Basic(optional = false)
    @NotNull
    @Column(name = "activo")
    private boolean activo;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Usuario usuario;

    public MetodoPago() {
    }

    public MetodoPago(MetodoPagoPK metodoPagoPK) {
        this.metodoPagoPK = metodoPagoPK;
    }

    public MetodoPago(MetodoPagoPK metodoPagoPK, String tipo, String tokenPasarela, boolean esPredeterminado, boolean activo) {
        this.metodoPagoPK = metodoPagoPK;
        this.tipo = tipo;
        this.tokenPasarela = tokenPasarela;
        this.esPredeterminado = esPredeterminado;
        this.activo = activo;
    }

    public MetodoPago(int idMetodoPago, int idUsuario) {
        this.metodoPagoPK = new MetodoPagoPK(idMetodoPago, idUsuario);
    }

    public MetodoPagoPK getMetodoPagoPK() {
        return metodoPagoPK;
    }

    public void setMetodoPagoPK(MetodoPagoPK metodoPagoPK) {
        this.metodoPagoPK = metodoPagoPK;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTokenPasarela() {
        return tokenPasarela;
    }

    public void setTokenPasarela(String tokenPasarela) {
        this.tokenPasarela = tokenPasarela;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getUltimosCuatro() {
        return ultimosCuatro;
    }

    public void setUltimosCuatro(String ultimosCuatro) {
        this.ultimosCuatro = ultimosCuatro;
    }

    public String getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(String fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public boolean getEsPredeterminado() {
        return esPredeterminado;
    }

    public void setEsPredeterminado(boolean esPredeterminado) {
        this.esPredeterminado = esPredeterminado;
    }

    public boolean getActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
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
        hash += (metodoPagoPK != null ? metodoPagoPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MetodoPago)) {
            return false;
        }
        MetodoPago other = (MetodoPago) object;
        if ((this.metodoPagoPK == null && other.metodoPagoPK != null) || (this.metodoPagoPK != null && !this.metodoPagoPK.equals(other.metodoPagoPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.MetodoPago[ metodoPagoPK=" + metodoPagoPK + " ]";
    }
    
}
