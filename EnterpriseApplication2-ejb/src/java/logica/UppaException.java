package logica;

import javax.ejb.ApplicationException;

/**
 * Error de negocio con mensaje apto para mostrarse al usuario
 * (credenciales inválidas, API no disponible, imagen no válida...).
 * rollback = false: p. ej. el contador de intentos fallidos de login debe persistir.
 *
 * @author crstvbl
 */
@ApplicationException(rollback = false)
public class UppaException extends Exception {

    private static final long serialVersionUID = 1L;

    public UppaException(String mensaje) {
        super(mensaje);
    }

    public UppaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
