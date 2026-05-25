package com.mycompany.sistema.controllers;

import com.mycompany.sistema.models.Asistencia;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.layout.AnchorPane;

import javafx.stage.Stage;

import javafx.util.Duration;

import lib.SqlLib;

public class AsistenciaController implements Initializable, SidebarActions {

    @FXML
    private TableView<Asistencia> tablaAsistencias;

    @FXML private TableColumn<Asistencia, String> col_ID;
    @FXML private TableColumn<Asistencia, String> col_Empleado;
    @FXML private TableColumn<Asistencia, String> col_Fecha;
    @FXML private TableColumn<Asistencia, String> col_HoraEntrada;
    @FXML private TableColumn<Asistencia, String> col_HoraSalida;
    @FXML private TableColumn<Asistencia, String> col_Estado;
    @FXML private TableColumn<Asistencia, String> col_Horario;

    @FXML private ComboBox<String> comboFiltro;
    @FXML private TextField txt_buscar;

    @FXML private SidebarController sidebarController;
    @FXML private AnchorPane sidebar;

    private boolean abierto = false;

    private ObservableList<Asistencia> lista;

    private SqlLib sql = new SqlLib();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        configurarTabla();
        cargarDatos();

        comboFiltro.getItems().addAll("Fecha", "Día", "Hora entrada", "Hora salida", "Empleado");
        comboFiltro.setValue("Fecha");

        sidebar.setTranslateX(-200);

        if (sidebarController != null) {
            sidebarController.setParent(this);
        }
    }

    private void configurarTabla() {

        col_ID.setCellValueFactory(data ->
            new SimpleStringProperty(String.valueOf(data.getValue().getIdAsistencia()))
        );

        col_Empleado.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getNombreEmpleado())
        );

        col_Fecha.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getFecha())
        );

        col_HoraEntrada.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getEntrada())
        );

        col_HoraSalida.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getSalida())
        );

        col_Estado.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getEstado())
        );

        col_Horario.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getHorario())
        );
    }

    private void cargarDatos() {

        lista = FXCollections.observableArrayList(
            sql.obtenerAsistencias()
        );

        tablaAsistencias.setItems(lista);
    }

    @FXML
    private void buscarPorFiltro() {

        String filtro = comboFiltro.getValue();
        String valor = txt_buscar.getText().trim();

        if (filtro == null || valor.isEmpty()) {
            cargarDatos();
            return;
        }

        lista = FXCollections.observableArrayList(
            sql.buscarAsistenciasPorFiltro(filtro, valor)
        );

        tablaAsistencias.setItems(lista);
    }

    @FXML
    private void agregarAsistencia() {

        try {

            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/scenes/DialogAdmin/DialogAsistencia.fxml")
            );

            Parent root = loader.load();

            Stage stage = new Stage();

            stage.setTitle("Agregar Asistencia");
            stage.setScene(new Scene(root));

            stage.showAndWait();

            cargarDatos();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editarAsistencia() {

        Asistencia a = tablaAsistencias
            .getSelectionModel()
            .getSelectedItem();

        if (a != null) {

            try {

                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/scenes/DialogAdmin/DialogAsistencia.fxml")
                );

                Parent root = loader.load();

                DialogAsistenciaController controller =
                    loader.getController();

                controller.setAsistencia(a);

                Stage stage = new Stage();

                stage.setTitle("Editar Asistencia");
                stage.setScene(new Scene(root));

                stage.showAndWait();

                cargarDatos();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void eliminarAsistencia() {

        Asistencia a = tablaAsistencias
            .getSelectionModel()
            .getSelectedItem();

        if (a != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

            alert.setTitle("Eliminar");
            alert.setHeaderText("¿Eliminar asistencia?");
            alert.setContentText(
                a.getNombreEmpleado() + " - " + a.getFecha()
            );

            if (alert.showAndWait().get() == ButtonType.OK) {

                sql.eliminarAsistencia(
                    a.getIdAsistencia()
                );

                cargarDatos();
            }
        }
    }

    @FXML
    private void mostrarSidebar() {

        TranslateTransition tt =
            new TranslateTransition(Duration.millis(300), sidebar);

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

            TranslateTransition tt =
                new TranslateTransition(Duration.millis(300), sidebar);

            tt.setToX(-200);

            tt.play();

            abierto = false;
        }
    }
}