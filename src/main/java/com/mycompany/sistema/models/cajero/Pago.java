package com.mycompany.sistema.models.cajero;

public class Pago {

    private int idPago;
    private int idCuenta;
    private int idTurno;
    private String metodoPago;
    private double monto;
    private String estado;
    private String fechaPago;

    public Pago() {
    }

    public Pago(int idPago, int idCuenta, int idTurno, String metodoPago,
                double monto, String estado, String fechaPago) {
        this.idPago = idPago;
        this.idCuenta = idCuenta;
        this.idTurno = idTurno;
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.estado = estado;
        this.fechaPago = fechaPago;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }
}