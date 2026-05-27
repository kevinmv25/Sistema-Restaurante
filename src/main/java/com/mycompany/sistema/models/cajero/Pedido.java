package com.mycompany.sistema.models.cajero;

public class Pedido {

    private int idPedido;
    private int idMesa;
    private String estado;
    private String fechaCreacion;

    public Pedido() {
    }

    public Pedido(int idPedido, int idMesa, String estado, String fechaCreacion) {
        this.idPedido = idPedido;
        this.idMesa = idMesa;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}