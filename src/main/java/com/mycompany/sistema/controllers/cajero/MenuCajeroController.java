/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistema.controllers.cajero;

import com.mycompany.sistema.services.SceneService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Controla la navegación principal del módulo de cajero.
 *
 * <p>Esta clase funciona como punto de entrada para los casos de uso del cajero.
 * Desde aquí se puede acceder a facturación, pagos, caja, descuentos,
 * reembolsos e historial de ventas.</p>
 *
 * <p>No contiene reglas de negocio; su responsabilidad es mantener una
 * navegación clara entre las pantallas del módulo.</p>
 *
 * @author Gutierrez Colorado Oliver
 * @see SceneService
 */
public class MenuCajeroController {
    
    /**
    * Abre la pantalla de generación de cuenta, ticket y factura.
    *
    * @param event evento generado por el botón correspondiente.
    */
    @FXML
    private void irFacturacion(ActionEvent event) {
        SceneService.cambiarEscena(event, "/scenes/cajero/facturacion.fxml");
    }
    
    /**
     * Abre la pantalla de registro de pago.
     *
     * @param event evento generado por el botón correspondiente.
     */
    @FXML
    private void irPagos(ActionEvent event) {
        SceneService.cambiarEscena(event, "/scenes/cajero/pagos.fxml");
    }

    /**
    * Abre la pantalla de gestión de caja.
    *
    * @param event evento generado por el botón correspondiente.
    */
    @FXML
    private void irCaja(ActionEvent event) {
        SceneService.cambiarEscena(event, "/scenes/cajero/gestion_caja.fxml");
    }

    /**
    * Abre la pantalla para aplicar descuentos y cortesías.
    *
    * @param event evento generado por el botón correspondiente.
    */
    @FXML
    private void irDescuentos(ActionEvent event) {
        SceneService.cambiarEscena(event, "/scenes/cajero/descuentos.fxml");
    }

    /**
    * Abre la pantalla de cancelación de pagos y reembolsos.
    *
    * @param event evento generado por el botón correspondiente.
    */
    @FXML
    private void irReembolsos(ActionEvent event) {
        SceneService.cambiarEscena(event, "/scenes/cajero/reembolsos.fxml");
    }

    /**
    * Cierra la sesión del cajero y regresa a la pantalla de login.
    *
    * @param event evento generado por el botón de cerrar sesión.
    */
    @FXML
    private void cerrarSesion(ActionEvent event) {
        SceneService.cambiarEscena(event, "/scenes/login.fxml");
    }
    
    /**
    * Abre la pantalla de historial de ventas y movimientos.
    *
    * @param event evento generado por el botón correspondiente.
    */
    @FXML
    private void irHistorialVentas(ActionEvent event) {
        SceneService.cambiarEscena(event, "/scenes/cajero/historial_ventas.fxml");
    }
}
