package com.mycompany.sistema.controllers;

import com.mycompany.sistema.models.Producto;
import java.sql.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import lib.SqlLib;

public class ProductoController {

    @FXML
    private FlowPane flowPaneProductos;

    private SqlLib db = new SqlLib();

    public void cargarProductos(String categoria) {

        flowPaneProductos.getChildren().clear();

        String sql =
            "SELECT p.nombre, p.precio FROM productos p " +
            "JOIN categorias c ON p.id_categoria = c.id_categoria " +
            "WHERE c.nombre = ?";

        try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/sistema_restaurante",
                    "admin_rest",
                    "rest123");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, categoria);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                String nombre = rs.getString("nombre");
                double precio = rs.getDouble("precio");

                Button btn = new Button(nombre + "\n$" + precio);
                btn.setPrefSize(140, 140);

                btn.setOnAction(e -> {
                    System.out.println("Agregado al pedido: " + nombre);
                });

                flowPaneProductos.getChildren().add(btn);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
