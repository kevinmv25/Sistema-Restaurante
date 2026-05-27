package com.mycompany.sistema.models.cajero;

public class MovimientoCaja {

    private int idMovimiento;
    private int idTurno;
    private int idPago;
    private String tipo;
    private double monto;
    private String descripcion;
    private String fecha;

    public MovimientoCaja() {
    }

    public MovimientoCaja(int idMovimiento, int idTurno, int idPago,
                          String tipo, double monto, String descripcion, String fecha) {
        this.idMovimiento = idMovimiento;
        this.idTurno = idTurno;
        this.idPago = idPago;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}