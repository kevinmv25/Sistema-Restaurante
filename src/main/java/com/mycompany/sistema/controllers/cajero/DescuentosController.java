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
 * Controla la aplicación de descuentos y cortesías sobre cuentas pendientes.
 *
 * <p>Esta clase implementa el caso de uso <b>CU-10 Aplicar descuentos y
 * cortesías</b>. Permite aplicar descuentos sobre una cuenta completa o sobre
 * un producto específico del pedido, respetando las reglas de autorización
 * cuando el descuento excede el límite permitido para el cajero.</p>
 *
 * <p>La pantalla trabaja únicamente con cuentas en estado <code>Por pagar</code>,
 * ya que una cuenta pagada no debe modificarse desde este flujo.</p>
 *
 * @author Gutierrez Colorado Oliver
 * @see CajeroService
 * @see Cuenta
 * @see DetallePedido
 */
public class DescuentosController implements Initializable {

    @FXML private TableView<Cuenta> tablaCuentas;
    @FXML private TableColumn<Cuenta, String> colCuenta, colPedido, colSubtotal, colImpuestos, colDescuento, colTotal, colEstado;

    @FXML private TableView<DetallePedido> tablaProductos;
    @FXML private TableColumn<DetallePedido, String> colProducto, colCantidad, colPrecio;

    @FXML private ComboBox<String> comboAplicarA;
    @FXML private ComboBox<String> comboTipoDescuento;
    @FXML private TextField txtValorDescuento;
    @FXML private TextArea txtMotivo;

    private final CajeroService cajeroService = new CajeroService();

    private Cuenta cuentaSeleccionada;

