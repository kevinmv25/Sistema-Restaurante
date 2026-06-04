/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.sistema.controllers.recepcionista;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import lib.SqlLib;
import javafx.scene.control.cell.MapValueFactory;

/**
 * FXML Controller class
 *
 * @author rojas
 */
public class MapaMesasController implements Initializable {   
    @FXML
    private Circle mesa1, mesa2, mesa3, mesa4, mesa5, mesa6, mesa7, mesa8, mesa9, mesa10, mesa11, mesa12;
    
    @FXML
    private Button btnConfirmar, btnSalir, btnQuitarReserva, btnAgregarLista, btnPasarMesa;
    
    @FXML
    private TextField txtNombreEspera, txtPersonasEspera;
    
    @FXML
    private TableView<com.mycompany.sistema.models.cliente.Reservacion> tblReservaciones; 
    @FXML
    private TableColumn<com.mycompany.sistema.models.cliente.Reservacion, String> colResNombre;
    @FXML
    private TableColumn<com.mycompany.sistema.models.cliente.Reservacion, String> colResHora;
    @FXML
    private TableColumn<com.mycompany.sistema.models.cliente.Reservacion, String> colResMesa;
    @FXML
    private TableColumn<com.mycompany.sistema.models.cliente.Reservacion, String> colResEstatus;
    
    @FXML
    private TableView<Map<String, Object>> tblListaEspera;
    @FXML
    private TableColumn<Map<String, Object>, Integer> colEspPosicion;
    @FXML
    private TableColumn<Map<String, Object>, String> colEspNombre;
    @FXML
    private TableColumn<Map<String, Object>, Integer> colEspPersonas;
    
    private SqlLib sql = new SqlLib();
    
    private Circle mesaSeleccionadaActual = null;
    private int idMesaSeleccionadaActual = 0;
    
    private ObservableList<com.mycompany.sistema.models.cliente.Reservacion> listaReservaciones = FXCollections.observableArrayList();
    private ObservableList<Map<String, Object>> listaEspera = FXCollections.observableArrayList();
    
    // Constantes de color
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
        if (tblReservaciones != null) {
            tblReservaciones.setItems(listaReservaciones);
        }
        if (tblListaEspera != null) {
            tblListaEspera.setItems(listaEspera);
        }
        
        if (colResNombre != null) colResNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        if (colResHora != null) colResHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        if (colResMesa != null) colResMesa.setCellValueFactory(new PropertyValueFactory<>("mesa"));
        
