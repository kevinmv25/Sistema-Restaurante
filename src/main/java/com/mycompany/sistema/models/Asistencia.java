package com.mycompany.sistema.models;

import java.io.Serializable;

public class Asistencia implements Serializable {

    private int idAsistencia;

    private String nombreEmpleado;
    private String fecha;

    private String entrada;
    private String salida;

    private String estado;
    private String horario;

    public Asistencia(int idAsistencia,
                       String nombreEmpleado,
                       String fecha,
                       String entrada,
                       String salida,
                       String estado,
                       String horario) {

        this.idAsistencia = idAsistencia;

        this.nombreEmpleado = nombreEmpleado;
        this.fecha = fecha;

        this.entrada = entrada;
        this.salida = salida;

        this.estado = estado;
        this.horario = horario;
    }

    public Asistencia(String nombreEmpleado,
                       String fecha,
                       String entrada,
                       String salida,
                       String estado,
                       String horario) {

        this.nombreEmpleado = nombreEmpleado;
        this.fecha = fecha;

        this.entrada = entrada;
        this.salida = salida;

        this.estado = estado;
        this.horario = horario;
    }

    public int getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(int idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEntrada() {
        return entrada;
    }

    public void setEntrada(String entrada) {
        this.entrada = entrada;
    }

    public String getSalida() {
        return salida;
    }

    public void setSalida(String salida) {
        this.salida = salida;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }
}