package com.mycompany.sistema.controllers.cajero;

import com.mycompany.sistema.models.cajero.Cuenta;
import com.mycompany.sistema.models.cajero.DetallePedido;
import com.mycompany.sistema.models.cajero.Pedido;
import com.mycompany.sistema.services.CajeroService;
import com.mycompany.sistema.services.ContextoCajero;
import com.mycompany.sistema.services.SceneService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

/**
 * Controla la generación de cuentas, tickets y facturas.
 *
 * <p>Esta clase implementa el caso de uso <b>CU-05 Generar cuenta, ticket y
 * factura</b>. Recupera pedidos pendientes, muestra su detalle y genera una
 * cuenta en estado <code>Por pagar</code>, lista para recibir descuentos o ser
 * cobrada en el módulo de pagos.</p>
 *
 * <p>La cuenta generada mantiene trazabilidad con el pedido original, por lo que
 * los casos de uso posteriores pueden continuar el flujo sin duplicar datos.</p>
 *
 * @author Gutierrez Colorado Oliver
 * @see CajeroService
 * @see Pedido
 * @see Cuenta
 * @see DetallePedido
 */
public class FacturacionController implements Initializable {

    @FXML
    private TableView<DetallePedido> tablaPedido;

    @FXML
    private TableColumn<DetallePedido, String> colProducto;

    @FXML
    private TableColumn<DetallePedido, String> colCantidad;

    @FXML
    private TableColumn<DetallePedido, String> colPrecio;

    @FXML
    private ComboBox<String> comboTipoDocumento;

    @FXML
    private ComboBox<String> comboFormato;

    @FXML
    private RadioButton rbIncluirIVA;

    @FXML
    private RadioButton rbDesglosarIVA;

    @FXML
    private TextArea txtVistaPrevia;

    private final CajeroService cajeroService = new CajeroService();

    private Pedido pedidoActual;
    private Cuenta cuentaActual;

