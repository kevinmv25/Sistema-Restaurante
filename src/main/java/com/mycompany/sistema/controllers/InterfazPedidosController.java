package com.mycompany.sistema.controllers;

import com.mycompany.sistema.models.Producto;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

public class InterfazPedidosController {

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private FlowPane flowPanePedidos;

    private List<Producto> pedidoActual = new ArrayList<>();

    // DATOS DE CONEXIÓN (Cámbialos por los tuyos)
    private final String URL = "jdbc:mysql://localhost:3306/nombre_de_tu_bd";
    private final String USER = "tu_usuario";
    private final String PASS = "tu_contraseña";

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
    }

    // --- MÉTODOS DE CATEGORÍA (Llaman al motor cargarTabla) ---
    @FXML public void btnDesayunosClick(ActionEvent event) { cargarTabla("DESAYUNOS"); }
    @FXML public void btnComidasClick(ActionEvent event)   { cargarTabla("COMIDAS"); }
    @FXML public void btnPostresClick(ActionEvent event)   { cargarTabla("POSTRES"); }
    @FXML public void btnBebidasClick(ActionEvent event)   { cargarTabla("BEBIDAS"); }

    // --- MOTOR DE CARGA DESDE BD ---
    private void cargarTabla(String categoria) {
        System.out.println("Cargando " + categoria + " desde BD...");
        ObservableList<Producto> lista = FXCollections.observableArrayList();
        
        String sql = "SELECT nombre, precio FROM productos WHERE categoria = ?";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, categoria);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(new Producto(rs.getString("nombre"), rs.getDouble("precio")));
            }
            tablaProductos.setItems(lista);
            System.out.println("Carga exitosa: " + lista.size() + " productos.");
            
        } catch (SQLException e) {
            System.err.println("Error de BD: " + e.getMessage());
        }
    }

    // --- BOTÓN AGREGAR (Unificado y limpio) ---
    @FXML 
    public void agregarAlPedido(ActionEvent event) {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            pedidoActual.add(seleccionado);
            Button btn = new Button(seleccionado.getNombre() + " - $" + seleccionado.getPrecio());
            flowPanePedidos.getChildren().add(btn);
        }
    }

    // --- BOTÓN GUARDAR ---
    @FXML 
    public void btnConfirmarClick(ActionEvent event) {
        if (pedidoActual.isEmpty()) return;
        System.out.println("Guardando " + pedidoActual.size() + " productos en la BD...");
        pedidoActual.clear();
        flowPanePedidos.getChildren().clear();
    }
}