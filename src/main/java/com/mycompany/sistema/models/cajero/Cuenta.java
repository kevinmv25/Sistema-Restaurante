package com.mycompany.sistema.models.cajero;

public class Cuenta {

    private int idCuenta;
    private int idPedido;
    private double subtotal;
    private double impuestos;
    private double descuento;
    private double total;
    private String estado;
    private String tipoDocumento;
    private String formato;
    private String fechaGeneracion;

    public Cuenta() {
    }

    public Cuenta(int idCuenta, int idPedido, double subtotal, double impuestos,
                  double descuento, double total, String estado,
                  String tipoDocumento, String formato, String fechaGeneracion) {
        this.idCuenta = idCuenta;
        this.idPedido = idPedido;
        this.subtotal = subtotal;
        this.impuestos = impuestos;
        this.descuento = descuento;
        this.total = total;
        this.estado = estado;
        this.tipoDocumento = tipoDocumento;
        this.formato = formato;
        this.fechaGeneracion = fechaGeneracion;
    }

    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(double impuestos) {
        this.impuestos = impuestos;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(String fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }
}