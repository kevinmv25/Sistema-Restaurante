package com.mycompany.sistema.controllers;

import com.mycompany.sistema.models.Asistencia;
import com.mycompany.sistema.models.Empleado;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import lib.SqlLib;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class DialogAsistenciaController implements Initializable {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtFecha;

    @FXML
    private TextField txtEntrada;

    @FXML
    private TextField txtSalida;

    @FXML
    private TextField txtEstado;

    @FXML
    private TextField txtHorario;

    private SqlLib sql = new SqlLib();

    private Empleado empleadoSeleccionado;

    private Asistencia asistenciaSeleccionada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtNombre.setOnAction(e -> buscarEmpleado());
        
        txtFecha.setText(
            LocalDate.now().toString()
        );
        
        txtEntrada.setText(
            LocalTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    @FXML
    private void buscarEmpleado() {

        String nombreCompleto = txtNombre.getText().trim();

        if (nombreCompleto.isEmpty()) {
            mostrarAlerta("Campo vacío", "Escribe el nombre completo del empleado.");
            return;
        }

        Empleado e = sql.buscarEmpleadoPorNombreCompleto(nombreCompleto);

        if (e != null) {

            empleadoSeleccionado = e;

            txtEstado.setText(e.getEstatus());
            txtHorario.setText(e.getHorario());

        } else {

            empleadoSeleccionado = null;

            txtEstado.clear();
            txtHorario.clear();

            mostrarAlerta("Empleado no encontrado", "No se encontró un empleado con ese nombre completo.");
        }
    }

    @FXML
    private void guardarAsistencia() {

        String fecha = txtFecha.getText().trim();
        String entrada = txtEntrada.getText().trim();
        String salida = txtSalida.getText().trim();
        String estado = txtEstado.getText().trim();
        String horario = txtHorario.getText().trim();

        if (fecha.isEmpty() || entrada.isEmpty()) {
            mostrarAlerta("Campos incompletos", "Completa fecha, hora de entrada y hora de salida.");
            return;
        }

        if (asistenciaSeleccionada == null) {

            if (empleadoSeleccionado == null) {
                mostrarAlerta("Empleado no seleccionado", "Primero busca un empleado válido.");
                return;
            }

            sql.insertarAsistencia(
                empleadoSeleccionado.getId(),
                fecha,
                entrada,
                salida,
                estado,
                horario
            );

            mostrarInfo("Asistencia registrada", "La asistencia se registró correctamente.");

        } else {

            sql.actualizarAsistencia(
                asistenciaSeleccionada.getIdAsistencia(),
                fecha,
                entrada,
                salida,
                estado,
                horario
            );

            mostrarInfo("Asistencia actualizada", "La asistencia se actualizó correctamente.");
        }

        limpiarCampos();
    }

    public void setAsistencia(Asistencia a) {

        this.asistenciaSeleccionada = a;

        txtNombre.setText(a.getNombreEmpleado());
        txtFecha.setText(a.getFecha());
        txtEntrada.setText(a.getEntrada());
        txtSalida.setText(a.getSalida());
        txtEstado.setText(a.getEstado());
        txtHorario.setText(a.getHorario());
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtFecha.clear();
        txtEntrada.clear();
        txtSalida.clear();
        txtEstado.clear();
        txtHorario.clear();

        empleadoSeleccionado = null;
        asistenciaSeleccionada = null;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}