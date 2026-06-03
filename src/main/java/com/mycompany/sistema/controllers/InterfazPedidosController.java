/*
package com.mycompany.sistema.controllers;

import com.mycompany.sistema.models.cajero.DetallePedido;
import com.mycompany.sistema.models.Producto;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

public class InterfazPedidosController {

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private FlowPane flowPanePedidos;

    private List<DetallePedido> pedidoActual = new ArrayList<>();

    // DATOS DE CONEXIÓN (Cámbialos por los tuyos)
    private final String URL = "jdbc:mysql://localhost:3306/sistema_restaurante";
    private final String USER = "admin_rest";
    private final String PASS = "rest123";

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
    }

    // --- MÉTODOS DE CATEGORÍA (Llaman al motor cargarTabla) ---
    @FXML public void btnPlatillosClick(ActionEvent event) { cargarTabla("Platillo"); }
    @FXML public void btnLicorClick(ActionEvent event)   { cargarTabla("Licor"); }
    @FXML public void btnPostresClick(ActionEvent event)   { cargarTabla("Postre"); }
    @FXML public void btnBebidasClick(ActionEvent event)   { cargarTabla("Bebida"); }
    @FXML public void btnEntradasClick(ActionEvent event)  {cargarTabla("Entrada"); }

    // --- MOTOR DE CARGA DESDE BD ---
    private void cargarTabla(String categoria) {
    System.out.println("Cargando " + categoria + " desde BD...");

    ObservableList<Producto> lista = FXCollections.observableArrayList();

    String sql = """
    SELECT p.id_producto, p.nombre, p.precio
    FROM productos p
    INNER JOIN categorias c ON p.id_categoria = c.id_categoria
    WHERE c.nombre = ?
""";

    try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, categoria);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            lista.add(new Producto(
            rs.getInt("id_Producto"),
            rs.getString("nombre"),
            rs.getDouble("precio")
            ));
        }

        tablaProductos.setItems(lista);
        
        System.out.println("Productos cargados: " + lista.size());

    } catch (SQLException e) {
        System.out.println("Error de BD: " + e.getMessage());
    }
}
    // --- BOTÓN AGREGAR (Unificado y limpio) ---
    @FXML
public void agregarAlPedido(ActionEvent event) {
    Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();

    if (seleccionado != null) {
        System.out.println("Producto agregado: " + seleccionado.getNombre());

        String solicitud = txtSolicitudEspecial.getText().trim();

        DetallePedido detalle = new DetallePedido(seleccionado, solicitud);

        pedidoActual.add(detalle);

       Button btn = new Button(
        seleccionado.getNombre() + " - $" + seleccionado.getPrecio()
        + (solicitud.isEmpty() ? "" : " | Nota: " + solicitud)
        );

        flowPanePedidos.getChildren().add(btn);
        txtSolicitudEspecial.clear();

        System.out.println("Productos en pedido: " + pedidoActual.size());
        System.out.println("Elementos en FlowPane: " + flowPanePedidos.getChildren().size());
    } else {
        System.out.println("No seleccionaste ningún producto.");
    }
}

    // --- BOTÓN GUARDAR ---
    @FXML
public void btnConfirmarClick(ActionEvent event) {
    if (pedidoActual.isEmpty()) {
        System.out.println("No hay productos en el pedido.");
        return;
    }

    guardarPedidoEnBD();

    pedidoActual.clear();
    flowPanePedidos.getChildren().clear();

    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.close();
}
private void guardarPedidoEnBD() {
    String sqlPedido = "INSERT INTO pedidos (id_mesa, estado) VALUES (?, ?)";
    String sqlDetalle = """
    INSERT INTO detalle_pedido
    (id_pedido, id_producto, cantidad, precio_unitario, solicitud_especial)
    VALUES (?, ?, ?, ?, ?)
""";

    try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {

        conn.setAutoCommit(false);

        int idMesa = 1; // Por ahora puedes dejarlo fijo o cambiarlo según tu interfaz

        PreparedStatement psPedido = conn.prepareStatement(
            sqlPedido,
            Statement.RETURN_GENERATED_KEYS
        );

        psPedido.setInt(1, idMesa);
        psPedido.setString(2, "Pendiente");
        psPedido.executeUpdate();

        ResultSet rsKeys = psPedido.getGeneratedKeys();

        if (!rsKeys.next()) {
            throw new SQLException("No se pudo obtener el ID del pedido.");
        }

        int idPedido = rsKeys.getInt(1);

        PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);

        for (DetallePedido detalle : pedidoActual) {
            Producto producto = detalle.getProducto();
            System.out.println("Guardando producto ID: " + producto.getIdProducto()
        + " - " + producto.getNombre());
            psDetalle.setInt(1, idPedido);
            psDetalle.setInt(2, producto.getIdProducto());
            psDetalle.setInt(3, 1);
            psDetalle.setDouble(4, producto.getPrecio());
            psDetalle.setString(5, detalle.getSolicitudEspecial());
            psDetalle.addBatch();
        }

        psDetalle.executeBatch();

        conn.commit();

        System.out.println("Pedido guardado correctamente. ID pedido: " + idPedido);

    } catch (SQLException e) {
        System.out.println("Error al guardar pedido: " + e.getMessage());
        e.printStackTrace();
    }
}
    @FXML
    private TextArea txtSolicitudEspecial;
}
*/