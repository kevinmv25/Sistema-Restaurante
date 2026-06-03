package com.mycompany.sistema.models.cajero;

/**
 * Representa el turno o corte de caja del cajero.
 *
 * <p>Esta clase concentra los datos necesarios para abrir y cerrar caja. Guarda
 * el fondo inicial, los totales esperados, el conteo físico y cualquier
 * diferencia justificada durante el cierre.</p>
 *
 * <p>El total esperado se calcula con la lógica:
 * <code>total esperado = monto inicial + pagos - cancelaciones</code>.</p>
 *
 * @author Gutierrez Colorado Oliver
 * @see MovimientoCaja
 * @see Pago
 */
public class CorteCaja {

    private int idTurno;
    private double montoInicial;
    private String fechaApertura;
    private String fechaCierre;
    private double totalEsperado;
    private double totalFisico;
    private double diferencia;
    private String justificacion;
    private String estado;

    /**
    * Crea un corte de caja vacío.
    *
    * <p>Es útil para formularios o procesos donde los datos del turno se asignan
    * después de consultar la base de datos.</p>
    */
    public CorteCaja() {
    }

    /**
    * Crea un corte de caja con la información completa del turno.
    *
    * @param idTurno identificador único del turno de caja.
    * @param montoInicial fondo inicial registrado al abrir caja.
    * @param fechaApertura fecha y hora de apertura.
    * @param fechaCierre fecha y hora de cierre, si el turno ya fue cerrado.
    * @param totalEsperado monto que debería existir al final del turno.
    * @param totalFisico monto contado físicamente por el cajero.
    * @param diferencia diferencia entre total físico y total esperado.
    * @param justificacion explicación registrada si existe sobrante o faltante.
    * @param estado estado del turno, por ejemplo <code>Abierta</code> o <code>Cerrada</code>.
    */
    public CorteCaja(int idTurno, double montoInicial, String fechaApertura,
                     String fechaCierre, double totalEsperado, double totalFisico,
                     double diferencia, String justificacion, String estado) {
        this.idTurno = idTurno;
        this.montoInicial = montoInicial;
        this.fechaApertura = fechaApertura;
        this.fechaCierre = fechaCierre;
        this.totalEsperado = totalEsperado;
        this.totalFisico = totalFisico;
        this.diferencia = diferencia;
        this.justificacion = justificacion;
        this.estado = estado;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    public double getMontoInicial() {
        return montoInicial;
    }

    public void setMontoInicial(double montoInicial) {
        this.montoInicial = montoInicial;
    }

    public String getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(String fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public String getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(String fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public double getTotalEsperado() {
        return totalEsperado;
    }

    public void setTotalEsperado(double totalEsperado) {
        this.totalEsperado = totalEsperado;
    }

    public double getTotalFisico() {
        return totalFisico;
    }

    public void setTotalFisico(double totalFisico) {
        this.totalFisico = totalFisico;
    }

    public double getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(double diferencia) {
        this.diferencia = diferencia;
    }

    public String getJustificacion() {
        return justificacion;
    }

    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}