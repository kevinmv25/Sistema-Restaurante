package com.mycompany.sistema.controllers;

import com.mycompany.sistema.models.Empleado;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import lib.SqlLib;

public class DialogEmpleadoController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtPuesto;
    @FXML private TextField txtHorario;
    @FXML private TextField txtEstado;
    @FXML private TextField txtSalario;
    @FXML private TextField txtVacaciones;

    private Empleado empleado;
    private SqlLib sql = new SqlLib();

    private boolean editando = false;
    private boolean guardado = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setEmpleado(Empleado e) {

        this.empleado = e;
        this.editando = true;

        txtNombre.setText(e.getNombre());
        txtTelefono.setText(e.getTelefono());
        txtCorreo.setText(e.getCorreo());
        txtPuesto.setText(e.getPuesto());
        txtHorario.setText(e.getHorario());
        txtEstado.setText(e.getEstatus());
        txtSalario.setText(String.valueOf(e.getSalario()));
        txtVacaciones.setText(e.getVacaciones());
    }

    @FXML
    private void guardarEmpleado() {

        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();
        String puesto = txtPuesto.getText().trim();
        String horario = txtHorario.getText().trim();
        String estado = txtEstado.getText().trim();
        String salarioTxt = txtSalario.getText().trim();
        String vacaciones = txtVacaciones.getText().trim();

        if (nombre.isEmpty()
                || telefono.isEmpty()
                || correo.isEmpty()
                || puesto.isEmpty()
                || horario.isEmpty()
                || estado.isEmpty()
                || salarioTxt.isEmpty()) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campos incompletos",
                    "Todos los campos obligatorios deben estar llenos."
            );
            return;
        }

        if (!correo.contains("@")) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Correo inválido",
                    "Ingrese un correo válido."
            );
            return;
        }

        if (!telefono.matches("\\d+")) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Teléfono inválido",
                    "El teléfono solo debe contener números."
            );
            return;
        }

        try {

            double salario = Double.parseDouble(salarioTxt);

            if (salario < 0) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Salario inválido",
                        "El salario no puede ser negativo."
                );
                return;
            }

            if (editando) {

                empleado.setNombre(nombre);
                empleado.setTelefono(telefono);
                empleado.setCorreo(correo);
                empleado.setPuesto(puesto);
                empleado.setHorario(horario);
                empleado.setEstatus(estado);
                empleado.setSalario(salario);
                empleado.setVacaciones(vacaciones);

                sql.actualizarEmpleado(empleado);

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Empleado actualizado",
                        "Empleado actualizado correctamente."
                );

                guardado = true;

            } else {

                Empleado nuevo = new Empleado(
                        nombre,
                        "",
                        telefono,
                        correo,
                        puesto,
                        horario,
                        estado,
                        vacaciones,
                        salario
                );

                sql.insertarEmpleado(nuevo);

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Empleado agregado",
                        "Empleado agregado correctamente."
                );

                guardado = true;
            }

            cerrarVentana();

        } catch (NumberFormatException e) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Salario inválido",
                    "El salario debe ser un número válido."
            );

        } catch (Exception e) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error",
                    "Ocurrió un error al guardar el empleado."
            );

            e.printStackTrace();
        }
    }

    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public boolean isGuardado() {
        return guardado;
    }
}