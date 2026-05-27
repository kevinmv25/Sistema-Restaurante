package com.mycompany.sistema.controllers.cajero;

import javafx.fxml.*;
import javafx.scene.control.*;
import com.mycompany.sistema.services.*;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class CajaController implements Initializable {

    @FXML private TextField txtMontoInicial;
    @FXML private TextField txtTotalEsperado;
    @FXML private TextField txtConteoFisico;
    @FXML private TextArea txtJustificacion;

    private final CajeroService cajeroService = new CajeroService();

    private Integer turnoAbierto;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarEstadoCaja();
    }

    private void cargarEstadoCaja() {
        turnoAbierto = cajeroService.obtenerTurnoAbierto();

        if (turnoAbierto != null) {
            double totalEsperado = cajeroService.calcularTotalEsperadoTurno(turnoAbierto);

            txtTotalEsperado.setText(String.format("%.2f", totalEsperado));
            txtMontoInicial.setText("Caja abierta. Turno #" + turnoAbierto);
            txtMontoInicial.setEditable(false);

        } else {
            txtTotalEsperado.setText("0.00");
            txtMontoInicial.clear();
            txtMontoInicial.setPromptText("Ingresa fondo inicial");
            txtMontoInicial.setEditable(true);
        }

        txtConteoFisico.clear();
        txtJustificacion.clear();
    }

    @FXML
    private void handleAbrirCaja() {
        turnoAbierto = cajeroService.obtenerTurnoAbierto();

        if (turnoAbierto != null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Caja ya abierta",
                    "Ya existe una caja abierta con el turno #" + turnoAbierto + "."
            );
            return;
        }

        String montoTexto = txtMontoInicial.getText().trim();

        if (montoTexto.isEmpty()) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campo vacío",
                    "Ingresa el monto inicial de caja."
            );
            return;
        }

        double montoInicial;

        try {
            montoInicial = Double.parseDouble(montoTexto);
        } catch (NumberFormatException e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Monto inválido",
                    "El monto inicial debe ser un número válido."
            );
            return;
        }

        if (montoInicial < 0) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Monto inválido",
                    "El monto inicial no puede ser negativo."
            );
            return;
        }

        int idTurno = cajeroService.abrirCaja(montoInicial);

        if (idTurno == -1) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo abrir la caja."
            );
            return;
        }

        mostrarAlerta(
                Alert.AlertType.INFORMATION,
                "Caja abierta",
                "La caja se abrió correctamente.\nTurno #" + idTurno
        );

        cargarEstadoCaja();
    }

    @FXML
    private void handleCerrarCaja() {
        turnoAbierto = cajeroService.obtenerTurnoAbierto();

        if (turnoAbierto == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Caja cerrada",
                    "No hay una caja abierta para cerrar."
            );
            return;
        }

        if (cajeroService.existenCuentasPendientes()) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Operaciones pendientes",
                    "Existen cuentas pendientes de cobro. No se puede realizar el corte."
            );
            return;
        }

        String conteoTexto = txtConteoFisico.getText().trim();

        if (conteoTexto.isEmpty()) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campo vacío",
                    "Ingresa el conteo físico de efectivo y comprobantes."
            );
            return;
        }

        double totalFisico;

        try {
            totalFisico = Double.parseDouble(conteoTexto);
        } catch (NumberFormatException e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Monto inválido",
                    "El conteo físico debe ser un número válido."
            );
            return;
        }

        double totalEsperado = cajeroService.calcularTotalEsperadoTurno(turnoAbierto);
        double diferencia = totalFisico - totalEsperado;

        String justificacion = txtJustificacion.getText().trim();

        if (Math.abs(diferencia) > 0.01 && justificacion.isEmpty()) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Diferencia detectada",
                    "Existe una diferencia de $" + String.format("%.2f", diferencia) +
                    ". Debes ingresar una justificación."
            );
            return;
        }

        boolean cerrado = cajeroService.cerrarCaja(
                turnoAbierto,
                totalFisico,
                justificacion
        );

        if (!cerrado) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo cerrar la caja."
            );
            return;
        }

        String mensaje =
                "Corte de caja realizado correctamente.\n\n" +
                "Turno: #" + turnoAbierto + "\n" +
                "Total esperado: $" + String.format("%.2f", totalEsperado) + "\n" +
                "Total físico: $" + String.format("%.2f", totalFisico) + "\n" +
                "Diferencia: $" + String.format("%.2f", diferencia) + "\n";

        if (!justificacion.isEmpty()) {
            mensaje += "Justificación: " + justificacion + "\n";
        }

        mensaje += "\nTicket de corte generado.";

        mostrarAlerta(
                Alert.AlertType.INFORMATION,
                "Corte de caja",
                mensaje
        );

        cargarEstadoCaja();
    }

    @FXML
    private void volverMenuCajero(ActionEvent event) {
        SceneService.cambiarEscena(event, "/scenes/cajero/menu-cajero.fxml");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}