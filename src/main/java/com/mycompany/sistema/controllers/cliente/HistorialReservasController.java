package com.mycompany.sistema.controllers.cliente;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

import com.mycompany.sistema.controllers.LoginController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import com.mycompany.sistema.models.cliente.Reservacion; 
import lib.SqlLib;

/**
 * FXML Controller class
 *
 * @author rojas
 */
public class HistorialReservasController implements Initializable {

    // Componentes de la Tabla
    @FXML private TableView<Reservacion> tablaReservaciones;
    @FXML private TableColumn<Reservacion, String> colFolio;
    @FXML private TableColumn<Reservacion, String> colFecha;
    @FXML private TableColumn<Reservacion, String> colEstado;

    // Componentes de los Detalles Laterales
    @FXML private Label lblFolio;
    @FXML private Label lblFecha;
    @FXML private Label lblHora;
    @FXML private Label lblMesa;
    @FXML private Label lblPersonas;
    @FXML private Label lblEstado;
    
    @FXML private Button btnMInfo;

    private SqlLib db;
    
    // Variable global para almacenar temporalmente el objeto a modificar
    public static Reservacion RESERVA_A_MODIFICAR = null;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        db = new SqlLib();
        
        // 1. Configurar las columnas para que sepan qué atributo del objeto leer
        colFolio.setCellValueFactory(new PropertyValueFactory<>("folio"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estatus"));
        
        // 2. Escuchar cuando el usuario hace clic en una fila de la tabla para mostrar los detalles al lado
        tablaReservaciones.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> mostrarDetalles(newValue)
        );

        // 3. Cargar los datos del usuario activo
        cargarHistorial();
    }    
    
    private void cargarHistorial() {
        // Recuperamos el correo guardado globalmente en el Login
        String correoUsuario = LoginController.CORREO_SESION;
        
        System.out.println("DEBUG: Cargando reservaciones para el correo: " + correoUsuario);
        
        if (correoUsuario != null && !correoUsuario.isEmpty()) {
            ObservableList<Reservacion> historial = db.getReservacionesPorUsuario(correoUsuario);
            System.out.println("DEBUG: Cantidad de registros encontrados: " + historial.size());
            tablaReservaciones.setItems(historial);
        } else {
            System.err.println("¡ADVERTENCIA! El correo de sesión está vacío o es nulo.");
        }
    }

    private void mostrarDetalles(Reservacion reserva) {
        if (reserva != null) {
            lblFolio.setText(reserva.getFolio());
            lblFecha.setText(reserva.getFecha());
            lblHora.setText(reserva.getHora());
            lblMesa.setText(reserva.getMesa());
            lblPersonas.setText(String.valueOf(reserva.getPersonas()));
            lblEstado.setText(reserva.getEstatus());
        } else {
            // Limpiar los labels si no hay nada seleccionado
            lblFolio.setText("");
            lblFecha.setText("");
            lblHora.setText("");
            lblMesa.setText("");
            lblPersonas.setText("");
            lblEstado.setText("");
        }
    }
    
    private void limpiarDetalles() {
        lblFolio.setText("");
        lblFecha.setText("");
        lblHora.setText("");
        lblMesa.setText("");
        lblPersonas.setText("");
        lblEstado.setText("");
    }
    
    @FXML
    private void mostrarMenu(ActionEvent event) {
        ContextMenu menu = new ContextMenu();
        
        MenuItem itemInicio = new MenuItem("Inicio / Información");
        MenuItem itemReservar = new MenuItem("Hacer una Reservación");
        MenuItem itemHistorial = new MenuItem("Mis Reservaciones");
        MenuItem itemSalir = new MenuItem("Cerrar Sesión");

        itemInicio.setOnAction(e -> cambiarEscenaMenu("/scenes/Usuario/InfoRest.fxml"));
        itemReservar.setOnAction(e -> cambiarEscenaMenu("/scenes/Usuario/Reservacion.fxml"));
        itemHistorial.setOnAction(e -> cambiarEscenaMenu("/scenes/Usuario/HistorialReservas.fxml"));
        itemSalir.setOnAction(e -> cambiarEscenaMenu("/scenes/login.fxml"));
        
        menu.getItems().addAll(itemInicio, itemReservar, itemHistorial, new SeparatorMenuItem(), itemSalir);
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
            Stage stage = (Stage) btnMInfo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            System.err.println("Error al cargar la escena: " + ruta);
            ex.printStackTrace();
        }
    }
    
    @FXML
    private void btnModificarReserva(ActionEvent event) {
        Reservacion seleccionada = tablaReservaciones.getSelectionModel().getSelectedItem();
        
        if (seleccionada == null) {
            mostrarAlerta("Selecciona una reservación.");
            return;
        }
        
        String estado = seleccionada.getEstatus();

            if (estado.equals("Cancelada") ||
                estado.equals("Modificada") ||
                estado.equals("Caducada")) {

                mostrarAlerta(
                    "Esta reservación ya no puede modificarse."
                );
                return;
            }
            
        int idMesa = Integer.parseInt(seleccionada.getMesa().replace("Mesa ", "").trim());
        String estadoActualMesa = db.obtenerEstadosMesas().get(idMesa);
        if ("Ocupada".equalsIgnoreCase(estadoActualMesa)) {
            mostrarAlerta("No puedes modificar esta reservación porque ya te encuentras en la mesa.");
            return;
        }

        RESERVA_A_MODIFICAR = seleccionada;
        
        // Aquí rediriges a la pantalla de Reservación
        cambiarEscenaMenu("/scenes/Usuario/Reservacion.fxml");
    }

    @FXML
    private void btnCancelarReserva(ActionEvent event) {
        Reservacion seleccionada = tablaReservaciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta("Selecciona una reservación de la tabla para cancelarla.");
            return;
        }

        String estado = seleccionada.getEstatus();

        if ("Cancelada".equals(estado) || "Modificada".equals(estado)) {
            mostrarAlerta("La reservación ya no está activa.");
            return;
        }
        
        int idMesa = Integer.parseInt(
            seleccionada.getMesa()
            .replace("Mesa ", "")
            .trim()
        );
        
        String estadoActualMesa = db.obtenerEstadosMesas().get(idMesa);
        
        //si la mesa ya se encuentra ocupada, ya no puede cancelar
        if ("Ocupada".equalsIgnoreCase(estadoActualMesa)) {
            mostrarAlerta("No puedes cancelar esta reservación porque ya ha sido confirmada y la mesa se encuentra ocupada.");
            return;
        }
        
        boolean exito = db.cancelarReservacion(seleccionada.getFolio());
        
        if (exito) {
            limpiarDetalles();
            cargarHistorial();
            db.sincronizarEstadoMesas();
            mostrarAlerta("La reservación " + seleccionada.getFolio() + " ha sido cancelada exitosamente.");
        } else {
            mostrarAlerta("Hubo un error al procesar la cancelación en la base de datos.");
        }
    }
    
    private void mostrarAlerta(String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Sistema Restaurante");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
