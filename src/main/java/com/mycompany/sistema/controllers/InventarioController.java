package com.mycompany.sistema.controllers;

import com.mycompany.sistema.models.insumos;

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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.layout.AnchorPane;

import javafx.stage.Stage;

import javafx.util.Duration;

import lib.SqlLib;

public class InventarioController implements Initializable, SidebarActions {

    @FXML
    private TableView<insumos> tablaInsumos;

    @FXML
    private AnchorPane sidebar;

    @FXML
    private Button btn_sidebar;

    @FXML
    private Button btn_salir;

    @FXML
    private SidebarController sidebarController;

    private boolean abierto = false;

    private ObservableList<insumos> lista;

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

        TableColumn<insumos, String> colInsumo = new TableColumn<>("Insumo");
        colInsumo.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getNombre())
        );

        TableColumn<insumos, String> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(data ->
            new SimpleStringProperty(String.valueOf(data.getValue().getStock()))
        );

        TableColumn<insumos, String> colMedida = new TableColumn<>("Unidad de medida");
        colMedida.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getUnidadMedida())
        );

        TableColumn<insumos, String> colCategoria = new TableColumn<>("Categoría");
        colCategoria.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getCategoria())
        );

        TableColumn<insumos, String> colEstatus = new TableColumn<>("Estatus");
        colEstatus.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getEstatus())
        );

        tablaInsumos.getColumns().clear();

        tablaInsumos.getColumns().addAll(
            colInsumo,
            colStock,
            colMedida,
            colCategoria,
            colEstatus
        );
    }

    private void cargarDatos() {

        lista = FXCollections.observableArrayList(
            sql.obtenerInsumos()
        );

        tablaInsumos.setItems(lista);
    }

    @FXML
    private void agregarInsumo() {

        try {

            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/scenes/DialogAdmin/DialogInventario.fxml")
            );

            Parent root = loader.load();

            Stage stage = new Stage();

            stage.setTitle("Agregar Insumo");
            stage.setScene(new Scene(root));

            stage.showAndWait();

            cargarDatos();

        } catch (Exception e) {
            e.printStackTrace();

            new Alert(
                Alert.AlertType.ERROR,
                "No se pudo abrir la ventana para agregar insumo"
            ).showAndWait();
        }
    }

    @FXML
    private void editarInsumo() {

        insumos i = tablaInsumos
            .getSelectionModel()
            .getSelectedItem();

        if (i != null) {

            try {

                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/scenes/DialogAdmin/DialogInventario.fxml")
                );

                Parent root = loader.load();

                DialogInventarioController controller =
                    loader.getController();

                controller.setInsumo(i);

                Stage stage = new Stage();

                stage.setTitle("Editar Insumo");
                stage.setScene(new Scene(root));

                stage.showAndWait();

                cargarDatos();

            } catch (Exception e) {
                e.printStackTrace();

                new Alert(
                    Alert.AlertType.ERROR,
                    "No se pudo abrir la ventana para editar insumo"
                ).showAndWait();
            }

        } else {

            new Alert(
                Alert.AlertType.WARNING,
                "Selecciona un insumo para editar"
            ).showAndWait();
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