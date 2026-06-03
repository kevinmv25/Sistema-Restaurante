package com.mycompany.sistema.models.cajero;

/**
 * Representa un movimiento registrado dentro de caja.
 *
 * <p>Un movimiento de caja puede corresponder a un pago, una entrada, una
 * cancelación o un reembolso. Esta clase permite mantener el historial de lo
 * que afecta el total esperado del turno.</p>
 *
 * <p>Los movimientos se usan principalmente para calcular el corte de caja y
 * para mostrar el historial de ventas del cajero.</p>
 *
 * @author Gutierrez Colorado Oliver
 * @see CorteCaja
 * @see Pago
 * @see HistorialVenta
 */
public class MovimientoCaja {

    private int idMovimiento;
    private int idTurno;
    private int idPago;
    private String tipo;
    private double monto;
    private String descripcion;
    private String fecha;

    /**
    * Crea un movimiento de caja vacío.
    *
    * <p>Se usa cuando el movimiento se completará posteriormente desde una consulta
    * o desde una operación del sistema.</p>
    */
    public MovimientoCaja() {
    }

    /**
    * Crea un movimiento de caja con sus datos principales.
    *
    * @param idMovimiento identificador único del movimiento.
    * @param idTurno turno de caja al que pertenece.
    * @param idPago pago relacionado, si existe.
    * @param tipo tipo de movimiento, como <code>Pago</code> o <code>Cancelación</code>.
    * @param monto cantidad que afecta el total del turno.
    * @param descripcion explicación breve del movimiento.
    * @param fecha fecha y hora en que se registró el movimiento.
    */
    public MovimientoCaja(int idMovimiento, int idTurno, int idPago,
                          String tipo, double monto, String descripcion, String fecha) {
        this.idMovimiento = idMovimiento;
        this.idTurno = idTurno;
        this.idPago = idPago;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.fecha = fecha;
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
}