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

public class ReembolsosController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TextArea txtMotivo;

    @FXML private TableView<Pago> tablaPagos;
    @FXML private TableColumn<Pago, String> colPago, colCuenta, colMetodo, colMonto, colEstado, colFecha;

    private final CajeroService cajeroService = new CajeroService();

    private Pago pagoSeleccionado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarPagos();
        configurarSeleccionTabla();
    }

    private void configurarTabla() {
        colPago.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getIdPago())));

        colCuenta.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getIdCuenta())));

        colMetodo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getMetodoPago()));

        colMonto.setCellValueFactory(data ->
                new SimpleStringProperty("$" + String.format("%.2f", data.getValue().getMonto())));

        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEstado()));

        colFecha.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFechaPago()));
    }

    private void configurarSeleccionTabla() {
        tablaPagos.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            pagoSeleccionado = newValue;

            if (pagoSeleccionado != null) {
                txtBuscar.setText(String.valueOf(pagoSeleccionado.getIdPago()));
                ContextoCajero.setIdPagoActual(pagoSeleccionado.getIdPago());
                ContextoCajero.setIdCuentaActual(pagoSeleccionado.getIdCuenta());
            }
        });
    }

    private void cargarPagos() {
        ObservableList<Pago> pagos =
                FXCollections.observableArrayList(cajeroService.obtenerPagosDelTurnoActual());

        tablaPagos.setItems(pagos);

        if (!pagos.isEmpty()) {
            tablaPagos.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleBuscarTicket() {
        String texto = txtBuscar.getText().trim();

        if (texto.isEmpty()) {
            cargarPagos();
            return;
        }

        int idPago;

        try {
            idPago = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Dato inválido", "El ID del pago debe ser numérico.");
            return;
        }

        Pago pago = cajeroService.buscarPagoPorId(idPago);

        if (pago == null || !pago.getEstado().equalsIgnoreCase("Pagado")) {
            mostrarAlerta(Alert.AlertType.WARNING, "No encontrado", "No se encontró un pago válido con ese ID.");
            return;
        }

        Integer turnoAbierto = cajeroService.obtenerTurnoAbierto();

        if (turnoAbierto == null || pago.getIdTurno() != turnoAbierto) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Ticket de otro turno",
                    "No se pueden cancelar operaciones de turnos anteriores."
            );
            return;
        }

        tablaPagos.setItems(FXCollections.observableArrayList(pago));
        tablaPagos.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleCancelarPago() {
        if (pagoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin pago", "Selecciona un pago para cancelar.");
            return;
        }

        String motivo = txtMotivo.getText().trim();

        if (motivo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Motivo requerido", "Ingresa el motivo de la cancelación.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cancelación");
        confirmacion.setHeaderText("¿Cancelar el pago #" + pagoSeleccionado.getIdPago() + "?");
        confirmacion.setContentText("La cuenta volverá a estado Por pagar y la mesa quedará ocupada.");

        Optional<ButtonType> respuesta = confirmacion.showAndWait();

        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }

        boolean cancelado = cajeroService.cancelarPago(
                pagoSeleccionado.getIdPago(),
                motivo
        );

        if (!cancelado) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cancelar el pago.");
            return;
        }

        mostrarAlerta(
                Alert.AlertType.INFORMATION,
                "Pago cancelado",
                "El pago fue cancelado correctamente.\nLa cuenta volvió a estado Por pagar."
        );

        txtMotivo.clear();
        txtBuscar.clear();
        cargarPagos();
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