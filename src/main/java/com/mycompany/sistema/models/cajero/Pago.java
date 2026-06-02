package com.mycompany.sistema.models.cajero;

/**
 * Representa un pago registrado para una cuenta.
 *
 * <p>Esta clase almacena la información del cobro realizado por el cajero,
 * incluyendo el método de pago, el monto, el turno de caja y el estado de la
 * transacción.</p>
 *
 * <p>Un pago válido permite cerrar el ciclo de una cuenta, liberar la mesa
 * relacionada y registrar el ingreso dentro de los movimientos de caja.</p>
 *
 * @author Gutierrez Colorado Oliver
 * @see Cuenta
 * @see MovimientoCaja
 * @see ComprobanteCancelacion
 */
public class Pago {

    private int idPago;
    private int idCuenta;
    private int idTurno;
    private String metodoPago;
    private double monto;
    private String estado;
    private String fechaPago;

    /**
    * Crea un pago vacío.
    *
    * <p>Se utiliza cuando el objeto será construido gradualmente desde la interfaz
    * o al recibir datos desde la base de datos.</p>
    */
    public Pago() {
    }

    /**
    * Crea un pago con la información completa de la transacción.
    *
    * @param idPago identificador único del pago.
    * @param idCuenta cuenta que fue pagada.
    * @param idTurno turno de caja donde se registró el cobro.
    * @param metodoPago método usado para pagar, como efectivo o tarjeta.
    * @param monto cantidad cobrada al cliente.
    * @param estado estado del pago, por ejemplo <code>Pagado</code> o <code>Cancelado</code>.
    * @param fechaPago fecha y hora en la que se registró el pago.
    */
    public Pago(int idPago, int idCuenta, int idTurno, String metodoPago,
                double monto, String estado, String fechaPago) {
        this.idPago = idPago;
        this.idCuenta = idCuenta;
        this.idTurno = idTurno;
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.estado = estado;
        this.fechaPago = fechaPago;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }
}