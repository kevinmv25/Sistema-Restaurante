package com.mycompany.sistema.models.cajero;

/**
 * Representa la cuenta generada a partir de un pedido.
 *
 * <p>La cuenta es el punto central entre facturación, descuentos y pagos. Se crea
 * cuando el cajero genera el documento de consumo y permanece en estado
 * <code>Por pagar</code> hasta que se registra el cobro correspondiente.</p>
 *
 * <p>El total de la cuenta se compone de subtotal, impuestos y descuentos:
 * <code>total = subtotal + impuestos - descuento</code>.</p>
 *
 * @author Gutierrez Colorado Oliver
 * @see Pedido
 * @see Pago
 * @see Descuento
 */
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

    /**
    * Crea una cuenta vacía.
    *
    * <p>Este constructor facilita la creación del objeto cuando los datos se
    * asignan después, por ejemplo desde una consulta o una pantalla JavaFX.</p>
    */
    public Cuenta() {
    }

    /**
    * Crea una cuenta con todos sus importes y datos de documento.
    *
    * @param idCuenta identificador único de la cuenta.
    * @param idPedido pedido que originó la cuenta.
    * @param subtotal suma inicial de los productos antes de impuestos o descuentos.
    * @param impuestos monto calculado por IVA u otros cargos.
    * @param descuento total descontado sobre la cuenta.
    * @param total monto final que debe pagar el cliente.
    * @param estado estado de la cuenta, por ejemplo <code>Por pagar</code> o <code>Pagada</code>.
    * @param tipoDocumento tipo de documento generado, como ticket o factura.
    * @param formato formato de salida del documento.
    * @param fechaGeneracion fecha y hora en la que se generó la cuenta.
    */
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