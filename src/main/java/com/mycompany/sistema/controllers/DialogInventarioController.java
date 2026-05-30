package com.mycompany.sistema.controllers;

import com.mycompany.sistema.models.insumos;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import javafx.stage.Stage;

import lib.SqlLib;

public class DialogInventarioController implements Initializable {

    @FXML private TextField txtInsumo;
    @FXML private TextField txtStock;
    @FXML private TextField txtMedida;
    @FXML private TextField txtCategoria;
    @FXML private TextField txtEstatus;

    private insumos insumo;
    private SqlLib sql = new SqlLib();

    private boolean editando = false;
    private boolean guardado = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setInsumo(insumos i) {

        this.insumo = i;
        this.editando = true;

        txtInsumo.setText(i.getNombre());
        txtStock.setText(String.valueOf(i.getStock()));
        txtMedida.setText(i.getUnidadMedida());
        txtCategoria.setText(i.getCategoria());
        txtEstatus.setText(i.getEstatus());
    }

    @FXML
    private void guardarInsumo() {

        String nombre = txtInsumo.getText();
        String stockTxt = txtStock.getText();
        String unidadMedida = txtMedida.getText();
        String categoria = txtCategoria.getText();
        String estatus = txtEstatus.getText();

        try {

            double stock = stockTxt.isEmpty() ? 0 : Double.parseDouble(stockTxt);

            if (stock <= 0) {
                estatus = "agotado";
            } else if (estatus == null || estatus.trim().isEmpty()) {
                estatus = "disponible";
            }

            if (editando) {

                insumo.setNombre(nombre);
                insumo.setStock(stock);
                insumo.setUnidadMedida(unidadMedida);
                insumo.setCategoria(categoria);
                insumo.setEstatus(estatus);

                sql.actualizarInsumo(insumo);

                new Alert(Alert.AlertType.INFORMATION, "Insumo actualizado").showAndWait();
                guardado = true;

            } else {

                insumos nuevo = new insumos(
                    nombre,
                    stock,
                    unidadMedida,
                    categoria,
                    estatus
                );

                sql.insertarInsumo(nuevo);

                new Alert(Alert.AlertType.INFORMATION, "Insumo agregado").showAndWait();
                guardado = true;
            }

            cerrarVentana();

        } catch (NumberFormatException e) {

            new Alert(Alert.AlertType.ERROR, "El stock debe ser un número válido").showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo guardar el insumo").showAndWait();
        }
    }

    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtInsumo.getScene().getWindow();
        stage.close();
    }

    public boolean isGuardado() {
        return guardado;
    }
}