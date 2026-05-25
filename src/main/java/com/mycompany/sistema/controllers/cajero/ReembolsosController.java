package com.mycompany.sistema.controllers.cajero;

import javafx.fxml.FXML;
import javafx.scene.control.*;
/**
 *
 * @author olive
 */
public class ReembolsosController {
    @FXML private TextField txtBuscar;
    @FXML private TableView<?> tablaOrdenes;
    @FXML private TextArea txtMotivo;

    @FXML
    private void handleBuscarTicket() {
        System.out.println("buscando ticket...");
    }

    @FXML
    private void handleCancelarPago() {
        // Aquí llamarás a: gestorReembolsos.procesarReembolso();
        System.out.println("pago cancelado...");
    }
}