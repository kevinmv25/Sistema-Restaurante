/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistema.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    // Si tus amigos se conectan a tu PC, cambia localhost por tu IP
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_restaurante";
    private static final String USER = "root";
    private static final String PASS = "tu_password"; // ¡No olvides poner tu pass!

    public static Connection conectar() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
}
