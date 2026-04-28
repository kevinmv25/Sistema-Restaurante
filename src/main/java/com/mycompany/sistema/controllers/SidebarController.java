package com.mycompany.sistema.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Duration;

public class SidebarController implements Initializable {

    private SidebarActions parent; //no guarda un controller en especifico, sino el que en ese momento lo use o implemente

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setParent(SidebarActions parent){
        this.parent = parent; 
    }

    @FXML
    private void cerrarSide(){
        parent.ocultarSidebar();
    }
    
    public void cambiarEscena(String fxml, Node node) {
        try {
            Scene scene = node.getScene();

            Object actual = scene.getRoot().getUserData();

            if (actual != null && actual.equals(fxml)) {
                return;
            }

            Parent root = FXMLLoader.load(getClass().getResource(fxml));

            root.setUserData(fxml); 

            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), scene.getRoot());
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            fadeOut.setOnFinished(e -> {
                scene.setRoot(root);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });

        fadeOut.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    @FXML
    private void sceneMenuAdmin(javafx.event.ActionEvent event){
        cambiarEscena("/scenes/menu-admin.fxml", (Node) event.getSource() );
    }
    
    @FXML
    private void sceneInventario(javafx.event.ActionEvent event){
        cambiarEscena("/scenes/inventario.fxml", (Node) event.getSource() );
    }
    
    @FXML
    private void scenePrincipal(javafx.event.ActionEvent event){
        cambiarEscena("/scenes/interfazAdmin.fxml", (Node) event.getSource() );
    }
    
    
}
