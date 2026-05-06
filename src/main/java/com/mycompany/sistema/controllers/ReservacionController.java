/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.sistema.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lib.SqlLib;

/**
 * FXML Controller class
 *
 * @author rojas
 */
public class ReservacionController implements Initializable {
    
    
    @FXML
    private Button btnMenu;
    
    @FXML
    private Button btnSeleccionarHora;
    private String horaSeleccionada = "";
    private int personas = 0;
    
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtNombre;
    @FXML
    private DatePicker dpFecha;
    
    @FXML
    private Button btnHora;
    
    private SqlLib sql = new SqlLib();
    private int idMesaSeleccionada = 1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void abrirSeleccionHora(ActionEvent event) {
        List<String> horas = Arrays.asList("14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("14:00", horas);
        dialog.setTitle("Seleccionar Hora");
        dialog.setHeaderText("¿A qué hora vendrá?");
        dialog.setContentText("Elija un horario:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(hora -> {
            this.horaSeleccionada = hora;
            btnHora.setText(hora);
        });
    }
    
    @FXML
    private void enviarReservacion(ActionEvent event) {
        if (personas == 0 || horaSeleccionada.isEmpty() || 
            txtApellido.getText().trim().isEmpty() || 
            txtNombre.getText().trim().isEmpty() || 
            dpFecha.getValue() == null) {

            mostrarAlerta("Por favor, completa todos los campos.");
            return;
        }
        try {
            // idMesaSeleccionada debe ser el ID de la mesa que el usuario escogió en el mapa
            sql.actualizarEstadoMesa(idMesaSeleccionada, "Reservada");
            System.out.println("Base de datos actualizada: Mesa " + idMesaSeleccionada + " Reservada.");
        } catch (Exception e) {
            System.err.println("Error al actualizar estado: " + e.getMessage());
        }
        cambiarEscena("/scenes/Usuario/ConfirmacionReserva.fxml", event);
    }

    @FXML
    private void seleccionarPersonas(ActionEvent event) {
        Button btn = (Button) event.getSource();
        personas = Integer.parseInt(btn.getText());

        System.out.println("Personas seleccionadas: " + personas);
    }
    
    // Método de alerta que faltaba
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    @FXML
    private void mostrarMenu(ActionEvent event) {
        ContextMenu menu = new ContextMenu();
        MenuItem itemInfo = new MenuItem("Regresar a Información");
        MenuItem itemSalir = new MenuItem("Cerrar Sesión");

        itemInfo.setOnAction(e -> cambiarEscena("/scenes/Usuario/InfoRest.fxml", event));
        itemSalir.setOnAction(e -> cambiarEscena("/scenes/login.fxml", event));

        menu.getItems().addAll(itemInfo, new SeparatorMenuItem(), itemSalir);
        menu.show(btnMenu, Side.BOTTOM, 0, 0);
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

