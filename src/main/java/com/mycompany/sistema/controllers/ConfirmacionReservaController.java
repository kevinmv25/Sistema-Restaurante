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
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;

/**
 * FXML Controller class
 * @author rojas
 */
public class ConfirmacionReservaController implements Initializable {
    
    @FXML private Button btnMConf;
    @FXML private Label lblFolio, lblDia, lblHora, lblPersonas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Aquí puedes inicializar algo si es necesario
    }    
    
    // Método para recibir los datos de la pantalla anterior
    public void configurarDatos(String dia, String hora, String personas) {
        int numFolio = (int)(Math.random() * 10000);
        lblFolio.setText("Folio de reservación: RES-" + numFolio);

        lblDia.setText("Día: " + dia);
        lblHora.setText("Hora: " + hora);
        lblPersonas.setText("Personas: " + personas);
    }

    // Método para el botón oscuro de "Finalizar" en el centro
    @FXML
    private void finalizar(ActionEvent event) {
        // Usamos tu método cambiarEscena para mantener el código limpio
        cambiarEscena("/scenes/Usuario/InfoRest.fxml", event);
    }
    
    @FXML
    private void mostrarMenu(ActionEvent event) {
        ContextMenu menu = new ContextMenu();
        MenuItem itemEditar = new MenuItem("Corregir Datos (Volver)");
        MenuItem itemInfo = new MenuItem("Ver Información");
        MenuItem itemSalir = new MenuItem("Cerrar Sesión");

        itemEditar.setOnAction(e -> cambiarEscena("/scenes/Usuario/Reservacion.fxml", event));
        itemInfo.setOnAction(e -> cambiarEscena("/scenes/Usuario/InfoRest.fxml", event));
        itemSalir.setOnAction(e -> cambiarEscena("/scenes/login.fxml", event));

        menu.getItems().addAll(itemEditar, itemInfo, new SeparatorMenuItem(), itemSalir);
        menu.show(btnMConf, Side.BOTTOM, 0, 0);
    }
    
    // Tu método genérico de cambio de escena (está perfecto)
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