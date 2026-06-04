/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.sistema.controllers.cliente;

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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import lib.SqlLib;

/**
 * FXML Controller class
 * @author rojas
 */
public class ConfirmacionReservaController implements Initializable {
    
    @FXML private Button btnMConf;
    @FXML private Label lblFolio, lblDia, lblHora, lblPersonas;
    @FXML private Label lblTolerancia;
    private String folioActual;
    
    private SqlLib sql = new SqlLib();
    // Guardamos el ID numérico de la mesa para poder liberarla si se cancela
    private int idMesaGuardada = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
    
    // Método para recibir los datos de la pantalla anterior
    public void configurarDatos(String fecha, String hora, String personas, String mesa, int idMesa) {
        this.idMesaGuardada = idMesa;
        
        int numFolio = (int)(Math.random() * 10000);
        lblFolio.setText("Folio de reservación: RES-" + numFolio);

        lblDia.setText("Día: " + fecha);
        lblHora.setText("Hora: " + hora);
        lblPersonas.setText("Personas: " + personas + "   |   " + mesa);
        
        try {
            LocalTime tiempoOriginal = LocalTime.parse(hora);
            // Sumamos 15 minutos
            LocalTime tiempoTolerancia = tiempoOriginal.plusMinutes(15);
            
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm");
            String horaFinal = tiempoTolerancia.format(formato);

            lblTolerancia.setText("Recuerda llegar a tiempo. Tu mesa se mantendrá reservada hasta las " 
                                  + horaFinal + " hrs (15 min de tolerancia).");
        } catch (Exception e) {
            lblTolerancia.setText("Recuerda llegar a tiempo. Tienes 15 min de tolerancia.");
        }
    }

    @FXML
    private void finalizar(ActionEvent event) {
        cambiarEscena("/scenes/Usuario/InfoRest.fxml", event);
    }
    
    @FXML
    private void modificarReserva(ActionEvent event) {
        // Si va a corregir datos, liberamos la mesa primero para que vuelva a aparecer disponible
        if (idMesaGuardada != 0) {
            sql.actualizarEstatusReserva(folioActual, "Modificada");
        }
        cambiarEscena("/scenes/Usuario/Reservacion.fxml", event);
    }

    @FXML
    private void cancelarReserva(ActionEvent event) {
        //si cancela la reserva, liberamos la mesa en la base de datos de inmediato
        if (idMesaGuardada != 0) {
            sql.actualizarEstadoMesa(idMesaGuardada, "Disponible");
        }
        cambiarEscena("/scenes/Usuario/InfoRest.fxml", event);
    }
    
    @FXML
    private void mostrarMenu(ActionEvent event) {
        ContextMenu menu = new ContextMenu();
        MenuItem itemEditar = new MenuItem("Corregir Datos (Volver)");
        MenuItem itemInfo = new MenuItem("Ver Información");
        MenuItem itemSalir = new MenuItem("Cerrar Sesión");

        itemEditar.setOnAction(e -> {
            if (idMesaGuardada != 0) sql.actualizarEstadoMesa(idMesaGuardada, "Disponible");
            cambiarEscena("/scenes/Usuario/Reservacion.fxml", event);
        });
        
        itemInfo.setOnAction(e -> cambiarEscena("/scenes/Usuario/InfoRest.fxml", event));
        itemSalir.setOnAction(e -> cambiarEscena("/scenes/login.fxml", event));

        menu.getItems().addAll(itemEditar, itemInfo, new SeparatorMenuItem(), itemSalir);
        menu.show(btnMConf, Side.BOTTOM, 0, 0);
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