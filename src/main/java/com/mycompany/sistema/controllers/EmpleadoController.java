package com.mycompany.sistema.controllers;

import com.mycompany.sistema.models.Empleado;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleStringProperty;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.layout.AnchorPane;

import javafx.stage.Stage;

import javafx.util.Duration;

import lib.SqlLib;

public class EmpleadoController implements Initializable, SidebarActions {

    @FXML
    private TableView<Empleado> tablaEmpleados;

    @FXML private TableColumn<Empleado, String> colNombre;
    @FXML private TableColumn<Empleado, String> colPuesto;
    @FXML private TableColumn<Empleado, String> colTelefono;
    @FXML private TableColumn<Empleado, String> colHorario;
    @FXML private TableColumn<Empleado, String> colEstado;
    @FXML private TableColumn<Empleado, String> colVacaciones;
    @FXML private TableColumn<Empleado, String> colCorreo;
    @FXML private TableColumn<Empleado, String> colSalario;

    @FXML private SidebarController sidebarController;
    @FXML private AnchorPane sidebar;

    private boolean abierto = false;

    private ObservableList<Empleado> lista;
    private SqlLib sql = new SqlLib();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        configurarTabla();
        cargarDatos();

        sidebar.setTranslateX(-200);

        if (sidebarController != null) {
            sidebarController.setParent(this);
        }
    }

    private void configurarTabla() {

        colNombre.setCellValueFactory(data ->
            new SimpleStringProperty(
                data.getValue().getNombre() + " " + data.getValue().getApellido()
            )
        );

        colPuesto.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getPuesto())
        );

        colTelefono.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getTelefono())
        );

        colHorario.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getHorario())
        );

        colEstado.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getEstatus())
        );

        
        colVacaciones.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getVacaciones())
        );

        colCorreo.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getCorreo())
        );

        colSalario.setCellValueFactory(data ->
            new SimpleStringProperty(String.valueOf(data.getValue().getSalario()))
        );
    }

    private void cargarDatos() {
        lista = FXCollections.observableArrayList(sql.obtenerEmpleados());
        tablaEmpleados.setItems(lista);
    }

    // ================= CRUD =================

    @FXML
    private void agregarEmpleado() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/DialogAdmin/DialogEmpleado.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Agregar Empleado");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            cargarDatos();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editarEmpleado() {

        Empleado e = tablaEmpleados.getSelectionModel().getSelectedItem();

        if (e != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/DialogAdmin/DialogEmpleado.fxml"));
                Parent root = loader.load();

                DialogEmpleadoController controller = loader.getController();
                controller.setEmpleado(e);

                Stage stage = new Stage();
                stage.setTitle("Editar Empleado");
                stage.setScene(new Scene(root));
                stage.showAndWait();

                cargarDatos();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void eliminarEmpleado() {

        Empleado e = tablaEmpleados.getSelectionModel().getSelectedItem();

        if (e != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Eliminar");
            alert.setHeaderText("¿Eliminar empleado?");
            alert.setContentText(e.getNombre() + " " + e.getApellido());

            if (alert.showAndWait().get() == ButtonType.OK) {
                sql.eliminarEmpleado(e.getId());
                cargarDatos();
            }
        }
    }

    // ================= SIDEBAR =================

    @FXML
    private void mostrarSidebar() {

        TranslateTransition tt = new TranslateTransition(Duration.millis(300), sidebar);

        if (abierto) {
            tt.setToX(-200);
        } else {
            tt.setToX(0);
        }

        tt.play();
        abierto = !abierto;
    }

    @FXML
    @Override
    public void ocultarSidebar() {

        if (abierto) {
            TranslateTransition tt = new TranslateTransition(Duration.millis(300), sidebar);
            tt.setToX(-200);
            tt.play();
            abierto = false;
        }
    }
}