    /**
    * Inicializa combos, tablas y datos necesarios para aplicar descuentos.
    *
    * @param url ubicación usada por JavaFX para resolver recursos.
    * @param rb recursos de internacionalización, si existieran.
    */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarCombos();
        configurarTablas();
        cargarCuentas();
        configurarSeleccionCuenta();
    }

    /**
    * Configura las opciones disponibles para el alcance y tipo de descuento.
    *
    * <p>El descuento puede aplicarse a la cuenta completa o a un producto
    * seleccionado. También puede calcularse por porcentaje o como monto fijo.</p>
    */
    private void configurarCombos() {
        comboAplicarA.getItems().addAll("Cuenta completa", "Producto seleccionado");
        comboAplicarA.setValue("Cuenta completa");

        comboTipoDescuento.getItems().addAll("Porcentaje", "Monto fijo");
        comboTipoDescuento.setValue("Porcentaje");
    }

    /**
    * Asocia las columnas de las tablas con los atributos de cuenta y detalle.
    *
    * <p>La tabla superior muestra las cuentas pendientes y la tabla inferior
    * muestra los productos relacionados con la cuenta seleccionada.</p>
    */
    private void configurarTablas() {
        colCuenta.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getIdCuenta())));

        colPedido.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getIdPedido())));

        colSubtotal.setCellValueFactory(data ->
                new SimpleStringProperty("$" + String.format("%.2f", data.getValue().getSubtotal())));

        colImpuestos.setCellValueFactory(data ->
                new SimpleStringProperty("$" + String.format("%.2f", data.getValue().getImpuestos())));

        colDescuento.setCellValueFactory(data ->
                new SimpleStringProperty("$" + String.format("%.2f", data.getValue().getDescuento())));

        colTotal.setCellValueFactory(data ->
                new SimpleStringProperty("$" + String.format("%.2f", data.getValue().getTotal())));

        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEstado()));

        colProducto.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreProducto()));

        colCantidad.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getCantidad())));

        colPrecio.setCellValueFactory(data ->
                new SimpleStringProperty("$" + String.format("%.2f", data.getValue().getSubtotal())));
    }

    /**
    * Carga las cuentas pendientes de pago desde la base de datos.
    *
    * <p>Después de aplicar un descuento, este método se vuelve a ejecutar para
    * reflejar el nuevo total de la cuenta.</p>
    */
    private void cargarCuentas() {
        ObservableList<Cuenta> cuentas =
                FXCollections.observableArrayList(cajeroService.obtenerCuentasPorPagar());

        tablaCuentas.setItems(cuentas);

        if (!cuentas.isEmpty()) {
            tablaCuentas.getSelectionModel().selectFirst();
        }
    }

    /**
    * Detecta la cuenta seleccionada y carga sus productos relacionados.
    *
    * <p>También actualiza el contexto temporal de cajero para que otras pantallas
    * puedan reutilizar la cuenta y el pedido seleccionados.</p>
    */
    private void configurarSeleccionCuenta() {
        tablaCuentas.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            cuentaSeleccionada = newValue;

            if (cuentaSeleccionada != null) {
                cargarProductosCuenta(cuentaSeleccionada.getIdPedido());

                ContextoCajero.setIdCuentaActual(cuentaSeleccionada.getIdCuenta());
                ContextoCajero.setIdPedidoActual(cuentaSeleccionada.getIdPedido());
            }
        });
    }

    /**
    * Carga los productos asociados al pedido de la cuenta seleccionada.
    *
    * @param idPedido identificador del pedido del cual se consultará el detalle.
    */
    private void cargarProductosCuenta(int idPedido) {
        ObservableList<DetallePedido> detalles =
                FXCollections.observableArrayList(cajeroService.obtenerDetallePedido(idPedido));

        tablaProductos.setItems(detalles);
    }

    /**
    * Aplica el descuento capturado por el cajero.
    *
    * <p>Valida campos obligatorios, formato numérico, selección de producto cuando
    * aplica, y autorización de gerente si el descuento supera el límite permitido.
    * Para esta versión, el PIN de autorización usado en la validación es
    * <code>1234</code>.</p>
    */
    @FXML
    private void handleAplicarDescuento() {
        if (cuentaSeleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin cuenta", "Selecciona una cuenta pendiente.");
            return;
        }

        String aplicarA = comboAplicarA.getValue();
        String tipo = comboTipoDescuento.getValue();
        String valorTexto = txtValorDescuento.getText().trim();
        String motivo = txtMotivo.getText().trim();

        if (aplicarA == null || tipo == null || valorTexto.isEmpty() || motivo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos incompletos", "Completa todos los campos.");
            return;
        }

        double valor;

        try {
            valor = Double.parseDouble(valorTexto);
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Valor inválido", "El valor del descuento debe ser numérico.");
            return;
        }

        Integer idProducto = null;

        if (aplicarA.equals("Producto seleccionado")) {
            DetallePedido detalle = tablaProductos.getSelectionModel().getSelectedItem();

            if (detalle == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Sin producto", "Selecciona un producto de la tabla.");
                return;
            }

            idProducto = detalle.getIdProducto();
        }

        boolean requiereAutorizacion = false;

        if (tipo.equals("Porcentaje") && valor > 30) {
            requiereAutorizacion = true;
        }

        if (tipo.equals("Monto fijo") && valor > 100) {
            requiereAutorizacion = true;
        }

        if (requiereAutorizacion) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Autorización requerida");
            dialog.setHeaderText("El descuento supera el límite permitido.");
            dialog.setContentText("Ingresa PIN de gerente:");

            Optional<String> resultado = dialog.showAndWait();

            if (resultado.isEmpty() || !resultado.get().equals("1234")) {
                mostrarAlerta(Alert.AlertType.ERROR, "No autorizado", "PIN incorrecto. No se aplicó el descuento.");
                return;
            }
        }

        boolean aplicado = cajeroService.aplicarDescuento(
                cuentaSeleccionada.getIdCuenta(),
                idProducto,
                tipo,
                valor,
                motivo,
                requiereAutorizacion
        );

        if (!aplicado) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo aplicar el descuento.");
            return;
        }

        mostrarAlerta(Alert.AlertType.INFORMATION, "Descuento aplicado", "La cuenta fue actualizada correctamente.");

        txtValorDescuento.clear();
        txtMotivo.clear();

        cargarCuentas();
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
    * @param mensaje mensaje que describe el resultado de la operación.
    */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}