/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.sistema.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author juego
 */
public class menuadminController implements Initializable {

    
    @FXML
    private AnchorPane sidebar;
    
    @FXML
    private Button btn_sidebar;
    @FXML
    private Button btn_salir;
    
    @FXML
    private SidebarController sidebarController;

    private boolean abierto =  false; //bandera para comprobar
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       sidebar.setTranslateX(-200);
       sidebarController.setParent(this);
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
    
}