        if (colResEstatus != null) {
            colResEstatus.setCellValueFactory(new PropertyValueFactory<>("estatus"));
            colResEstatus.setCellFactory(column -> new TableCell<com.mycompany.sistema.models.cliente.Reservacion, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        
                        if (getTableRow() != null && getTableRow().isSelected()) {
                            setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                            return;
                        }
                        
                        switch (item) {
                            case "Caducada":
                            case "Cancelada":
                                setStyle("-fx-text-fill: #ff3333; -fx-font-weight: bold;"); // Rojo
                                break;
                            case "En Tolerancia":
                                setStyle("-fx-text-fill: #ffcc00; -fx-font-weight: bold;"); // Amarillo / Ámbar
                                break;
                            case "Modificada":
                                setStyle("-fx-text-fill: #0077cc; -fx-font-weight: bold;"); // Azul para cambios
                                break;
                            case "Para Hoy":
                            case "Próxima":
                            case "Confirmada":
                                setStyle("-fx-text-fill: #00cc44; -fx-font-weight: bold;"); // Verde
                                break;
                            default:
                                // Para cualquier otro estado desconocido, usamos un gris oscuro/negro 
                                // para evitar que se mimetice con el fondo blanco de la tabla.
                                setStyle("-fx-text-fill: #222222;"); 
                                break;
                        }
                    }
                }
            });
        }
        
        if (colEspPosicion != null) colEspPosicion.setCellValueFactory(new MapValueFactory("posicion"));
        if (colEspNombre != null) colEspNombre.setCellValueFactory(new MapValueFactory("nombre"));
        if (colEspPersonas != null) colEspPersonas.setCellValueFactory(new MapValueFactory("personas"));
        
        // Cargar estado inicial seguro
        sql.sincronizarEstadoMesas();
        actualizarColoresMesas(); 
        cargarTablasDesdeBD();
        
        // Listeners
        tblListaEspera.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            evaluarEstadoBotonesPasarMesa();
        });
        
        tblReservaciones.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, reservacionSeleccionada) -> {

            if (reservacionSeleccionada != null) {
                if (mesaSeleccionadaActual != null) {
                    restaurarColorMesaPrevia(
                        mesaSeleccionadaActual,
                        idMesaSeleccionadaActual
                    );
                }
                try {
                    String mesaStr = reservacionSeleccionada
                            .getMesa()
                            .replace("Mesa ", "")
                            .trim();

                    int idMesa = Integer.parseInt(mesaStr);

                    Circle circuloMesa = obtenerCirculoPorId(idMesa);

                    if (circuloMesa != null) {
                        this.idMesaSeleccionadaActual = idMesa;
                        this.mesaSeleccionadaActual = circuloMesa;
                        cambiarEstadoSeleccionado(circuloMesa);
                    }
                } catch (Exception e) {
                    System.err.println(
                        "Error al mapear mesa desde tabla: "
                        + e.getMessage()
                    );
                }

                if (btnConfirmar != null) {
                    btnConfirmar.setDisable(false);
                }

                String estado = reservacionSeleccionada.getEstatus();

                boolean puedeEliminar =
                       estado.equalsIgnoreCase("Confirmada")
                    || estado.equalsIgnoreCase("Reservada")
                    || estado.equalsIgnoreCase("En Tolerancia");

                btnQuitarReserva.setDisable(!puedeEliminar);
            }
        });

        if (btnConfirmar != null) btnConfirmar.setDisable(true);
        if (btnQuitarReserva != null) btnQuitarReserva.setDisable(true);
        if (btnPasarMesa != null) btnPasarMesa.setDisable(true);
    }
    
    private void restaurarColorMesaPrevia(Circle mesa, int id) {
        if (mesa == null) return;
        mesa.setStroke(Color.TRANSPARENT);
        mesa.setStrokeWidth(0);
        Map<Integer, String> datos = sql.obtenerEstadosMesas();
        String estado = datos.get(id);
        if (estado != null) {
            switch (estado) {
                case "Ocupada": mesa.setFill(COLOR_OCUPADA); break;
                case "Reservada": mesa.setFill(COLOR_RESERVADA); break;
                default: mesa.setFill(COLOR_DISPONIBLE); break;
            }
        }
    }
    
    private void cargarTablasDesdeBD() {
        Platform.runLater(() -> {
            try {
                listaReservaciones.clear();
                listaEspera.clear();

                listaReservaciones.addAll(sql.obtenerTodasLasReservaciones()); 
                listaEspera.addAll(sql.obtenerListaEsperaVigente());

                tblReservaciones.refresh();
                tblListaEspera.refresh();
            } catch (Exception e) {
                System.err.println("Aviso: Configura los métodos de retorno de datos en tu SqlLib: " + e.getMessage());
            }
        });
    }
    
    @FXML
    private void seleccionarMesa(MouseEvent event) {
        // Validación de protección por si se hace clic en un nodo que no sea un círculo
        if (!(event.getSource() instanceof Circle)) {
            return;
        }
        
        Circle mesaPresionada = (Circle) event.getSource();

        if (mesaPresionada == mesaSeleccionadaActual) {
            return; 
        }

        if (!mesaPresionada.getFill().equals(COLOR_DISPONIBLE) && !mesaPresionada.getFill().equals(COLOR_RESERVADA)){
            mostrarAlerta("Mesa no disponible", 
                          "Mesa Ocupada", 
                          "Esta mesa ya está ocupada por clientes. No puedes alterarla.");
            return;
        }
        Color colorOriginal = (Color) mesaPresionada.getFill();
        
        actualizarColoresMesas(); 
        
        String idStr = mesaPresionada.getId().replace("mesa", "");
        this.idMesaSeleccionadaActual = Integer.parseInt(idStr);
        this.mesaSeleccionadaActual = mesaPresionada;
        
        cambiarEstadoSeleccionado(mesaPresionada);
        
        if (colorOriginal.equals(COLOR_RESERVADA)) {
            if (btnQuitarReserva != null) btnQuitarReserva.setDisable(false);
            if (btnConfirmar != null) btnConfirmar.setDisable(true);
        } else {
            if (btnConfirmar != null) btnConfirmar.setDisable(false);     
            if (btnQuitarReserva != null) btnQuitarReserva.setDisable(true);
        }
        
        evaluarEstadoBotonesPasarMesa();
    }
    
    private void cambiarEstadoSeleccionado(Circle mesaCirculo) {
        if (mesaCirculo == null) return;
        mesaCirculo.setFill(COLOR_SELECCIONADA);
        mesaCirculo.setStroke(BORDE_SELECCIONADA);
        mesaCirculo.setStrokeWidth(3.0);
    }
    
    private void evaluarEstadoBotonesPasarMesa() {
        if (tblListaEspera == null || btnConfirmar == null || btnPasarMesa == null) return;
        Map<String, Object> clienteSeleccionado = tblListaEspera.getSelectionModel().getSelectedItem();
        
        if (mesaSeleccionadaActual != null && clienteSeleccionado != null && !btnConfirmar.isDisable()) {
            btnPasarMesa.setDisable(false);
        } else {
            btnPasarMesa.setDisable(true);
        }
    }
    
    @FXML
    private void onActualizar(ActionEvent event) {
        if (mesaSeleccionadaActual == null || idMesaSeleccionadaActual == 0) {
            mostrarAlerta("Error", "No hay selección", "Por favor selecciona una mesa primero.");
            return; 
        }

        try {
            sql.actualizarEstadoMesa(idMesaSeleccionadaActual, "Ocupada");

            actualizarColoresMesas();
            resetearSeleccion();
            cargarTablasDesdeBD();

            System.out.println("Mesa " + idMesaSeleccionadaActual + " ocupada correctamente.");
        } catch (Exception e) {
            e.printStackTrace(); 
            mostrarAlerta("Error de BD", "No se pudo actualizar", "Verifica la conexión con Workbench.");
        }
    }
    
    @FXML
    private void quitarReserva(ActionEvent event) {
        if (tblReservaciones == null) return;
        com.mycompany.sistema.models.cliente.Reservacion resSeleccionada = tblReservaciones.getSelectionModel().getSelectedItem();
        
        int idMesaAEliminar = idMesaSeleccionadaActual; 
        String idReservaAEliminar = (resSeleccionada != null) ? resSeleccionada.getFolio() : null;

        if (resSeleccionada == null && (mesaSeleccionadaActual == null || idMesaSeleccionadaActual == 0)) {
            mostrarAlerta("Error", "No hay selección", "Por favor selecciona una reservación de la tabla o una mesa reservada en el mapa.");
            return;
        }
        
        try {
            if (resSeleccionada != null) {
                String mesaStr = resSeleccionada.getMesa().replace("Mesa ", "").trim();
                idMesaAEliminar = Integer.parseInt(mesaStr);
            }
            if (idReservaAEliminar != null) {
                sql.cancelarReservacion(idReservaAEliminar); 
            }
            
            boolean esReservaDeHoy = true;
            if (resSeleccionada != null) {
                if (resSeleccionada.getEstatus().equals("Caducada")) {
                    esReservaDeHoy = false;
                } else {
                    java.time.LocalDate hoy = java.time.LocalDate.now();
                    String fechaReserva = resSeleccionada.getFecha();
                    if (fechaReserva != null && !fechaReserva.equals(hoy.toString())) {
                        esReservaDeHoy = false;
                    }
                }
            }
            
            if (esReservaDeHoy) {
                sql.actualizarEstadoMesa(idMesaAEliminar, "Disponible");
            }
            
            cargarTablasDesdeBD();
            actualizarColoresMesas();
            resetearSeleccion();
            
            if (btnConfirmar != null) btnConfirmar.setDisable(true);
            
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error de BD", "No se pudo liberar la mesa", "Ocurrió un error al conectar con la base de datos.");
        }
    }
    
    @FXML
    private void agregarAListaEspera(ActionEvent event) {
        if (txtNombreEspera == null || txtPersonasEspera == null) return;
        String nombre = txtNombreEspera.getText().trim();
        String personasStr = txtPersonasEspera.getText().trim();
        
        if (nombre.isEmpty() || personasStr.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Falta información", "Escribe el nombre y número de personas para el registro.");
            return;
        }
        
        try {
            int personas = Integer.parseInt(personasStr);
            sql.registrarListaEspera(nombre, personas);
            
            txtNombreEspera.clear();
            txtPersonasEspera.clear();
            
            cargarTablasDesdeBD(); 
            
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "Número inválido", "El campo 'Personas' debe ser un número entero.");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error de BD", "No se pudo registrar", "Error al guardar en la lista de espera.");
        }
    }
    
    @FXML
    private void pasarMesaEspera(ActionEvent event) {
        if (tblListaEspera == null || idMesaSeleccionadaActual == 0) return;
        Map<String, Object> clienteSeleccionado = tblListaEspera.getSelectionModel().getSelectedItem();
        
        if (clienteSeleccionado == null) {
            return;
        }
        
        try {
            int idListaEspera = (int) clienteSeleccionado.get("id_espera"); 
            sql.actualizarEstadoMesa(idMesaSeleccionadaActual, "Ocupada");
            sql.atenderClienteListaEspera(idListaEspera, idMesaSeleccionadaActual);
            
            actualizarColoresMesas();
            resetearSeleccion();
            cargarTablasDesdeBD();
            
            System.out.println("Cliente de espera asignado exitosamente a la mesa " + idMesaSeleccionadaActual);
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo realizar la asignación", "Verifica los logs del servidor.");
        }
    }
    
    private void resetearSeleccion() {
        this.mesaSeleccionadaActual = null;
        this.idMesaSeleccionadaActual = 0;
        if (btnConfirmar != null) btnConfirmar.setDisable(true);
        if (btnQuitarReserva != null) btnQuitarReserva.setDisable(true);
    }
    
    @FXML
    private void actualizarColoresMesas() {
        Map<Integer, String> datos = sql.obtenerEstadosMesas();
        int contadorDisponibles = 0;
        
        for (int i = 1; i <= 12; i++) {
            String estado = datos.get(i);
            Circle circuloActual = obtenerCirculoPorId(i);

            if (circuloActual != null && estado != null) {
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
                        contadorDisponibles++;
                        break;
                }
            }
        }
        
        boolean bloquearListaEspera = (contadorDisponibles > 0);
        if (txtNombreEspera != null) {
            txtNombreEspera.setDisable(bloquearListaEspera);
            txtNombreEspera.setPromptText(bloquearListaEspera ? "¡Hay mesas libres!" : "Nombre del cliente");
        }
        if (txtPersonasEspera != null) {
            txtPersonasEspera.setDisable(bloquearListaEspera);
            txtPersonasEspera.setPromptText(bloquearListaEspera ? "Asigne directo" : "Personas");
        }
        if (btnAgregarLista != null) {
            btnAgregarLista.setDisable(bloquearListaEspera);
        }
    }
    
    private Circle obtenerCirculoPorId(int id) {
        switch (id) {
            case 1: return mesa1; case 2: return mesa2; case 3: return mesa3;
            case 4: return mesa4; case 5: return mesa5; case 6: return mesa6;
            case 7: return mesa7; case 8: return mesa8; case 9: return mesa9;
            case 10: return mesa10; case 11: return mesa11; case 12: return mesa12;
            default: return null;
        }
    }
    
    @FXML
    private void regresarLogin(ActionEvent event) {
        try {
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

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista", "Revisa la consola de NetBeans.");
        }
    }
    
    private void mostrarAlerta(String titulo, String encabezado, String contenido) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(contenido);
        alert.showAndWait(); 
    }
    
    private void verificarDisponibilidadListaEspera(Map<Integer, String> estadosMesas) {
        //cuenta las mesas en estado "Disponible"
        long mesasDisponibles = estadosMesas.values().stream()
                .filter(estado -> estado.equalsIgnoreCase("Disponible"))
                .count();
        //si hay AL MENOS una mesa disponible, la lista de espera DEBE bloquearse
        boolean restauranteTieneLugar = (mesasDisponibles > 0);
        txtNombreEspera.setDisable(restauranteTieneLugar);
        txtPersonasEspera.setDisable(restauranteTieneLugar);
        btnAgregarLista.setDisable(restauranteTieneLugar);

        if (restauranteTieneLugar) {
            txtNombreEspera.setPromptText("Mesas disponibles (" + mesasDisponibles + ")");
            txtPersonasEspera.setPromptText("Asigne mesa directo");
        } else {
            txtNombreEspera.setPromptText("Nombre del cliente");
            txtPersonasEspera.setPromptText("Ej. 4");
        }
    }   
}