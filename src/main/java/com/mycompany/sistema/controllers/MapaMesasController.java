/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.sistema.controllers;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    
        //Si el color no es gris, esta ocupada o reservada
        if (!mesaPresionada.getFill().equals(Color.GRAY)) {
            mostrarAlerta("Mesa no disponible", 
                          "La mesa seleccionada no está disponible actualmente", 
                          "Por favor, selecciona una mesa que esté en color gris.");
            return; //se detiene la ejecución
        }

        //Si es gris, permite la selección normal
        actualizarColoresMesas(); //Limpia selecciones previas
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

            sql.actualizarEstadoMesa(idMesa, "Ocupada");

            actualizarColoresMesas();
            btnConfirmar.setDisable(true);
            mesaSeleccionadaActual = null;
        } catch (Exception e) {
            // Esto evita que el programa se cierre y te muestra el error en la consola
            e.printStackTrace(); 
        }
    }
    
    //actualiza el mapa
    @FXML
    private void actualizarColoresMesas() {
        Map<Integer, String> datos = sql.obtenerEstadosMesas();

        // Recorremos las 10 mesas
        for (int i = 1; i <= 10; i++) {
            String estado = datos.get(i);
            Circle circuloActual = obtenerCirculoPorId(i);

            if (circuloActual != null && estado != null) {
                circuloActual.setStroke(Color.TRANSPARENT);
                circuloActual.setStrokeWidth(0);
                
                switch (estado) {
                    case "Ocupada":
                        circuloActual.setFill(Color.RED);
                        break;
                    case "Reservada":
                        circuloActual.setFill(Color.YELLOW);
                        break;
                    case "Disponible":
                        circuloActual.setFill(Color.GRAY);
                        break;
                }
                circuloActual.setStroke(Color.TRANSPARENT);
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
    
    //muestra mensaje de error
    private void mostrarAlerta(String titulo, String encabezado, String contenido) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait(); // El usuario debe dar clic en "Aceptar" para regresar al mapa
    }
}
