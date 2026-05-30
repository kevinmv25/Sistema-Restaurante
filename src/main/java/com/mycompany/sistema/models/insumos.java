package com.mycompany.sistema.models;

import java.io.Serializable;

public class insumos implements Serializable {

    private int idInsumo;

    private String nombre;
    private double stock;

    private String unidadMedida;
    private String categoria;

    public insumos(int idInsumo, String nombre, double stock, String unidadMedida, String categoria) {
        this.idInsumo = idInsumo;
        this.nombre = nombre;
        this.stock = stock;
        this.unidadMedida = unidadMedida;
        this.categoria = categoria;
    }

    public insumos(String nombre, double stock, String unidadMedida, String categoria) {

        this.nombre = nombre;
        this.stock = stock;
        this.unidadMedida = unidadMedida;
        this.categoria = categoria;
    }

    public int getIdInsumo() {
        return idInsumo;
    }

    public void setIdInsumo(int idInsumo) {
        this.idInsumo = idInsumo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}