package com.mycompany.sistema.controllers;

import com.mycompany.sistema.models.Producto;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class DialogMenuAdminController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtPrecio;
    @FXML private ComboBox<String> comboCategoria;
    @FXML private Button btnGuardar;

    private boolean editando = false;
    private int idProducto;
    private boolean guardado = false;

    private final String URL_DB = "jdbc:mysql://localhost:3306/sistema_restaurante";
    private final String USER = "admin_rest";
    private final String PASS = "rest123";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboCategoria.getItems().addAll(
                "Platillo",
                "Bebida",
                "Postre",
                "Licor",
                "Entrada"
        );
    }

    /*
    public void setModoEdicion(Producto p) {
        editando = true;
        idProducto = p.getId();

        txtNombre.setText(p.getNombre());
        txtDescripcion.setText(p.getDescripcion());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        comboCategoria.setValue(p.getCategoria());
    }
    */

    @FXML
    public void guardar() {
        // VALIDACIÓN DE CAMPOS VACÍOS (CP-05 / CU-03-CP-02)
        if (txtNombre.getText().trim().isEmpty() || 
            txtPrecio.getText().trim().isEmpty() || 
            comboCategoria.getValue() == null) {
            
            new Alert(Alert.AlertType.WARNING, "Todos los campos (Nombre, Precio y Categoría) deben estar llenos").showAndWait();
            return;
        }

        String nombre = txtNombre.getText();
        String desc = txtDescripcion.getText();
        double precio;
        
        // VALIDACIÓN DE FORMATO DE PRECIO
        try {
            precio = Double.parseDouble(txtPrecio.getText());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "El precio debe ser un número válido").showAndWait();
            return;
        }
        
        String categoria = comboCategoria.getValue();

        try (Connection conn = DriverManager.getConnection(URL_DB, USER, PASS)) {

            int idCategoria = obtenerIdCategoria(conn, categoria);

            if (editando) {
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE productos SET nombre=?, descripcion=?, precio=?, id_categoria=? WHERE id_producto=?"
                );

                ps.setString(1, nombre);
                ps.setString(2, desc);
                ps.setDouble(3, precio);
                ps.setInt(4, idCategoria);
                ps.setInt(5, idProducto);
                ps.executeUpdate();
                
                new Alert(Alert.AlertType.INFORMATION, "Producto actualizado").showAndWait();
                guardado = true;
            } else {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO productos(nombre, descripcion, precio, id_categoria) VALUES (?,?,?,?)"
                );

                ps.setString(1, nombre);
                ps.setString(2, desc);
                ps.setDouble(3, precio);
                ps.setInt(4, idCategoria);
                ps.executeUpdate();

                new Alert(Alert.AlertType.INFORMATION, "Producto agregado").showAndWait();
                guardado = true;
            }

            cerrarVentana();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error de base de datos: " + e.getMessage()).showAndWait();
            e.printStackTrace();
        }
    }

    private int obtenerIdCategoria(Connection conn, String nombre) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT id_categoria FROM categorias WHERE nombre=?"
        );

        ps.setString(1, nombre);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("id_categoria");
        }

        return 1;
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }
    
    public boolean isGuardado() {
        return guardado;
    }
}