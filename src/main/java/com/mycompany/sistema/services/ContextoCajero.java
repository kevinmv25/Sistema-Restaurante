package com.mycompany.sistema.services;

/**
 * Mantiene datos temporales compartidos entre las pantallas del módulo de cajero.
 *
 * <p>Esta clase funciona como un contexto sencillo para conservar información
 * como el pedido, cuenta, mesa o pago seleccionado. Su objetivo es facilitar la
 * comunicación entre ventanas sin acoplar directamente los controladores entre
 * sí.</p>
 *
 * <p>No sustituye a la base de datos. Solo conserva referencias temporales
 * durante la navegación del usuario.</p>
 *
 * @author Gutierrez Colorado Oliver
 * @see SceneService
 * @see ReceptorDatos
 */
public class ContextoCajero {
    
    private static Integer idPedidoActual;
    private static Integer idCuentaActual;
    private static Integer idMesaActual;
    private static Integer idPagoActual;

    private static String estadoCuentaActual;
    private static String nombreClienteActual;

    public static Integer getIdPedidoActual() {
        return idPedidoActual;
    }

    public static void setIdPedidoActual(Integer idPedidoActual) {
        ContextoCajero.idPedidoActual = idPedidoActual;
    }

    public static Integer getIdCuentaActual() {
        return idCuentaActual;
    }

    public static void setIdCuentaActual(Integer idCuentaActual) {
        ContextoCajero.idCuentaActual = idCuentaActual;
    }

    public static Integer getIdMesaActual() {
        return idMesaActual;
    }

    public static void setIdMesaActual(Integer idMesaActual) {
        ContextoCajero.idMesaActual = idMesaActual;
    }

    public static Integer getIdPagoActual() {
        return idPagoActual;
    }

    public static void setIdPagoActual(Integer idPagoActual) {
        ContextoCajero.idPagoActual = idPagoActual;
    }

    public static String getEstadoCuentaActual() {
        return estadoCuentaActual;
    }

    public static void setEstadoCuentaActual(String estadoCuentaActual) {
        ContextoCajero.estadoCuentaActual = estadoCuentaActual;
    }

    public static String getNombreClienteActual() {
        return nombreClienteActual;
    }

    public static void setNombreClienteActual(String nombreClienteActual) {
        ContextoCajero.nombreClienteActual = nombreClienteActual;
    }

    /**
    * Limpia todos los datos temporales almacenados en el contexto.
    *
    * <p>Este método puede usarse al cerrar sesión o cuando se quiera evitar que una
    * operación anterior afecte el flujo de otra pantalla.</p>
    */
    public static void limpiar() {
        idPedidoActual = null;
        idCuentaActual = null;
        idMesaActual = null;
        idPagoActual = null;
        estadoCuentaActual = null;
        nombreClienteActual = null;
    }
}
