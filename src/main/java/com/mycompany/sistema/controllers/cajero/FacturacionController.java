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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarCombos();
        configurarTabla();
        cargarPedidoActual();
    }

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