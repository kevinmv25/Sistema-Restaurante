package com.mycompany.sistema.controllers.cajero;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PagosController {
    @FXML private TextField txtSaldo;
    @FXML private ComboBox<String> comboMetodo;
    @FXML private TextField txtMonto;

    @FXML
    private void handleProcesarPago() {
        // Aquí llamarás a: gestorPagos.procesarPago();
        System.out.println("procesando pago...");
    }
}