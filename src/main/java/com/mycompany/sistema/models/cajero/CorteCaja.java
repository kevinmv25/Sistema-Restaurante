package com.mycompany.sistema.models.cajero;

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

    public CorteCaja() {
    }

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