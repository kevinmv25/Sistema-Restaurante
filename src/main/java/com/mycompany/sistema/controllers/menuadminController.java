/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.sistema.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author juego
 */
public class menuadminController implements Initializable, SidebarActions {

    
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
    
    @Override
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
    
    @FXML
    public void abrirDialog(ActionEvent event) throws IOException {
        System.out.println(
        getClass().getResource("/scenes/DialogAdmin/DialogMenu-Admin.fxml"));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/DialogAdmin/DialogMenu-Admin.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage(); 

        stage.setTitle("Agregar platillo");
        stage.setScene(new Scene(root));
        stage.show();
    
    }
    
}
