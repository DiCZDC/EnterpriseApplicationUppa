/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
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
@Table(name = "credencial")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Credencial.findAll", query = "SELECT c FROM Credencial c"),
    @NamedQuery(name = "Credencial.findByIdUsuario", query = "SELECT c FROM Credencial c WHERE c.idUsuario = :idUsuario"),
    @NamedQuery(name = "Credencial.findByPasswordHash", query = "SELECT c FROM Credencial c WHERE c.passwordHash = :passwordHash"),
    @NamedQuery(name = "Credencial.findBySalt", query = "SELECT c FROM Credencial c WHERE c.salt = :salt"),
    @NamedQuery(name = "Credencial.findByAlgoritmoHash", query = "SELECT c FROM Credencial c WHERE c.algoritmoHash = :algoritmoHash"),
    @NamedQuery(name = "Credencial.findByMfaHabilitado", query = "SELECT c FROM Credencial c WHERE c.mfaHabilitado = :mfaHabilitado"),
    @NamedQuery(name = "Credencial.findByMfaSecreto", query = "SELECT c FROM Credencial c WHERE c.mfaSecreto = :mfaSecreto"),
    @NamedQuery(name = "Credencial.findByIntentosFallidos", query = "SELECT c FROM Credencial c WHERE c.intentosFallidos = :intentosFallidos"),
    @NamedQuery(name = "Credencial.findByBloqueadoHasta", query = "SELECT c FROM Credencial c WHERE c.bloqueadoHasta = :bloqueadoHasta"),
    @NamedQuery(name = "Credencial.findByUltimoAcceso", query = "SELECT c FROM Credencial c WHERE c.ultimoAcceso = :ultimoAcceso"),
    @NamedQuery(name = "Credencial.findByFechaCambioPassword", query = "SELECT c FROM Credencial c WHERE c.fechaCambioPassword = :fechaCambioPassword"),
    @NamedQuery(name = "Credencial.findByTokenRecuperacion", query = "SELECT c FROM Credencial c WHERE c.tokenRecuperacion = :tokenRecuperacion"),
    @NamedQuery(name = "Credencial.findByExpiracionToken", query = "SELECT c FROM Credencial c WHERE c.expiracionToken = :expiracionToken")})
public class Credencial implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id_usuario")
    private Integer idUsuario;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "password_hash")
    private String passwordHash;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "salt")
    private String salt;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "algoritmo_hash")
    private String algoritmoHash;
    @Basic(optional = false)
    @NotNull
    @Column(name = "mfa_habilitado")
    private boolean mfaHabilitado;
    @Size(max = 255)
    @Column(name = "mfa_secreto")
    private String mfaSecreto;
    @Basic(optional = false)
    @NotNull
    @Column(name = "intentos_fallidos")
    private int intentosFallidos;
    @Column(name = "bloqueado_hasta")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bloqueadoHasta;
    @Column(name = "ultimo_acceso")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ultimoAcceso;
    @Column(name = "fecha_cambio_password")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCambioPassword;
    @Size(max = 255)
    @Column(name = "token_recuperacion")
    private String tokenRecuperacion;
    @Column(name = "expiracion_token")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiracionToken;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Usuario usuario;

    public Credencial() {
    }

    public Credencial(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Credencial(Integer idUsuario, String passwordHash, String salt, String algoritmoHash, boolean mfaHabilitado, int intentosFallidos) {
        this.idUsuario = idUsuario;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.algoritmoHash = algoritmoHash;
        this.mfaHabilitado = mfaHabilitado;
        this.intentosFallidos = intentosFallidos;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getAlgoritmoHash() {
        return algoritmoHash;
    }

    public void setAlgoritmoHash(String algoritmoHash) {
        this.algoritmoHash = algoritmoHash;
    }

    public boolean getMfaHabilitado() {
        return mfaHabilitado;
    }

    public void setMfaHabilitado(boolean mfaHabilitado) {
        this.mfaHabilitado = mfaHabilitado;
    }

    public String getMfaSecreto() {
        return mfaSecreto;
    }

    public void setMfaSecreto(String mfaSecreto) {
        this.mfaSecreto = mfaSecreto;
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    public Date getBloqueadoHasta() {
        return bloqueadoHasta;
    }

    public void setBloqueadoHasta(Date bloqueadoHasta) {
        this.bloqueadoHasta = bloqueadoHasta;
    }

    public Date getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(Date ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    public Date getFechaCambioPassword() {
        return fechaCambioPassword;
    }

    public void setFechaCambioPassword(Date fechaCambioPassword) {
        this.fechaCambioPassword = fechaCambioPassword;
    }

    public String getTokenRecuperacion() {
        return tokenRecuperacion;
    }

    public void setTokenRecuperacion(String tokenRecuperacion) {
        this.tokenRecuperacion = tokenRecuperacion;
    }

    public Date getExpiracionToken() {
        return expiracionToken;
    }

    public void setExpiracionToken(Date expiracionToken) {
        this.expiracionToken = expiracionToken;
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
        hash += (idUsuario != null ? idUsuario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Credencial)) {
            return false;
        }
        Credencial other = (Credencial) object;
        if ((this.idUsuario == null && other.idUsuario != null) || (this.idUsuario != null && !this.idUsuario.equals(other.idUsuario))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "modelo.Credencial[ idUsuario=" + idUsuario + " ]";
    }
    
}
