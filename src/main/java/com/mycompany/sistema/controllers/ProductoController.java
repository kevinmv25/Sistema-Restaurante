/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistema.controllers;

import com.mycompany.sistema.database.ConexionDB;
import com.mycompany.sistema.models.Producto;
import java.sql.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

public class ProductoController {

    @FXML
    private FlowPane flowPaneProductos; // Asegúrate de que en SceneBuilder el FlowPane tenga este ID

    // Este método lo llamarás cuando presiones un botón de categoría
    public void cargarProductos(String categoria) {
        flowPaneProductos.getChildren().clear(); // Limpiamos la pantalla

        String sql = "SELECT p.nombre, p.precio FROM productos p " +
                     "JOIN categorias c ON p.id_categoria = c.id_categoria " +
                     "WHERE c.nombre = ?";

        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, categoria);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                double precio = rs.getDouble("precio");

                // Creamos el botón del producto físicamente
                Button btn = new Button(nombre + "\n$" + precio);
                btn.setPrefSize(140, 140);
                
                // Acción al hacer clic en el producto
                btn.setOnAction(e -> {
                    System.out.println("Agregado al pedido: " + nombre);
                    // Aquí iría la lógica para mandarlo a la lista blanca de la izquierda
                });

                flowPaneProductos.getChildren().add(btn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}