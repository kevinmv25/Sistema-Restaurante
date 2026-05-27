/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistema.services;

import java.io.IOException;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 *
 * @author olive
 */
public class SceneService {
    
    public static void cambiarEscena(ActionEvent event, String rutaFXML) {
        try {
            URL url = SceneService.class.getResource(rutaFXML);

            if (url == null) {
                System.err.println("No se encontró el archivo FXML: " + rutaFXML);
                return;
            }

            Parent root = FXMLLoader.load(url);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.println("Error al cargar la escena: " + rutaFXML);
            e.printStackTrace();
        }
    }

    public static void cambiarEscenaConDatos(ActionEvent event, String rutaFXML, Object datos) {
        try {
            URL url = SceneService.class.getResource(rutaFXML);

            if (url == null) {
                System.err.println("No se encontró el archivo FXML: " + rutaFXML);
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Object controller = loader.getController();

            if (controller instanceof ReceptorDatos) {
                ((ReceptorDatos) controller).recibirDatos(datos);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.println("Error al cargar la escena con datos: " + rutaFXML);
            e.printStackTrace();
        }
    }
}
