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
 * Proporciona métodos reutilizables para cambiar escenas en JavaFX.
 *
 * <p>Esta clase evita repetir el mismo código de carga de archivos FXML en cada
 * controlador. También ayuda a mantener una navegación uniforme dentro del
 * módulo de cajero y el resto del sistema.</p>
 *
 * @author Gutierrez Colorado Oliver
 * @see ReceptorDatos
 */
public class SceneService {
    
    /**
    * Cambia la escena actual por la vista indicada.
    *
    * <p>El método recibe el evento del botón que originó la navegación, obtiene la
    * ventana actual y reemplaza su contenido por el FXML solicitado.</p>
    *
    * @param event evento generado desde la interfaz.
    * @param rutaFXML ruta absoluta del archivo FXML dentro de resources.
    */
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

    /**
    * Cambia de escena enviando un objeto de datos al controlador destino.
    *
    * <p>El controlador de la nueva pantalla debe implementar
    * <code>ReceptorDatos</code>. Si no lo implementa, la escena se carga de todos
    * modos, pero no se entrega información adicional.</p>
    *
    * @param event evento generado desde la interfaz.
    * @param rutaFXML ruta absoluta del archivo FXML que se cargará.
    * @param datos objeto que será enviado al controlador destino.
    * @see ReceptorDatos
    */
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
