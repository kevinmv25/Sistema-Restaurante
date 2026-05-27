package com.mycompany.sistema.models.cajero;

public class ComprobanteCancelacion {

    private int idComprobante;
    private int idPago;
    private String motivo;
    private double monto;
    private String fecha;

    public ComprobanteCancelacion() {
    }

    public ComprobanteCancelacion(int idComprobante, int idPago,
                                  String motivo, double monto, String fecha) {
        this.idComprobante = idComprobante;
        this.idPago = idPago;
        this.motivo = motivo;
        this.monto = monto;
        this.fecha = fecha;
    }

    public int getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(int idComprobante) {
        this.idComprobante = idComprobante;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}