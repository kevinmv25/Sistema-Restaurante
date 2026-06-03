package com.mycompany.sistema.controllers.cajero;

import javafx.fxml.*;
import javafx.scene.control.*;
import com.mycompany.sistema.services.*;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.ResourceBundle;
/**
 * Controla la apertura y cierre de caja del módulo de cajero.
 *
 * <p>Esta clase implementa la lógica del caso de uso <b>CU-09 Realizar apertura
 * y corte de caja</b>. Su función principal es permitir que el cajero registre
 * el fondo inicial del turno, consulte el total esperado y realice el corte
 * final comparando el dinero físico contra los movimientos registrados.</p>
 *
 * <p>El <code>monto inicial</code> representa el fondo con el que inicia la caja.
 * El <code>total esperado</code> se calcula con la suma del monto inicial más
 * los pagos registrados, descontando cancelaciones o reembolsos. El
 * <code>conteo físico</code> es el dinero real contado por el cajero al cerrar
 * el turno.</p>
 *
 * @author Gutierrez Colorado Oliver
 * @see CajeroService
 * @see SceneService
 */
public class CajaController implements Initializable {

    @FXML private TextField txtMontoInicial;
    @FXML private TextField txtTotalEsperado;
    @FXML private TextField txtConteoFisico;
    @FXML private TextArea txtJustificacion;

    private final CajeroService cajeroService = new CajeroService();

    private Integer turnoAbierto;

    /**
    * Inicializa la pantalla de caja cargando el estado actual del turno.
    *
    * <p>Si existe una caja abierta, se muestra el turno activo y el total esperado.
    * Si no existe, se habilita el campo para ingresar el monto inicial.</p>
    *
    * @param url ubicación usada por JavaFX para resolver recursos.
    * @param rb recursos de internacionalización, si existieran.
    */
   @Override
   public void initialize(URL url, ResourceBundle rb) {
        cargarEstadoCaja();
    }

    /**
    * Consulta si hay una caja abierta y actualiza los campos visibles.
    *
    * <p>Este método evita que el cajero abra dos cajas al mismo tiempo y mantiene
    * sincronizada la interfaz con el estado real de la base de datos.</p>
    */
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
    
    /**
    * Abre un nuevo turno de caja con el monto inicial capturado por el cajero.
    *
    * <p>Antes de registrar la apertura, valida que no exista otra caja abierta y
    * que el monto inicial sea un número válido mayor o igual a cero.</p>
    */
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

    /**
    * Realiza el cierre de caja del turno actual.
    *
    * <p>Antes de cerrar, valida que no existan cuentas pendientes de cobro. Después
    * compara el total esperado contra el conteo físico. Si existe diferencia,
    * solicita una justificación para dejar evidencia en el corte.</p>
    */
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

    /**
    * Regresa al menú principal del módulo de cajero.
    *
    * @param event evento generado por el botón de regreso.
    * @see SceneService#cambiarEscena(ActionEvent, String)
    */
    @FXML
    private void volverMenuCajero(ActionEvent event) {
        SceneService.cambiarEscena(event, "/scenes/cajero/menu-cajero.fxml");
    }

    /**
    * Muestra una alerta informativa, de advertencia o de error.
    *
    * @param tipo tipo de alerta que se desea mostrar.
    * @param titulo título de la ventana emergente.
    * @param mensaje contenido que verá el usuario.
    */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}