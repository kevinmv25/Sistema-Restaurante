package com.mycompany.sistema.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class InicioMeseroController {

    @FXML private FlowPane flowPanePedidos;

    @FXML
    public void initialize() {
        System.out.println("DEBUG: Iniciando InicioMeseroController...");
    }

    // ESTE ES EL MÉTODO QUE FALTABA
    // Asegúrate de que el botón en tu FXML tenga onAction="#abrirTomarPedido"
    @FXML
    public void abrirTomarPedido(ActionEvent event) {
        try {
            // Carga la nueva interfaz
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/Mesero/InterfazPedidos.fxml"));
            Parent root = loader.load();
            
            // Crea una nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Tomar Pedido");
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            System.err.println("Error al cargar la interfaz de pedidos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}