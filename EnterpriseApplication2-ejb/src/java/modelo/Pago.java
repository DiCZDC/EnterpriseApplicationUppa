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
@Table(name = "pago")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pago.findAll", query = "SELECT p FROM Pago p"),
    @NamedQuery(name = "Pago.findByIdPago", query = "SELECT p FROM Pago p WHERE p.pagoPK.idPago = :idPago"),
    @NamedQuery(name = "Pago.findByIdSuscripcion", query = "SELECT p FROM Pago p WHERE p.pagoPK.idSuscripcion = :idSuscripcion"),
    @NamedQuery(name = "Pago.findByIdMetodoPago", query = "SELECT p FROM Pago p WHERE p.idMetodoPago = :idMetodoPago"),
    @NamedQuery(name = "Pago.findByMonto", query = "SELECT p FROM Pago p WHERE p.monto = :monto"),
    @NamedQuery(name = "Pago.findByMoneda", query = "SELECT p FROM Pago p WHERE p.moneda = :moneda"),
    @NamedQuery(name = "Pago.findByFechaPago", query = "SELECT p FROM Pago p WHERE p.fechaPago = :fechaPago"),
    @NamedQuery(name = "Pago.findByEstado", query = "SELECT p FROM Pago p WHERE p.estado = :estado"),
    @NamedQuery(name = "Pago.findByReferenciaPasarela", query = "SELECT p FROM Pago p WHERE p.referenciaPasarela = :referenciaPasarela"),
    @NamedQuery(name = "Pago.findByNumeroFactura", query = "SELECT p FROM Pago p WHERE p.numeroFactura = :numeroFactura"),
    @NamedQuery(name = "Pago.findByDescripcion", query = "SELECT p FROM Pago p WHERE p.descripcion = :descripcion")})
public class Pago implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PagoPK pagoPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_metodo_pago")
    private int idMetodoPago;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "monto")
    private BigDecimal monto;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 3)
    @Column(name = "moneda")
    private String moneda;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_pago")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaPago;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "estado")
    private String estado;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "referencia_pasarela")
    private String referenciaPasarela;
    @Size(max = 50)
    @Column(name = "numero_factura")
    private String numeroFactura;
    @Size(max = 2147483647)
    @Column(name = "descripcion")
    private String descripcion;
    @JoinColumn(name = "id_suscripcion", referencedColumnName = "id_suscripcion", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Suscripcion suscripcion;

    public Pago() {
    }

    public Pago(PagoPK pagoPK) {
        this.pagoPK = pagoPK;
    }

    public Pago(PagoPK pagoPK, int idMetodoPago, BigDecimal monto, String moneda, Date fechaPago, String estado, String referenciaPasarela) {
        this.pagoPK = pagoPK;
        this.idMetodoPago = idMetodoPago;
        this.monto = monto;
        this.moneda = moneda;
        this.fechaPago = fechaPago;
        this.estado = estado;
        this.referenciaPasarela = referenciaPasarela;
    }

    public Pago(int idPago, int idSuscripcion) {
        this.pagoPK = new PagoPK(idPago, idSuscripcion);
    }

    public PagoPK getPagoPK() {
        return pagoPK;
    }

    public void setPagoPK(PagoPK pagoPK) {
        this.pagoPK = pagoPK;
    }

    public int getIdMetodoPago() {
        return idMetodoPago;
    }

    public void setIdMetodoPago(int idMetodoPago) {
        this.idMetodoPago = idMetodoPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getReferenciaPasarela() {
        return referenciaPasarela;
    }

    public void setReferenciaPasarela(String referenciaPasarela) {
        this.referenciaPasarela = referenciaPasarela;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Suscripcion getSuscripcion() {
        return suscripcion;
    }

    public void setSuscripcion(Suscripcion suscripcion) {
        this.suscripcion = suscripcion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pagoPK != null ? pagoPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pago)) {
            return false;
        }
        Pago other = (Pago) object;
        if ((this.pagoPK == null && other.pagoPK != null) || (this.pagoPK != null && !this.pagoPK.equals(other.pagoPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.Pago[ pagoPK=" + pagoPK + " ]";
    }
    
}
