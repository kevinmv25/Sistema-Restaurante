package com.mycompany.sistema.models.cajero;

public class Descuento {

    private int idDescuento;
    private int idCuenta;
    private Integer idProducto;
    private String tipo;
    private double valor;
    private String motivo;
    private double montoDescontado;
    private boolean autorizadoPorGerente;
    private String fecha;

    public Descuento() {
    }

    public Descuento(int idDescuento, int idCuenta, Integer idProducto,
                     String tipo, double valor, String motivo,
                     double montoDescontado, boolean autorizadoPorGerente,
                     String fecha) {
        this.idDescuento = idDescuento;
        this.idCuenta = idCuenta;
        this.idProducto = idProducto;
        this.tipo = tipo;
        this.valor = valor;
        this.motivo = motivo;
        this.montoDescontado = montoDescontado;
        this.autorizadoPorGerente = autorizadoPorGerente;
        this.fecha = fecha;
    }

    public int getIdDescuento() {
        return idDescuento;
    }

    public void setIdDescuento(int idDescuento) {
        this.idDescuento = idDescuento;
    }

    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public double getMontoDescontado() {
        return montoDescontado;
    }

    public void setMontoDescontado(double montoDescontado) {
        this.montoDescontado = montoDescontado;
    }

    public boolean isAutorizadoPorGerente() {
        return autorizadoPorGerente;
    }

    public void setAutorizadoPorGerente(boolean autorizadoPorGerente) {
        this.autorizadoPorGerente = autorizadoPorGerente;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}