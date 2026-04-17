package com.mycompany.sistema.restaurante;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SistemaRestaurante extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/scenes/Recepcionista/MapaMesas.fxml"));

        Scene scene = new Scene(root);
        stage.setTitle("Mapa de Mesas - Restaurante");
        stage.setScene(scene);
        stage.show();
    }
    
    /*
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/interfazAdmin.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setTitle("Restaurante LISTI");
        stage.setScene(scene);
        stage.show();
    }
    */
}

