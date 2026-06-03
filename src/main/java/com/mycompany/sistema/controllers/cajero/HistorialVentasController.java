package com.mycompany.sistema.controllers.cajero;

import com.mycompany.sistema.models.cajero.*;
import com.mycompany.sistema.services.*;

import java.net.URL;
import java.util.*;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;

/**
 * Controla la consulta del historial de ventas y movimientos de caja.
 *
 * <p>Esta clase implementa el caso de uso <b>CU-12 Consultar historial de ventas
 * y movimientos de caja</b>. Permite revisar pagos, cancelaciones, entradas y
 * reembolsos registrados en caja sin modificar la información almacenada.</p>
 *
 * <p>Su propósito principal es servir como apoyo para auditoría rápida del turno
 * y como pantalla de verificación para los movimientos generados por pagos y
 * reembolsos.</p>
 *
 * @author Gutierrez Colorado Oliver
 * @see CajeroService
 * @see HistorialVenta
 */
public class HistorialVentasController implements Initializable {

    @FXML private ComboBox<String> comboFiltro;
    @FXML private TableView<HistorialVenta> tablaHistorial;

    @FXML private TableColumn<HistorialVenta, String> colMovimiento;
    @FXML private TableColumn<HistorialVenta, String> colTurno;
    @FXML private TableColumn<HistorialVenta, String> colPago;
    @FXML private TableColumn<HistorialVenta, String> colCuenta;
    @FXML private TableColumn<HistorialVenta, String> colTipo;
    @FXML private TableColumn<HistorialVenta, String> colMetodo;
    @FXML private TableColumn<HistorialVenta, String> colMonto;
    @FXML private TableColumn<HistorialVenta, String> colEstado;
    @FXML private TableColumn<HistorialVenta, String> colFecha;

    @FXML private TextArea txtDetalle;

    private final CajeroService cajeroService = new CajeroService();

    /**
    * Inicializa filtros, columnas, datos y selección de movimientos.
    *
    * @param url ubicación usada por JavaFX para resolver recursos.
    * @param rb recursos de internacionalización, si existieran.
    */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarCombo();
        configurarTabla();
        cargarHistorial();
        configurarSeleccion();
    }

    /**
    * Configura los filtros disponibles para consultar movimientos de caja.
    */
    private void configurarCombo() {
        comboFiltro.getItems().addAll(
                "Todos",
                "Pago",
                "Cancelación",
                "Entrada",
                "Reembolso"
        );

        comboFiltro.setValue("Todos");
    }

    /**
    * Asocia cada columna de la tabla con los datos del historial.
    */
    private void configurarTabla() {
        colMovimiento.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getIdMovimiento())));

        colTurno.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getIdTurno())));

        colPago.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getIdPago())));

        colCuenta.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getIdCuenta())));

        colTipo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTipo()));

        colMetodo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getMetodoPago()));

        colMonto.setCellValueFactory(data ->
                new SimpleStringProperty("$" + String.format("%.2f", data.getValue().getMonto())));

        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEstadoPago()));

        colFecha.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFecha()));
    }

    /**
    * Configura la selección de movimientos en la tabla.
    *
    * <p>Cuando el usuario selecciona un movimiento, se muestra su detalle completo
    * en el área de texto inferior.</p>
    */
    private void configurarSeleccion() {
        tablaHistorial.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> {
                    if (newValue != null) {
                        mostrarDetalle(newValue);
                    }
                });
    }

    /**
    * Carga los movimientos de caja de acuerdo con el filtro seleccionado.
    *
    * <p>Si no existen movimientos, se muestra un mensaje simple en el área de
    * detalle.</p>
    */
    private void cargarHistorial() {
        String filtro = comboFiltro.getValue();

        ObservableList<HistorialVenta> lista =
                FXCollections.observableArrayList(
                        cajeroService.obtenerHistorialMovimientos(filtro)
                );

        tablaHistorial.setItems(lista);

        if (lista.isEmpty()) {
            txtDetalle.setText("No hay movimientos registrados.");
        } else {
            tablaHistorial.getSelectionModel().selectFirst();
        }
    }

    /**
    * Muestra el detalle legible de un movimiento seleccionado.
    *
    * @param h movimiento de historial que será presentado al usuario.
    */
    private void mostrarDetalle(HistorialVenta h) {
        txtDetalle.setText(
                "Movimiento: #" + h.getIdMovimiento() + "\n" +
                "Turno: #" + h.getIdTurno() + "\n" +
                "Pago relacionado: #" + h.getIdPago() + "\n" +
                "Cuenta relacionada: #" + h.getIdCuenta() + "\n" +
                "Tipo: " + h.getTipo() + "\n" +
                "Método de pago: " + h.getMetodoPago() + "\n" +
                "Estado del pago: " + h.getEstadoPago() + "\n" +
                "Monto: $" + String.format("%.2f", h.getMonto()) + "\n" +
                "Fecha: " + h.getFecha() + "\n" +
                "Descripción: " + h.getDescripcion()
        );
    }

    /**
    * Aplica el filtro seleccionado en el combo.
    */
    @FXML
    private void filtrarMovimientos() {
        cargarHistorial();
    }

    /**
    * Recarga manualmente el historial de movimientos.
    */
    @FXML
    private void actualizarHistorial() {
        cargarHistorial();
    }

    /**
    * Regresa al menú principal del módulo de cajero.
    *
    * @param event evento generado por el botón de regreso.
    */
    @FXML
    private void volverMenuCajero(ActionEvent event) {
        SceneService.cambiarEscena(event, "/scenes/cajero/menu-cajero.fxml");
    }
}