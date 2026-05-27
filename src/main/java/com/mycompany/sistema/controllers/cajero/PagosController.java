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

public class PagosController implements Initializable {

    @FXML private TableView<Cuenta> tablaCuentasPendientes;

    @FXML private TableColumn<Cuenta, String> colCuenta;
    @FXML private TableColumn<Cuenta, String> colPedido;
    @FXML private TableColumn<Cuenta, String> colSubtotal;
    @FXML private TableColumn<Cuenta, String> colImpuestos;
    @FXML private TableColumn<Cuenta, String> colDescuento;
    @FXML private TableColumn<Cuenta, String> colTotal;
    @FXML private TableColumn<Cuenta, String> colEstado;

    @FXML private TextField txtSaldoPendiente;
    @FXML private TextField txtMonto;
    @FXML private ComboBox<String> comboMetodoPago;

    private final CajeroService cajeroService = new CajeroService();

    private Cuenta cuentaSeleccionada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarCombo();
        configurarTabla();
        cargarCuentasPendientes();
        configurarSeleccionTabla();
    }

    private void configurarCombo() {
        comboMetodoPago.getItems().addAll(
                "Efectivo",
                "Tarjeta",
                "Pago mixto"
        );

        comboMetodoPago.setValue("Efectivo");
    }

    private void configurarTabla() {
        colCuenta.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getIdCuenta()))
        );

        colPedido.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getIdPedido()))
        );

        colSubtotal.setCellValueFactory(data ->
                new SimpleStringProperty("$" + String.format("%.2f", data.getValue().getSubtotal()))
        );

        colImpuestos.setCellValueFactory(data ->
                new SimpleStringProperty("$" + String.format("%.2f", data.getValue().getImpuestos()))
        );

        colDescuento.setCellValueFactory(data ->
                new SimpleStringProperty("$" + String.format("%.2f", data.getValue().getDescuento()))
        );

        colTotal.setCellValueFactory(data ->
                new SimpleStringProperty("$" + String.format("%.2f", data.getValue().getTotal()))
        );

        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEstado())
        );
    }

    private void configurarSeleccionTabla() {
        tablaCuentasPendientes.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> {
                    cuentaSeleccionada = newValue;

                    if (cuentaSeleccionada != null) {
                        txtSaldoPendiente.setText(
                                String.format("%.2f", cuentaSeleccionada.getTotal())
                        );

                        txtMonto.setText(
                                String.format("%.2f", cuentaSeleccionada.getTotal())
                        );

                        ContextoCajero.setIdCuentaActual(cuentaSeleccionada.getIdCuenta());
                        ContextoCajero.setIdPedidoActual(cuentaSeleccionada.getIdPedido());
                        ContextoCajero.setEstadoCuentaActual(cuentaSeleccionada.getEstado());
                    }
                });
    }

    private void cargarCuentasPendientes() {
        List<Cuenta> cuentas = cajeroService.obtenerCuentasPorPagar();

        ObservableList<Cuenta> lista = FXCollections.observableArrayList(cuentas);
        tablaCuentasPendientes.setItems(lista);

        if (!lista.isEmpty()) {
            tablaCuentasPendientes.getSelectionModel().selectFirst();
        } else {
            cuentaSeleccionada = null;
            txtSaldoPendiente.setText("Sin cuentas pendientes");
            txtMonto.clear();
        }
    }

    @FXML
    private void handleProcesarPago() {
        if (cuentaSeleccionada == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Sin cuenta seleccionada",
                    "Selecciona una cuenta pendiente de la tabla."
            );
            return;
        }

        Integer turnoAbierto = cajeroService.obtenerTurnoAbierto();

        if (turnoAbierto == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Caja cerrada",
                    "No hay una caja abierta. Primero abre caja en Gestión de Caja."
            );
            return;
        }

        String metodoPago = comboMetodoPago.getValue();
        String montoTexto = txtMonto.getText().trim();

        if (metodoPago == null || montoTexto.isEmpty()) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Datos incompletos",
                    "Selecciona el método de pago e ingresa el monto recibido."
            );
            return;
        }

        double montoRecibido;

        try {
            montoRecibido = Double.parseDouble(montoTexto);
        } catch (NumberFormatException e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Monto inválido",
                    "El monto debe ser un número válido."
            );
            return;
        }

        double totalCuenta = cuentaSeleccionada.getTotal();

        if (montoRecibido < totalCuenta) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Monto insuficiente",
                    "El monto recibido no cubre el total de la cuenta."
            );
            return;
        }

        if (metodoPago.equals("Pago mixto")) {
            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Pago mixto",
                    "El pago mixto se registrará como un solo movimiento por el total de la cuenta."
            );
        }

        Pago pago = cajeroService.registrarPago(
                cuentaSeleccionada.getIdCuenta(),
                metodoPago,
                totalCuenta
        );

        if (pago == null) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo registrar el pago."
            );
            return;
        }

        ContextoCajero.setIdPagoActual(pago.getIdPago());
        ContextoCajero.setEstadoCuentaActual("Pagada");

        double cambio = montoRecibido - totalCuenta;

        String mensaje =
                "Pago registrado correctamente.\n\n" +
                "Pago: #" + pago.getIdPago() + "\n" +
                "Cuenta: #" + pago.getIdCuenta() + "\n" +
                "Método: " + pago.getMetodoPago() + "\n" +
                "Total pagado: $" + String.format("%.2f", pago.getMonto()) + "\n";

        if (metodoPago.equals("Efectivo")) {
            mensaje += "Cambio: $" + String.format("%.2f", cambio) + "\n";
        }

        mostrarAlerta(
                Alert.AlertType.INFORMATION,
                "Pago registrado",
                mensaje
        );

        cargarCuentasPendientes();
    }

    @FXML
    private void volverMenuCajero(ActionEvent event) {
        SceneService.cambiarEscena(event, "/scenes/cajero/menu-cajero.fxml");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}