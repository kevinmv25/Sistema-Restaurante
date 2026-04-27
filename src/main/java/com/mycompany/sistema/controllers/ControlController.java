/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.sistema.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
// IMPORTANTE: Asegúrate de que esta ruta coincida con tu paquete de productos
import com.mycompany.sistema.controllers.ProductoController; 

public class ControlController implements Initializable {

    @FXML
    private ProductoController productoController; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    @FXML
    private void btnBebidasClick() {
        productoController.cargarProductos("Bebidas");
    }

    @FXML
    private void btnDesayunosClick() {
        productoController.cargarProductos("Desayunos");
    }

    @FXML
    private void btnComidasClick() {
        productoController.cargarProductos("Comidas");
    }

    @FXML
    private void btnPostresClick() {
        productoController.cargarProductos("Postres");
    }
} 

