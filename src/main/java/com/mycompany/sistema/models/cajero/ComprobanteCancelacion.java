package com.mycompany.sistema.models.cajero;

/**
 * Representa el comprobante generado al cancelar un pago.
 *
 * <p>Esta clase conserva la evidencia de una cancelación o reembolso. Guarda el
 * pago afectado, el motivo proporcionado por el cajero, el monto y la fecha de
 * la operación.</p>
 *
 * <p>Su uso permite justificar movimientos negativos dentro del corte de caja y
 * mantener trazabilidad cuando una cuenta vuelve a estado <code>Por pagar</code>.</p>
 *
 * @author Gutierrez Colorado Oliver
 * @see Pago
 * @see MovimientoCaja
 */
public class ComprobanteCancelacion {

    private int idComprobante;
    private int idPago;
    private String motivo;
    private double monto;
    private String fecha;

    /**
    * Crea un comprobante de cancelación vacío.
    *
    * <p>Puede utilizarse cuando el comprobante se llena después de procesar la
    * cancelación del pago.</p>
    */
    public ComprobanteCancelacion() {
    }

    /**
    * Crea un comprobante de cancelación con sus datos principales.
    *
    * @param idComprobante identificador único del comprobante.
    * @param idPago pago cancelado.
    * @param motivo explicación de la cancelación.
    * @param monto cantidad cancelada o reembolsada.
    * @param fecha fecha y hora en la que se emitió el comprobante.
    */
    public ComprobanteCancelacion(int idComprobante, int idPago,
                                  String motivo, double monto, String fecha) {
        this.idComprobante = idComprobante;
        this.idPago = idPago;
        this.motivo = motivo;
        this.monto = monto;
        this.fecha = fecha;
    }

    public int getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(int idComprobante) {
        this.idComprobante = idComprobante;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}