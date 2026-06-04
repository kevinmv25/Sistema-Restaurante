/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistema.models.cliente;

/**
 *
 * @author rojas
 */
public class Reservacion {
    private String folio;
    private String fecha;
    private String hora;
    private String mesa;
    private int personas;
    private String estatus;
    private String nombre;

    public Reservacion(String folio, String fecha, String hora, String mesa, int personas, String estado, String nombre) {
        this.folio = folio;
        this.fecha = fecha;
        this.hora = hora;
        this.mesa = mesa;
        this.personas = personas;
        this.estatus = estado;
        this.nombre = nombre;
    }

    // Getters obligatorios para que el TableView pueda leer las propiedades
    public String getFolio() { return folio; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public String getMesa() { return mesa; }
    public int getPersonas() { return personas; }
    public String getEstatus() { return estatus; }
    public String getNombre() { return nombre; }
}
