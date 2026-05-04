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

        String nombre = txtNombre.getText();
        String telefono = txtTelefono.getText();
        String correo = txtCorreo.getText();
        String puesto = txtPuesto.getText();
        String horario = txtHorario.getText();
        String estado = txtEstado.getText();
        String salarioTxt = txtSalario.getText();
        String vacaciones = txtVacaciones.getText();

        try {

            double salario = 0;

            // Validación básica
            if (!salarioTxt.isEmpty()) {
                salario = Double.parseDouble(salarioTxt);
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

                new Alert(Alert.AlertType.INFORMATION, "Empleado actualizado").showAndWait();
                guardado = true;

            } else {

                Empleado nuevo = new Empleado();

                nuevo.setNombre(nombre);
                nuevo.setTelefono(telefono);
                nuevo.setCorreo(correo);
                nuevo.setPuesto(puesto);
                nuevo.setHorario(horario);
                nuevo.setEstatus(estado);

                // 🔴 NUEVOS
                nuevo.setSalario(salario);
                nuevo.setVacaciones(vacaciones);

                sql.insertarEmpleado(nuevo);

                new Alert(Alert.AlertType.INFORMATION, "Empleado agregado").showAndWait();
                guardado = true;
            }

            cerrarVentana();

        } catch (NumberFormatException e) {

            new Alert(Alert.AlertType.ERROR, "El salario debe ser un número válido").showAndWait();

        } catch (Exception e) {
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

    public boolean isGuardado() {
        return guardado;
    }
}
