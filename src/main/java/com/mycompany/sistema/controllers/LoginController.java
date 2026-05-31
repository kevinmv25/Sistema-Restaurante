package com.mycompany.sistema.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import lib.SqlLib;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.*;

import javafx.stage.Stage;

public class LoginController implements Initializable {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMensaje;

    @FXML
    private Button btnLogin;

    @FXML
    private Hyperlink btnCliente;

    private SqlLib db;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        db = new SqlLib();
    }

    private String handleLogin() {

        String username = txtUsuario.getText();
        String password = txtPassword.getText();

        try {

            // Validar credenciales
            if (!db.isValidCredentials(username, password)) {

                // Si no existe, registrarlo como nuevo cliente
                boolean registrado = db.registrarClienteNuevo(username, password);

                if (registrado) {
                    return "usuario";
                }

                mostrarAlertaError(
                    "Acceso Denegado",
                    "Usuario o contraseña incorrectos."
                );

                return "nil";
            }

            // Obtener rol
            String rol = db.getRole(username);

            // Validar acceso
            if (rol.equalsIgnoreCase("usuario")) {

                mostrarAlertaError(
                    "Acceso Denegado",
                    "No tienes acceso al sistema."
                );

                return "denegado";
            }

            return rol;

        } catch (SQLException e) {

            mostrarErrorBD(
                "Error al validar credenciales, intente más tarde."
            );

            return "error_db";
        }
    }

    private boolean validarCampos() {

        return !txtUsuario.getText().trim().isEmpty()
                && !txtPassword.getText().trim().isEmpty();
    }

    private void mostrarAlerta(String mensaje) {

        Alert alert = new Alert(Alert.AlertType.WARNING);

        alert.setTitle("Campos incompletos");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        Stage stage = (Stage) txtUsuario.getScene().getWindow();

        alert.initOwner(stage);
        alert.showAndWait();
    }

    private void mostrarErrorBD(String mensaje) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error de Conexión");
        alert.setHeaderText("Fallo en el servidor");
        alert.setContentText(mensaje);

        Stage stage = (Stage) txtUsuario.getScene().getWindow();

        alert.initOwner(stage);
        alert.showAndWait();
    }

    private void mostrarAlertaError(String titulo, String mensaje) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        Stage stage = (Stage) txtUsuario.getScene().getWindow();

        alert.initOwner(stage);
        alert.showAndWait();
    }

    @FXML
    private void login(ActionEvent event) throws IOException {

        if (!validarCampos()) {

            mostrarAlerta("Todos los campos deben estar llenos");
            return;
        }

        String rol = handleLogin();

        if (rol.equals("nil")
                || rol.equals("denegado")
                || rol.equals("error_db")) {

            return;
        }

        FXMLLoader loader = null;

        switch (rol.toLowerCase()) {

            case "administrador":

                loader = new FXMLLoader(
                        getClass().getResource("/scenes/interfazAdmin.fxml")
                );

                break;

            case "mesero":

                try {

    Parent root = FXMLLoader.load(
            getClass().getResource("/scenes/Mesero/InicioMesero.fxml")
    );

    Stage stage = (Stage) ((Node) event.getSource())
            .getScene()
            .getWindow();

    stage.setScene(new Scene(root));
    stage.show();

    System.out.println("Pantalla cargada correctamente.");

    return;

} catch (Exception e) {

    System.err.println("ERROR EN LA CARGA:");
    e.printStackTrace();

    return;
}

            case "cocina":

                loader = new FXMLLoader(
                        getClass().getResource("/scenes/AQUI_COCINA.fxml")
                );

                break;

            case "recepcionista":

                loader = new FXMLLoader(
                        getClass().getResource("/scenes/Recepcionista/MapaMesas.fxml")
                );

                break;

            case "cajero":
                loader = new FXMLLoader(
                        getClass().getResource("/scenes/cajero/menu-cajero.fxml")
                );
                break;

            case "usuario":

                loader = new FXMLLoader(
                        getClass().getResource("/scenes/Usuario/InfoRest.fxml")
                );

                break;

            default:

                mostrarAlertaError(
                    "Error de Rol",
                    "Rol no reconocido en el sistema."
                );

                return;
        }

        if (loader == null) {
            System.out.println("Loader es NULL");
            return;
        }
        
        
        Parent root = loader.load();
        System.out.println("Loader = " + loader);

        Stage stage = (Stage) btnLogin.getScene().getWindow();

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.sizeToScene();
        stage.setResizable(false);
    }

    @FXML
    private void accesoDirecto(ActionEvent event) {

        cambiarEscena("/scenes/Usuario/InfoRest.fxml", event);
    }

    private void cambiarEscena(String ruta, ActionEvent event) {

        try {

            Parent root = FXMLLoader.load(
                    getClass().getResource(ruta)
            );

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException ex) {

            System.err.println(
                "No se pudo saltar el login: " + ruta
            );

            ex.printStackTrace();
        }
    }
}


