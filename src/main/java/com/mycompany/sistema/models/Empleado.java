package com.mycompany.sistema.models;

public class Empleado {

    private int id;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;
    private String puesto;
    private String horario;
    private String estatus;

    // 🔴 NUEVOS CAMPOS
    private String vacaciones;
    private double salario;

    public Empleado() {}

    public Empleado(int id, String nombre, String apellido, String telefono,
                    String correo, String puesto, String horario, String estatus) {

        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
        this.puesto = puesto;
        this.horario = horario;
        this.estatus = estatus;
    }

    // ================= GETTERS Y SETTERS =================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }


    public String getVacaciones() { return vacaciones; }
    public void setVacaciones(String vacaciones) { this.vacaciones = vacaciones; }

    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }
}
