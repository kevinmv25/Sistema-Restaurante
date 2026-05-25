package com.mycompany.sistema.controllers.cajero;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class DescuentosController {
    @FXML private TableView<?> tablaProductos;
    @FXML private ComboBox<String> comboTipoDescuento;
    @FXML private TextField txtValor;
    @FXML private TextArea txtMotivo;

    @FXML
    private void handleAplicarDescuento() {
        // Aquí llamarás a: gestorDescuentos.aplicarDescuento();
        System.out.println("descuento aplicado...");
    }
}