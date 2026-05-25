/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistema.controllers.cajero;

import javafx.fxml.FXML;
import javafx.scene.control.*;
/**
 *
 * @author olive
 */
public class FacturacionController {
    @FXML private TableView<?> tablaPedidos; // Reemplaza ? por tu modelo si lo deseas
    @FXML private ComboBox<String> comboTipoDoc;
    @FXML private ComboBox<String> comboFormato;
    @FXML private RadioButton radioIncluirIva;
    @FXML private RadioButton radioDesglosarIva;
    @FXML private TextArea txtVistaPrevia;

    @FXML
    private void handleGenerarDocumento() {
        // Aquí llamarás a: gestorFacturacion.generarDocumento();
        System.out.println("generando documento...");
    }
}
