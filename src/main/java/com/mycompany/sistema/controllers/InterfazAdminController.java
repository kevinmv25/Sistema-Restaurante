package com.mycompany.sistema.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class InterfazAdminController implements Initializable {

    @FXML private Button btn_sidebar;
    @FXML private BorderPane root;
    @FXML private VBox sidebar;
    @FXML private HBox centro;

    private boolean sidebarVisible = true;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        

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


    // ✨ EFECTO SUAVE EN CENTRO
    private void applyFadeEffect() {
        FadeTransition ft = new FadeTransition(Duration.millis(200), centro);
        ft.setFromValue(0.8);
        ft.setToValue(1.0);
        ft.play();
    }
}
