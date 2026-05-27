/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistema.services;

/**
 *
 * @author olive
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

    public static void limpiar() {
        idPedidoActual = null;
        idCuentaActual = null;
        idMesaActual = null;
        idPagoActual = null;
        estadoCuentaActual = null;
        nombreClienteActual = null;
    }
}
