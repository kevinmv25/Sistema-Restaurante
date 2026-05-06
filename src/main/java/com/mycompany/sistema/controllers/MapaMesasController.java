/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.sistema.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import lib.SqlLib;

/**
 * FXML Controller class
 *
 * @author rojas
 */
public class MapaMesasController implements Initializable {   
    @FXML
    private Circle mesa1, mesa2, mesa3, mesa4, mesa5, mesa6, mesa7, mesa8, mesa9, mesa10;
    
    @FXML
    private Button btnConfirmar;
    
    @FXML
    private Button btnSalir;
    
    private SqlLib sql = new SqlLib();
    
    private Circle mesaSeleccionadaActual = null;
    
    //se definen los colores de las mesas como constantes
    private final Color COLOR_DISPONIBLE = Color.GRAY;
    private final Color COLOR_OCUPADA = Color.RED;
    private final Color COLOR_RESERVADA = Color.YELLOW;
    private final Color COLOR_SELECCIONADA = Color.WHITE;
    private final Color BORDE_SELECCIONADA = Color.web("#2d5a27");
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        actualizarColoresMesas();
        
        if (btnConfirmar != null) {
            btnConfirmar.setDisable(true); //Inicialmente el botón está desactivado
        }
    }    
    
    //
    @FXML
    private void seleccionarMesa(MouseEvent event) {
        Circle mesaPresionada = (Circle) event.getSource();

        //Si el usuario hace clic en la mesa que ya tiene seleccionada, no hacemos nada
        if (mesaPresionada == mesaSeleccionadaActual) {
            return; 
        }

        //Si no es gris Y no es la seleccionada actual, entonces sí está ocupada/reservada
        if (!mesaPresionada.getFill().equals(Color.GRAY) && mesaPresionada != mesaSeleccionadaActual) {
            mostrarAlerta("Mesa no disponible", 
                          "La mesa seleccionada no está disponible actualmente", 
                          "Por favor, selecciona una mesa que esté en color gris.");
            return;
        }
        actualizarColoresMesas(); 
        btnConfirmar.setDisable(false);
        cambiarEstadoSeleccionado(mesaPresionada);

        this.mesaSeleccionadaActual = mesaPresionada;
    }
    
    //Cambia el color de la mesa al ser seleccionada
    private void cambiarEstadoSeleccionado(Circle mesaCirculo) {
        mesaCirculo.setFill(COLOR_SELECCIONADA);
        mesaCirculo.setStroke(BORDE_SELECCIONADA);
        mesaCirculo.setStrokeWidth(3.0);
    }
    
    //actualiza el estado de las mesas
    @FXML
    private void onActualizar(ActionEvent event) {
        if (mesaSeleccionadaActual == null) {
            mostrarAlerta("Error", "No hay selección", "Por favor selecciona una mesa primero.");
            return; 
        }

        try {
            String idStr = mesaSeleccionadaActual.getId().replace("mesa", "");
            int idMesa = Integer.parseInt(idStr);

            //Actualizamos en la base de datos
            sql.actualizarEstadoMesa(idMesa, "Ocupada");

            actualizarColoresMesas();
            
            mesaSeleccionadaActual = null;
            btnConfirmar.setDisable(true);

            System.out.println("Mesa " + idMesa + " actualizada correctamente. El programa sigue listo.");

        } catch (Exception e) {
            e.printStackTrace(); 
            mostrarAlerta("Error de BD", "No se pudo actualizar", "Verifica la conexión con Workbench.");
        }
    }
    
    //actualiza el mapa
    @FXML
    private void actualizarColoresMesas() {
        Map<Integer, String> datos = sql.obtenerEstadosMesas();

        for (int i = 1; i <= 10; i++) {
            String estado = datos.get(i);
            Circle circuloActual = obtenerCirculoPorId(i);

            if (circuloActual != null && estado != null) {
                // Quitamos bordes de selecciones previas
                circuloActual.setStroke(Color.TRANSPARENT);
                circuloActual.setStrokeWidth(0);

                switch (estado) {
                    case "Ocupada":
                        circuloActual.setFill(COLOR_OCUPADA);
                        break;
                    case "Reservada":
                        circuloActual.setFill(COLOR_RESERVADA);
                        break;
                    case "Disponible":
                        circuloActual.setFill(COLOR_DISPONIBLE);
                        break;
                }
            }
        }
    }
    
    //encuentra la mesa por su número
    private Circle obtenerCirculoPorId(int id) {
        switch (id) {
            case 1: return mesa1; case 2: return mesa2; case 3: return mesa3;
            case 4: return mesa4; case 5: return mesa5; case 6: return mesa6;
            case 7: return mesa7; case 8: return mesa8; case 9: return mesa9;
            case 10: return mesa10;
            default: return null;
        }
    }
    
    @FXML
    private void regresarLogin(ActionEvent event) {
        try {
            // La ruta basada en tu estructura de carpetas real
            String rutaFXML = "/scenes/login.fxml"; 
            URL url = getClass().getResource(rutaFXML);

            if (url == null) {
                System.err.println("No se encontró el archivo en: " + rutaFXML);
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

            System.out.println("Regresando al login correctamente.");

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista", "Revisa la consola de NetBeans.");
        }
    }
    
    //muestra mensaje de error
    private void mostrarAlerta(String titulo, String encabezado, String contenido) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait(); // El usuario debe dar clic en "Aceptar" para regresar al mapa
    }
}
