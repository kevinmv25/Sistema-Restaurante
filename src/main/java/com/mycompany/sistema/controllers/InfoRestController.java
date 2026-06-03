/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.sistema.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author rojas
 */
public class InfoRestController implements Initializable {
    
    @FXML
    private Button btnMInfo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void mostrarMenu(ActionEvent event) {
        ContextMenu menu = new ContextMenu();
        
        MenuItem itemReservar = new MenuItem("Hacer una Reservación");
        MenuItem itemHistorial = new MenuItem("Mis Reservaciones");
        MenuItem itemSalir = new MenuItem("Cerrar Sesión");

        // Llamados limpios compartiendo únicamente la ruta de texto
        itemReservar.setOnAction(e -> cambiarEscenaMenu("/scenes/Usuario/Reservacion.fxml"));
        itemHistorial.setOnAction(e -> cambiarEscenaMenu("/scenes/Usuario/HistorialReservas.fxml"));
        itemSalir.setOnAction(e -> cambiarEscenaMenu("/scenes/login.fxml"));
        
        // Incluidos todos los elementos físicos en la lista desplegable
        menu.getItems().addAll(itemReservar, itemHistorial, new SeparatorMenuItem(), itemSalir);
        menu.show(btnMInfo, Side.BOTTOM, 0, 0);
    }

    private void cambiarEscenaMenu(String ruta) {
        try {
            URL url = getClass().getResource(ruta);
            if (url == null) {
                System.err.println("¡ERROR! No se encontró el archivo FXML en: " + ruta);
                return;
            }
            Parent root = FXMLLoader.load(url);
            
            // Localiza de manera óptima la ventana actual a través del botón FXML
            Stage stage = (Stage) btnMInfo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            System.err.println("Error al cargar la escena: " + ruta);
            ex.printStackTrace();
        }
    }
    
}
