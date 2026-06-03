package com.mycompany.sistema.models.cajero;

/**
 * Representa un producto incluido dentro de un pedido.
 *
 * <p>Esta clase permite mostrar el desglose de productos que forman parte de una
 * cuenta. Cada detalle guarda el producto, la cantidad solicitada, el precio
 * unitario y el subtotal calculado para ese renglón.</p>
 *
 * <p>Su información se utiliza principalmente en facturación y descuentos, ya
 * que el cajero necesita conocer qué productos forman parte de la cuenta antes
 * de generar documentos o aplicar rebajas.</p>
 *
 * @author Gutierrez Colorado Oliver
 * @see Pedido
 * @see Cuenta
 * @see Descuento
 */
public class DetallePedido {

    private int idDetalle;
    private int idPedido;
    private int idProducto;
    private String nombreProducto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    /**
    * Crea un detalle de pedido vacío.
    *
    * <p>Se usa cuando el objeto será llenado posteriormente por la interfaz o por
    * consultas a la base de datos.</p>
    */
    public DetallePedido() {
    }

    /**
    * Crea un detalle de pedido con la información completa del producto.
    *
    * @param idDetalle identificador único del detalle.
    * @param idPedido identificador del pedido al que pertenece.
    * @param idProducto identificador del producto relacionado.
    * @param nombreProducto nombre visible del producto.
    * @param cantidad número de unidades solicitadas.
    * @param precioUnitario precio individual del producto.
    * @param subtotal resultado de multiplicar cantidad por precio unitario.
    */
    public DetallePedido(int idDetalle, int idPedido, int idProducto, String nombreProducto,
                         int cantidad, double precioUnitario, double subtotal) {
        this.idDetalle = idDetalle;
        this.idPedido = idPedido;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}