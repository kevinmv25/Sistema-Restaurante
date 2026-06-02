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
 * Controla la cancelación de pagos y emisión de reembolsos.
 *
 * <p>Esta clase implementa el caso de uso <b>CU-11 Cancelar pago y emitir
 * reembolso</b>. Permite consultar los pagos del turno actual y cancelar uno de
 * ellos siempre que pertenezca a la caja abierta.</p>
 *
 * <p>Cuando un pago se cancela, la cuenta vuelve a estado
 * <code>Por pagar</code>, el pedido se marca nuevamente como pendiente, la mesa
 * queda ocupada y se registra un movimiento de cancelación en caja.</p>
 *
 * @author Gutierrez Colorado Oliver
 * @see CajeroService
 * @see Pago
 */
public class ReembolsosController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TextArea txtMotivo;

    @FXML private TableView<Pago> tablaPagos;
    @FXML private TableColumn<Pago, String> colPago, colCuenta, colMetodo, colMonto, colEstado, colFecha;

    private final CajeroService cajeroService = new CajeroService();

    private Pago pagoSeleccionado;

    /**
    * Inicializa la pantalla de reembolsos.
    *
    * <p>Configura la tabla, carga los pagos del turno actual y prepara la selección
    * de registros.</p>
    *
    * @param url ubicación usada por JavaFX para resolver recursos.
    * @param rb recursos de internacionalización, si existieran.
    */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarPagos();
        configurarSeleccionTabla();
    }

    /**
    * Asocia las columnas de la tabla con los datos de cada pago.
    */
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

    /**
    * Configura la selección de pagos en la tabla.
    *
    * <p>Al seleccionar un pago, se actualiza el campo de búsqueda y el contexto
    * temporal del cajero.</p>
    */
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

    /**
    * Carga los pagos registrados en el turno de caja actualmente abierto.
    */
    private void cargarPagos() {
        ObservableList<Pago> pagos =
                FXCollections.observableArrayList(cajeroService.obtenerPagosDelTurnoActual());

        tablaPagos.setItems(pagos);

        if (!pagos.isEmpty()) {
            tablaPagos.getSelectionModel().selectFirst();
        }
    }

    /**
    * Busca un pago por su identificador.
    *
    * <p>El método valida que el pago exista, que siga en estado
    * <code>Pagado</code> y que pertenezca al turno de caja abierto. Si el pago
    * corresponde a otro turno, se bloquea la operación.</p>
    */
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

    /**
    * Cancela el pago seleccionado y registra el comprobante de cancelación.
    *
    * <p>Solicita un motivo obligatorio y confirma la operación con el usuario antes
    * de modificar los datos. Esta validación evita cancelaciones accidentales.</p>
    */
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

    /**
    * Regresa al menú principal del módulo de cajero.
    *
    * @param event evento generado por el botón de regreso.
    */
    @FXML
    private void volverMenuCajero(ActionEvent event) {
        SceneService.cambiarEscena(event, "/scenes/cajero/menu-cajero.fxml");
    }

    /**
    * Muestra una alerta al usuario.
    *
    * @param tipo tipo de alerta.
    * @param titulo título de la alerta.
    * @param mensaje mensaje mostrado al usuario.
    */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}