    /**
    * Inicializa la pantalla de facturación.
    *
    * <p>Configura los combos, prepara la tabla de productos y carga el pedido
    * pendiente que será facturado.</p>
    *
    * @param url ubicación usada por JavaFX para resolver recursos.
    * @param rb recursos de internacionalización, si existieran.
    */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarCombos();
        configurarTabla();
        cargarPedidoActual();
    }

    /**
    * Configura las opciones de documento y formato disponibles.
    *
    * <p>El usuario puede generar un ticket de venta o factura legal en distintos
    * formatos de salida.</p>
    */
    private void configurarCombos() {
        comboTipoDocumento.getItems().addAll(
                "Ticket de venta",
                "Factura legal"
        );

        comboFormato.getItems().addAll(
                "Impresión térmica",
                "PDF",
                "Envío por correo"
        );

        comboTipoDocumento.setValue("Ticket de venta");
        comboFormato.setValue("Impresión térmica");
    }

    /**
    * Asocia las columnas de la tabla con los datos del detalle del pedido.
    */
    private void configurarTabla() {
        colProducto.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreProducto())
        );

        colCantidad.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getCantidad()))
        );

        colPrecio.setCellValueFactory(data ->
                new SimpleStringProperty("$" + String.format("%.2f", data.getValue().getSubtotal()))
        );
    }

    /**
    * Recupera el pedido actual que será facturado.
    *
    * <p>Primero intenta usar el pedido guardado en <code>ContextoCajero</code>.
    * Si no existe, toma el primer pedido en estado <code>Por pagar</code>.</p>
    */
    private void cargarPedidoActual() {
        Integer idPedidoContexto = ContextoCajero.getIdPedidoActual();

        if (idPedidoContexto != null) {
            pedidoActual = cajeroService.buscarPedidoPorId(idPedidoContexto);
        }

        if (pedidoActual == null) {
            List<Pedido> pedidos = cajeroService.obtenerPedidosPorPagar();

            if (!pedidos.isEmpty()) {
                pedidoActual = pedidos.get(0);
                ContextoCajero.setIdPedidoActual(pedidoActual.getIdPedido());
                ContextoCajero.setIdMesaActual(pedidoActual.getIdMesa());
            }
        }

        if (pedidoActual == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Sin pedidos",
                    "No hay pedidos en estado 'Por pagar'."
            );
            txtVistaPrevia.setText("No hay pedidos disponibles para facturar.");
            return;
        }

        List<DetallePedido> detalles =
                cajeroService.obtenerDetallePedido(pedidoActual.getIdPedido());

        tablaPedido.setItems(FXCollections.observableArrayList(detalles));

        double subtotal = cajeroService.calcularSubtotalPedido(pedidoActual.getIdPedido());

        txtVistaPrevia.setText(
                "Pedido: #" + pedidoActual.getIdPedido() + "\n" +
                "Mesa: " + pedidoActual.getIdMesa() + "\n" +
                "Estado: " + pedidoActual.getEstado() + "\n" +
                "Subtotal: $" + String.format("%.2f", subtotal) + "\n\n" +
                "Seleccione tipo de documento, impuestos y formato."
        );
    }

    /**
    * Genera la cuenta y el documento de consumo seleccionado.
    *
    * <p>Calcula impuestos cuando el usuario lo solicita, crea el registro en la
    * tabla <code>cuentas</code> y actualiza la vista previa con el total final.</p>
    */
    @FXML
    private void handleGenerarDocumento() {
        if (pedidoActual == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Sin pedido",
                    "No hay un pedido seleccionado para generar la cuenta."
            );
            return;
        }

        String tipoDocumento = comboTipoDocumento.getValue();
        String formato = comboFormato.getValue();

        if (tipoDocumento == null || formato == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Datos incompletos",
                    "Selecciona el tipo de documento y el formato."
            );
            return;
        }

        boolean aplicarIVA = rbIncluirIVA.isSelected() || rbDesglosarIVA.isSelected();

        cuentaActual = cajeroService.generarCuenta(
                pedidoActual.getIdPedido(),
                tipoDocumento,
                formato,
                aplicarIVA
        );

        if (cuentaActual == null) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error al generar cuenta",
                    "No se pudo generar la cuenta del pedido."
            );
            return;
        }

        ContextoCajero.setIdCuentaActual(cuentaActual.getIdCuenta());
        ContextoCajero.setEstadoCuentaActual(cuentaActual.getEstado());

        String desgloseIVA;

        if (rbDesglosarIVA.isSelected()) {
            desgloseIVA =
                    "Subtotal: $" + String.format("%.2f", cuentaActual.getSubtotal()) + "\n" +
                    "IVA: $" + String.format("%.2f", cuentaActual.getImpuestos()) + "\n";
        } else if (rbIncluirIVA.isSelected()) {
            desgloseIVA =
                    "IVA incluido en el total.\n";
        } else {
            desgloseIVA =
                    "Sin impuestos aplicados.\n";
        }

        txtVistaPrevia.setText(
                "DOCUMENTO GENERADO\n\n" +
                "Cuenta: #" + cuentaActual.getIdCuenta() + "\n" +
                "Pedido: #" + cuentaActual.getIdPedido() + "\n" +
                "Mesa: " + pedidoActual.getIdMesa() + "\n" +
                "Tipo documento: " + cuentaActual.getTipoDocumento() + "\n" +
                "Formato: " + cuentaActual.getFormato() + "\n\n" +
                desgloseIVA +
                "Descuento: $" + String.format("%.2f", cuentaActual.getDescuento()) + "\n" +
                "Total a pagar: $" + String.format("%.2f", cuentaActual.getTotal()) + "\n" +
                "Estado: " + cuentaActual.getEstado() + "\n\n" +
                "Documento generado exitosamente."
        );

        mostrarAlerta(
                Alert.AlertType.INFORMATION,
                "Documento generado",
                "Documento generado exitosamente."
        );
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
    * Muestra una alerta en pantalla.
    *
    * @param tipo tipo de alerta.
    * @param titulo título de la ventana.
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