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
        MenuItem itemSalir = new MenuItem("Cerrar Sesión");

        itemReservar.setOnAction(e -> cambiarEscena("/scenes/Usuario/Reservacion.fxml", event));
        itemSalir.setOnAction(e -> cambiarEscena("/scenes/login.fxml", event));

        menu.getItems().addAll(itemReservar, new SeparatorMenuItem(), itemSalir);
        menu.show(btnMInfo, Side.BOTTOM, 0, 0);
    }

    private void cambiarEscena(String ruta, ActionEvent event) {
        try {
            URL url = getClass().getResource(ruta);
            if (url == null) {
                System.err.println("¡ERROR! No se encontró el archivo FXML en: " + ruta);
                return;
            }
            Parent root = FXMLLoader.load(url);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            System.err.println("Error al cargar la escena: " + ruta);
            ex.printStackTrace();
        }
    }
    
}
