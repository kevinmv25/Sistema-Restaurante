package com.mycompany.sistema.models.cajero;

/**
 * Representa un descuento aplicado a una cuenta o producto.
 *
 * <p>Esta clase almacena la información de una rebaja registrada por el cajero.
 * Puede aplicarse sobre toda la cuenta o sobre un producto específico, según lo
 * indicado en el flujo de descuentos.</p>
 *
 * <p>Cuando el descuento supera el límite permitido, se marca si fue autorizado
 * por gerente para conservar evidencia de la operación.</p>
 *
 * @author Gutierrez Colorado Oliver
 * @see Cuenta
 * @see DetallePedido
 */
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

    /**
    * Crea un descuento vacío.
    *
    * <p>Se utiliza cuando los valores serán asignados después de validar la
    * operación en la interfaz.</p>
    */
    public Descuento() {
    }

    /**
    * Crea un descuento con toda la información de la operación.
    *
    * @param idDescuento identificador único del descuento.
    * @param idCuenta cuenta a la que se aplicó la rebaja.
    * @param idProducto producto afectado, o <code>null</code> si aplica a toda la cuenta.
    * @param tipo tipo de descuento, por ejemplo porcentaje o monto fijo.
    * @param valor valor capturado por el cajero.
    * @param motivo justificación de la rebaja.
    * @param montoDescontado cantidad real descontada sobre la cuenta.
    * @param autorizadoPorGerente indica si se requirió autorización adicional.
    * @param fecha fecha y hora en que se aplicó el descuento.
    */
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