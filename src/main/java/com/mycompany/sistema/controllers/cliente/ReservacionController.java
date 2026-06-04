/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.sistema.controllers.cliente;

import com.mycompany.sistema.controllers.LoginController;
import com.mycompany.sistema.controllers.cliente.HistorialReservasController;
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
    
    @FXML private Button btnMInfo;
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
        
        dpFecha.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
        @Override
        public void updateItem(java.time.LocalDate date, boolean empty) {
            super.updateItem(date, empty);
            //deshabilita fechas anteriores a hoy
            if (date.isBefore(java.time.LocalDate.now())) {
                setDisable(true);
                setStyle("-fx-background-color: #ffc0c0;");
            }
        }
    });
        
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

            String fechaStr = dpFecha.getValue().toString();

            // Recuperamos el correo del usuario que inició sesión
            String correoCliente = LoginController.CORREO_SESION; 
            if(correoCliente == null || correoCliente.isEmpty()) {
                correoCliente = "cliente.anonimo@restaurante.com"; // Respaldo por si pruebas sin login
            }

            boolean exito = sql.registrarNuevaReservacion(
                correoCliente,
                fechaStr,
                horaSeleccionada + ":00",
                idMesaSeleccionada,
                personas
            );
            
            if (!exito) {
                mostrarAlerta("Hubo un error al procesar tu reservación en la base de datos.");
                return;
            }
            
            if (HistorialReservasController.RESERVA_A_MODIFICAR != null) {
                com.mycompany.sistema.models.cliente.Reservacion vieja = HistorialReservasController.RESERVA_A_MODIFICAR;
                
                // Extraemos el ID de la mesa antigua
                int idMesaVieja = Integer.parseInt(
                    vieja.getMesa()
                    .replace("Mesa ", "")
                    .trim()
                );
            
                sql.actualizarEstadoMesa(idMesaVieja, "Disponible");
                sql.actualizarEstatusReserva(vieja.getFolio(), "Modificada");
                sql.sincronizarEstadoMesas();
                HistorialReservasController.RESERVA_A_MODIFICAR = null;
                
                System.out.println("DEBUG: Modificación completada. Reservación vieja desactivada con éxito.");
            }
            

            // Ir a la pantalla de confirmación cargando el FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/Usuario/ConfirmacionReserva.fxml"));
            Parent root = loader.load();

            ConfirmacionReservaController controllerDestino = loader.getController();
            String personasStr = String.valueOf(personas);

            // Envío de parámetros estéticos a la pantalla de Comprobante
            controllerDestino.configurarDatos(fechaStr, horaSeleccionada, personasStr, "Mesa " + idMesaSeleccionada, this.idMesaSeleccionada);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.println("Error al cargar Confirmacion: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error general: " + e.getMessage());
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
    
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    @FXML
    private void mostrarMenu(ActionEvent event) {
        ContextMenu menu = new ContextMenu();
        
        MenuItem itemInicio = new MenuItem("Inicio / Información");
        MenuItem itemHistorial = new MenuItem("Mis Reservaciones");
        MenuItem itemSalir = new MenuItem("Cerrar Sesión");
        
        itemInicio.setOnAction(e -> {
            HistorialReservasController.RESERVA_A_MODIFICAR = null; 
            cambiarEscenaMenu("/scenes/Usuario/InfoRest.fxml");
        });
        itemHistorial.setOnAction(e -> {
            HistorialReservasController.RESERVA_A_MODIFICAR = null;
            cambiarEscenaMenu("/scenes/Usuario/HistorialReservas.fxml");
        });
        itemSalir.setOnAction(e -> {
            HistorialReservasController.RESERVA_A_MODIFICAR = null;
            cambiarEscenaMenu("/scenes/login.fxml");
        });
        
        menu.getItems().addAll(itemInicio, itemHistorial, new SeparatorMenuItem(), itemSalir);
        menu.show(btnMInfo, Side.BOTTOM, 0, 0);
    }
    
    private void cargarMesasDisponibles() {
        cmbMesas.getItems().clear(); 

        //obtenemos el mapa con los estados actuales de la BD
        Map<Integer, String> estados = sql.obtenerEstadosMesas();

        for (int i = 1; i <= 12; i++) {
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
}