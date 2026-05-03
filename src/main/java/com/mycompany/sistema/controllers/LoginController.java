package com.mycompany.sistema.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import lib.SqlLib;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

    private SqlLib db;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        db = new SqlLib(); 
    }

    
    private String handleLogin() {

        String username = txtUsuario.getText();
        String password = txtPassword.getText();

        
        if (!db.isValidCredentials(username, password)) {
            lblMensaje.setText("Usuario o contraseña incorrectos");
            return "nil";
        }

        
        String rol = db.getRole(username);

        
        if (rol.equalsIgnoreCase("usuario")) {
            lblMensaje.setText("No tienes acceso al sistema");
            return "denegado";
        }

        return rol;
    }

    
    private boolean validarCampos() {
        return !txtUsuario.getText().trim().isEmpty() &&
               !txtPassword.getText().trim().isEmpty();
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

    
    @FXML
    private void login(ActionEvent event) throws IOException {

        if (!validarCampos()) {
            mostrarAlerta("Todos los campos deben estar llenos");
            return;
        }

        String rol = handleLogin();

        if (rol.equals("nil") || rol.equals("denegado")) {
            return;
        }

        FXMLLoader loader = null;

        switch (rol.toLowerCase()) {
            //CAMBIAR EL NOMBRE DEL SCENE POR LA RUTA
            case "administrador":
                
                loader = new FXMLLoader(getClass().getResource("/scenes/interfazAdmin.fxml"));
                break;

            case "mesero":
                
                loader = new FXMLLoader(getClass().getResource("/scenes/AQUI_MESERO.fxml"));
                break;

            case "cocina":
                
                loader = new FXMLLoader(getClass().getResource("/scenes/AQUI_COCINA.fxml"));
                break;

            case "recepcionista":
                
                loader = new FXMLLoader(getClass().getResource("/scenes/Recepcionista/Recepcion.fxml"));
                break;

            case "cajero":
                
                loader = new FXMLLoader(getClass().getResource("/scenes/AQUI_CAJERO.fxml"));
                break;

            default:
                lblMensaje.setText("Rol no reconocido");
                return;
        }

        
        Parent root = loader.load();
        Stage stage = (Stage) btnLogin.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setResizable(false);
    }
}

