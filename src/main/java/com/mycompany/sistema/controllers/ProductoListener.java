package com.mycompany.sistema.controllers;
import com.mycompany.sistema.models.Producto;

public interface ProductoListener {
    void onProductoSeleccionado(Producto p);
}