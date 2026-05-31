package com.mycompany.sistema.models;

public class Producto {
    // Los campos deben ser privados
    private String nombre;
    private Double precio;
    private String categoria;
    private String descripcion;
    private int Id;

    // Constructor: necesario para crear nuevos objetos
    public Producto(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
    }

    // --- ESTOS SON LOS MÉTODOS OBLIGATORIOS (GETTERS) ---
    // La PropertyValueFactory busca estos métodos automáticamente
    
    public String getNombre() {
        return nombre;
    }
    

    public Double getPrecio() {
        return precio;
    }
    
    public String getCategoria() {
        return categoria;
    }
    
    public String getDescripcion(){
        return descripcion;
    }
    
    public int getId(){
        return Id;
    }
    // Opcional: Métodos para modificar datos (Setters)
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
