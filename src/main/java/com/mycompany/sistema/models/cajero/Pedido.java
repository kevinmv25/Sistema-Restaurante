package com.mycompany.sistema.models.cajero;
/**
 * Representa un pedido registrado dentro del restaurante.
 *
 * <p>Esta clase se utiliza como modelo base para los casos de uso del módulo de
 * cajero. Un pedido puede provenir de otra área del sistema, como meseros o
 * recepción, y posteriormente ser usado para generar una cuenta, aplicar
 * descuentos o registrar un pago.</p>
 *
 * <p>El estado más importante para el flujo de caja es <code>Por pagar</code>,
 * ya que indica que el pedido todavía no ha sido cobrado.</p>
 *
 * @author Gutierrez Colorado Oliver
 * @see DetallePedido
 * @see Cuenta
 */
public class Pedido {

    private int idPedido;
    private int idMesa;
    private String estado;
    private String fechaCreacion;

    /**
    * Crea una instancia vacía de pedido.
    *
    * <p>Este constructor es útil para JavaFX, pruebas o procesos donde el objeto se
    * llena por partes después de ser creado.</p>
    */
    public Pedido() {
    }

    /**
    * Crea un pedido con sus datos principales.
    *
    * @param idPedido identificador único del pedido.
    * @param idMesa identificador de la mesa relacionada con el pedido.
    * @param estado estado actual del pedido, por ejemplo <code>Por pagar</code>.
    * @param fechaCreacion fecha y hora en la que se registró el pedido.
    */
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