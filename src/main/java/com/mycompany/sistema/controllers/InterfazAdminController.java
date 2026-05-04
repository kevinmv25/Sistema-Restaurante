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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.ActionEvent; 


public class InterfazAdminController implements Initializable {

    @FXML private Button btn_sidebar;
    @FXML private BorderPane root;
    @FXML private VBox sidebar;
    @FXML private HBox centro;
    @FXML private Button btn_menuAdmin;
    @FXML private Button btn_inventario;
    @FXML private Button btn_empleados;
    @FXML private Button btn_reportes;
    
    
    private boolean sidebarVisible = true;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        sidebar.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getRoot().setUserData("/scenes/menu-admin.fxml");
            }
        });

    }

    
    @FXML
    private void mostrarSidebar() {
        if (!sidebarVisible) {
            animarMostrarSidebar();
        }
    }

    
    @FXML
    private void ocultarSidebar() {
        if (sidebarVisible) {
            animarOcultarSidebar();
        }
    }

    
    private void animarMostrarSidebar() {

        root.setLeft(sidebar); // primero lo agregas

        double width = sidebar.getWidth();

        sidebar.setTranslateX(-width); // empieza fuera

        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.millis(180));
        slide.setNode(sidebar);
        slide.setToX(0);
        slide.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);

        slide.play();

        sidebarVisible = true;
    }


   
    private void animarOcultarSidebar() {

        double width = sidebar.getWidth();

        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.millis(180));
        slide.setNode(sidebar);
        slide.setToX(-width);
        slide.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);

        slide.setOnFinished(e -> {
            root.setLeft(null); // ahora sí lo quitamos limpio
        });

        slide.play();

        sidebarVisible = false;
    }


    
    private void applyFadeEffect() {
        FadeTransition ft = new FadeTransition(Duration.millis(200), centro);
        ft.setFromValue(0.8);
        ft.setToValue(1.0);
        ft.play();
    }
    
    public void cambiarEscena(String fxml, Node node) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));

            Scene scene = node.getScene();

        
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
    
    @FXML
    private void sceneControl(javafx.event.ActionEvent event){
        cambiarEscena("/scenes/control.fxml", (Node) event.getSource() );
    }
    
}
