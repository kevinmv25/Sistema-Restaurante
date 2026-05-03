package com.mycompany.sistema.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import lib.SqlLib;
import com.mycompany.sistema.models.Producto;

public class menuadminController implements Initializable, SidebarActions {

    @FXML
    private AnchorPane sidebar;

    @FXML
    private Button btn_sidebar;

    @FXML
    private TableView<Producto> tablaProductos;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, String> colDescripcion;

    @FXML
    private TableColumn<Producto, Double> colPrecio;

    @FXML
    private TableColumn<Producto, String> colCategoria;

    private boolean abierto = false;

    private SqlLib db = new SqlLib();
    
    @FXML
    private SidebarController sidebarController;
    
    
    

    private ObservableList<Producto> listaProductos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        sidebar.setTranslateX(-200);

        // 🔥 CONFIGURAR COLUMNAS (IMPORTANTE)
        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombre()));

        colDescripcion.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDescripcion()));

        colPrecio.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getPrecio()));

        colCategoria.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCategoria()));

        cargarProductos(); // 🔥 cargar datos
        
        
        if (sidebarController != null) {
            sidebarController.setParent((SidebarActions) this);
        }
    }

    //
    private void cargarProductos() {
        listaProductos.clear();
        listaProductos.addAll(db.obtenerProductos()); // este método debe existir en SqlLib
        tablaProductos.setItems(listaProductos);
    }

    
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
    private void agregarProducto() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/DialogAdmin/DialogMenu-Admin.fxml"));
        Parent root = loader.load();

        DialogMenuAdminController controller = loader.getController();

        Stage stage = new Stage();
        stage.setTitle("Agregar producto");
        stage.setScene(new Scene(root));
        stage.showAndWait();

        if (controller.isGuardado()) {
            cargarProductos(); // refrescar tabla
        }
    }

    // 🔹 EDITAR PRODUCTO
    @FXML
    private void editarProducto() throws IOException {

        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            System.out.println("Selecciona un producto");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/DialogAdmin/DialogMenu-Admin.fxml"));
        Parent root = loader.load();

        DialogMenuAdminController controller = loader.getController();

        controller.setModoEdicion(seleccionado); 

        Stage stage = new Stage();
        stage.setTitle("Editar producto");
        stage.setScene(new Scene(root));
        stage.showAndWait();

        if (controller.isGuardado()) {
            cargarProductos(); //  refrescar tabla
        }
    }

    //
    @FXML
    private void eliminarProducto() {

        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            System.out.println("Selecciona un producto");
            return;
        }

        db.eliminarProducto(seleccionado.getId());
        cargarProductos();
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
