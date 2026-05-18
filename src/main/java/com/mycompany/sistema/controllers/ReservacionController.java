/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.sistema.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import javafx.scene.control.ComboBox;
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
    
    @FXML private Button btnMenu;
    @FXML private Button btnHora;
    @FXML private TextField txtApellido;
    @FXML private TextField txtNombre;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cmbMesas;
    
    private String horaSeleccionada = "";
    private int personas = 0;
    private Button botonPersonaSeleccionado = null;
    
    private SqlLib sql = new SqlLib();
    private int idMesaSeleccionada = 1;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarMesasDisponibles();
        
        dpFecha.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                cargarMesasDisponibles();
            }
        });
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
        // 1. OBTENER LA MESA SELECCIONADA DEL COMBOBOX
        String mesaSeleccionada = cmbMesas.getValue();
        
        if (personas == 0 || horaSeleccionada.isEmpty() || 
            txtApellido.getText().trim().isEmpty() || 
            txtNombre.getText().trim().isEmpty() || 
            dpFecha.getValue() == null || mesaSeleccionada == null) {

            mostrarAlerta("Por favor, completa todos los campos, incluyendo la selección de mesa.");
            return;
        }
        
        try {
            String numeroMesaStr = mesaSeleccionada.replace("Mesa ", "").trim();
            this.idMesaSeleccionada = Integer.parseInt(numeroMesaStr);
            
            // idMesaSeleccionada debe ser el ID de la mesa que el usuario escogió en el mapa
            sql.actualizarEstadoMesa(idMesaSeleccionada, "Reservada");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/Usuario/ConfirmacionReserva.fxml"));
            Parent root = loader.load();
            
            ConfirmacionReservaController controllerDestino = loader.getController();
            
            //extraemos los datos
            String fechaStr = dpFecha.getValue().toString();
            String personasStr = String.valueOf(personas);

            // Llamamos al método que tenemos en ConfirmacionReservaController
            controllerDestino.configurarDatos(fechaStr, horaSeleccionada, personasStr, "Mesa " + idMesaSeleccionada);
            
            // Cambio de escena manual (sin usar tu método cambiarEscena para poder usar el loader)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
        System.err.println("Error al cargar Confirmacion: " + e.getMessage());
        e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error en la BD: " + e.getMessage());
        }
    }

    @FXML
    private void seleccionarPersonas(ActionEvent event) {
        if (botonPersonaSeleccionado != null) {
            botonPersonaSeleccionado.setStyle(""); // Vuelve al estilo CSS por defecto (gris claro)
        }
        
        Button btn = (Button) event.getSource();
        personas = Integer.parseInt(btn.getText());
        
        btn.setStyle("-fx-background-color: #2d5a27; -fx-text-fill: white; -fx-font-weight: bold;");
        botonPersonaSeleccionado = btn;

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
    
    private void cargarMesasDisponibles() {
        cmbMesas.getItems().clear(); 

        //obtenemos el mapa con los estados actuales de la BD
        Map<Integer, String> estados = sql.obtenerEstadosMesas();

        for (int i = 1; i <= 10; i++) {
            String estado = estados.get(i);

            if (estado != null && estado.equals("Disponible")) {
                cmbMesas.getItems().add("Mesa " + i);
            }
        }
        if (cmbMesas.getItems().isEmpty()) {
            cmbMesas.setPromptText("No hay mesas disponibles");
        } else {
            cmbMesas.setPromptText("Selecciona una mesa...");
        }
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

