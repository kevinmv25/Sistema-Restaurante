/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistema.controllers.cajero;

import com.mycompany.sistema.services.SceneService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 *
 * @author olive
 */
public class MenuCajeroController {
    
    @FXML
    private void irFacturacion(ActionEvent event) {
        SceneService.cambiarEscena(event, "/scenes/cajero/facturacion.fxml");
    }

    @FXML
    private void irPagos(ActionEvent event) {
        SceneService.cambiarEscena(event, "/scenes/cajero/pagos.fxml");
    }

    @FXML
    private void irCaja(ActionEvent event) {
        SceneService.cambiarEscena(event, "/scenes/cajero/gestion_caja.fxml");
    }

    @FXML
    private void irDescuentos(ActionEvent event) {
        SceneService.cambiarEscena(event, "/scenes/cajero/descuentos.fxml");
    }

    @FXML
    private void irReembolsos(ActionEvent event) {
        SceneService.cambiarEscena(event, "/scenes/cajero/reembolsos.fxml");
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        SceneService.cambiarEscena(event, "/scenes/login.fxml");
    }
}
