/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.sistema.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;



public class ControlController implements Initializable {

    
    
    @FXML
    private SidebarController sidebarController;
    
    @FXML
    private AnchorPane sidebar;
    
    @FXML
    private Button btnSidebar;
    
    @FXML 
    private Button btnListaEmpleados;
    
    private boolean abierto = false;
    
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    @FXML
    private void mostrarSidebar() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), sidebar);

        if (abierto) {
            tt.setToX(-200);
        } else {
            tt.setToX(0);
        }

        tt.play();
        abierto = !abierto;
    }
    
    @FXML
    public void ocultarSidebar(){
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), sidebar);
        
        if(abierto){
            tt.setToX(-200);
        } else {
            tt.setToX(0);
        }
        tt.play();
        abierto = !abierto;
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
    private void sceneEmpleado(javafx.event.ActionEvent event){
        cambiarEscena("/scenes/empleados.fxml", (Node) event.getSource() );
    }
} 

