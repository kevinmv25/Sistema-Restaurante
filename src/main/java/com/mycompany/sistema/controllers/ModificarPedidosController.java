package com.mycompany.sistema.controllers;

import com.mycompany.sistema.models.Pedido;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lib.SqlLib;

public class ModificarPedidosController implements Initializable {

    private static final String URL_BD = "jdbc:mysql://localhost:3306/sistema_restaurante";
    private static final String USER = "admin_rest";
    private static final String PASS = "rest123";
    private SqlLib sql = new SqlLib();

    @FXML
    private TableView<Pedido> tablaPedidos;

    @FXML
    private TableColumn<Pedido, Integer> colIdPedido;

    @FXML
    private TableColumn<Pedido, Integer> colIdMesa;

    @FXML
    private TableColumn<Pedido, String> colFecha;

    @FXML
    private TableColumn<Pedido, String> colEstado;

    @FXML
    private TableColumn<Pedido, Double> colTotal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarPedidos();
    }

    private void configurarTabla() {
        colIdPedido.setCellValueFactory(new PropertyValueFactory<>("idPedido"));
        colIdMesa.setCellValueFactory(new PropertyValueFactory<>("idMesa"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
    }

    private void cargarPedidos() {
        ObservableList<Pedido> lista = FXCollections.observableArrayList();

        String sql = """
            SELECT 
                p.id_pedido,
                p.id_mesa,
                p.fecha,
                p.estado,
                SUM(dp.cantidad * dp.precio_unitario) AS total
            FROM pedidos p
            INNER JOIN detalle_pedido dp ON p.id_pedido = dp.id_pedido
            GROUP BY p.id_pedido, p.id_mesa, p.fecha, p.estado
            ORDER BY p.id_pedido DESC
        """;

        try (Connection conn = DriverManager.getConnection(URL_BD, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pedido pedido = new Pedido(
                        rs.getInt("id_pedido"),
                        rs.getInt("id_mesa"),
                        rs.getString("fecha"),
                        rs.getString("estado"),
                        rs.getDouble("total")
                );

                lista.add(pedido);
            }

            tablaPedidos.setItems(lista);
            System.out.println("Pedidos cargados: " + lista.size());

        } catch (Exception e) {
            System.out.println("Error al cargar pedidos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
private void btnModificarClick(ActionEvent event) {
    Pedido seleccionado = tablaPedidos.getSelectionModel().getSelectedItem();

    if (seleccionado == null) {
        System.out.println("Selecciona un pedido para modificar.");
        return;
    }

    ChoiceDialog<String> dialog = new ChoiceDialog<>(
            seleccionado.getEstado(),
            "Pendiente",
            "Preparando",
            "Entregado",
            "Cancelado"
    );

    dialog.setTitle("Modificar pedido");
    dialog.setHeaderText("Modificar estado del pedido #" + seleccionado.getIdPedido());
    dialog.setContentText("Nuevo estado:");

    Optional<String> resultado = dialog.showAndWait();

    if (resultado.isPresent()) {
        modificarEstadoPedidoBD(seleccionado.getIdPedido(), resultado.get());
        cargarPedidos();
    }
}

private void modificarEstadoPedidoBD(int idPedido, String nuevoEstado) {
    String sql = "UPDATE pedidos SET estado = ? WHERE id_pedido = ?";

    try (Connection conn = DriverManager.getConnection(URL_BD, USER, PASS);
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, nuevoEstado);
        ps.setInt(2, idPedido);

        int filas = ps.executeUpdate();

        if (filas > 0) {
            System.out.println("Pedido modificado correctamente. ID: " + idPedido);
        } else {
            System.out.println("No se encontró el pedido.");
        }

    } catch (Exception e) {
        System.out.println("Error al modificar pedido: " + e.getMessage());
        e.printStackTrace();
    }
}
    
    @FXML
private void btnEliminarClick(ActionEvent event) {
    Pedido seleccionado = tablaPedidos.getSelectionModel().getSelectedItem();

    if (seleccionado == null) {
        System.out.println("Selecciona un pedido para eliminar.");
        return;
    }

    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
    confirmacion.setTitle("Confirmar eliminación");
    confirmacion.setHeaderText("Eliminar pedido");
    confirmacion.setContentText("¿Seguro que quieres eliminar el pedido #" + seleccionado.getIdPedido() + "?");

    Optional<ButtonType> resultado = confirmacion.showAndWait();

    if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
        eliminarPedidoBD(seleccionado.getIdPedido());
        cargarPedidos();
    }
}

private void eliminarPedidoBD(int idPedido) {
    String sqlDetalle = "DELETE FROM detalle_pedido WHERE id_pedido = ?";
    String sqlPedido = "DELETE FROM pedidos WHERE id_pedido = ?";

    try (Connection conn = DriverManager.getConnection(URL_BD, USER, PASS)) {

        conn.setAutoCommit(false);

        try (PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);
             PreparedStatement psPedido = conn.prepareStatement(sqlPedido)) {

            psDetalle.setInt(1, idPedido);
            psDetalle.executeUpdate();

            psPedido.setInt(1, idPedido);
            psPedido.executeUpdate();

            conn.commit();

            System.out.println("Pedido eliminado correctamente. ID: " + idPedido);
        }

    } catch (Exception e) {
        System.out.println("Error al eliminar pedido: " + e.getMessage());
        e.printStackTrace();
    }
}
@FXML
private void btnRegresarClick(ActionEvent event) {
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.close();
}
@FXML
private void btnLiberarMesaClick(ActionEvent event) {
    Pedido seleccionado = tablaPedidos.getSelectionModel().getSelectedItem();

    if (seleccionado == null) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Sin selección");
        alerta.setHeaderText("No seleccionaste ningún pedido");
        alerta.setContentText("Selecciona un pedido para liberar su mesa.");
        alerta.showAndWait();
        return;
    }

    int idMesa = seleccionado.getIdMesa();

    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
    confirmacion.setTitle("Liberar mesa");
    confirmacion.setHeaderText("¿Deseas liberar la mesa #" + idMesa + "?");
    confirmacion.setContentText("La mesa cambiará su estado a Disponible.");

    Optional<ButtonType> resultado = confirmacion.showAndWait();

    if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
        try {
            sql.actualizarEstadoMesa(idMesa, "Disponible");

            // Opcional: cambiar también el estado del pedido
            modificarEstadoPedidoBD(seleccionado.getIdPedido(), "Finalizado");

            cargarPedidos();

            Alert exito = new Alert(Alert.AlertType.INFORMATION);
            exito.setTitle("Mesa liberada");
            exito.setHeaderText(null);
            exito.setContentText("La mesa #" + idMesa + " ahora está disponible.");
            exito.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();

            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("No se pudo liberar la mesa");
            error.setContentText("Revisa la conexión o la tabla de mesas.");
            error.showAndWait();
        }
    }
}
}
 
    

    