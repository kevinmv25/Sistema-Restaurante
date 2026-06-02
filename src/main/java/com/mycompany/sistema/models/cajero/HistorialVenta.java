package com.mycompany.sistema.models.cajero;

/**
 * Representa una vista resumida del historial de ventas y movimientos.
 *
 * <p>Esta clase no modifica información de caja; su propósito es reunir datos
 * de movimientos, pagos y cuentas para mostrarlos de manera clara en la pantalla
 * de historial.</p>
 *
 * <p>Funciona como un modelo de consulta, ya que combina información proveniente
 * de varias tablas para facilitar la revisión del turno.</p>
 *
 * @author Gutierrez Colorado Oliver
 * @see MovimientoCaja
 * @see Pago
 * @see Cuenta
 */
public class HistorialVenta {

    private int idMovimiento;
    private int idTurno;
    private int idPago;
    private String tipo;
    private double monto;
    private String descripcion;
    private String fecha;

    private int idCuenta;
    private String metodoPago;
    private String estadoPago;

    /**
    * Crea un historial de venta vacío.
    *
    * <p>Se utiliza cuando el objeto será llenado posteriormente desde la base de
    * datos o desde una consulta filtrada.</p>
    */
    public HistorialVenta() {
    }

    /**
    * Crea un registro de historial con información de movimiento, pago y cuenta.
    *
    * @param idMovimiento identificador del movimiento de caja.
    * @param idTurno turno donde ocurrió el movimiento.
    * @param idPago pago relacionado con el movimiento.
    * @param tipo tipo de movimiento registrado.
    * @param monto importe del movimiento.
    * @param descripcion descripción breve de la operación.
    * @param fecha fecha y hora del movimiento.
    * @param idCuenta cuenta relacionada con el pago.
    * @param metodoPago método usado en la transacción.
    * @param estadoPago estado actual del pago.
    */
    public HistorialVenta(int idMovimiento, int idTurno, int idPago, String tipo,
                          double monto, String descripcion, String fecha,
                          int idCuenta, String metodoPago, String estadoPago) {
        this.idMovimiento = idMovimiento;
        this.idTurno = idTurno;
        this.idPago = idPago;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.idCuenta = idCuenta;
        this.metodoPago = metodoPago;
        this.estadoPago = estadoPago;
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

    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }
}
