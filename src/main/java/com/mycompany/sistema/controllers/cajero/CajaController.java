package com.mycompany.sistema.controllers.cajero;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CajaController {
    @FXML private TextField txtMontoInicial;
    @FXML private TextField txtTotalEsperado;
    @FXML private TextField txtConteoFisico;
    @FXML private TextArea txtJustificacion;

    @FXML
    private void handleAbrirCaja() {
        // Aquí llamarás a: gestorCaja.registrarApertura();
        System.out.println("caja abierta...");
    }

    @FXML
    private void handleCerrarCaja() {
        // Aquí llamarás a: gestorCaja.registrarCierre();
        System.out.println("caja cerrada...");
    }
}