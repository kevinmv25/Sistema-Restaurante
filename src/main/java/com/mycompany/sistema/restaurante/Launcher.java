/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.mycompany.sistema.restaurante;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import lib.SqlLib;

public class Launcher {

    private static SqlLib sql;

    public static void main(String[] args) {

        String[] credentials = getDBCredentials();

        if (credentials == null) {
            System.out.println("Error: No se pudieron cargar las credenciales.");
            return;
        }

        try {
            // Inicializa tu conexión (en tu SqlLib no necesitas getInstance,
            // pero lo dejamos listo por si lo implementas)
            sql = new SqlLib();

        } catch (Exception e) {
            System.out.println("Error al conectar a la base de datos");
            e.printStackTrace();
            return;
        }

        // Llama a tu aplicación JavaFX principal
        SistemaRestaurante.main(args);
    }

    private static String[] getDBCredentials() {

        Properties properties = new Properties();
        String[] credentials = new String[3];

        try (FileInputStream fis = new FileInputStream("src/main/java/var/credentials.properties")) {

            properties.load(fis);

            credentials[0] = properties.getProperty("db.url");
            credentials[1] = properties.getProperty("db.user");
            credentials[2] = properties.getProperty("db.password");

        } catch (IOException e) {
            System.out.println("Error leyendo credenciales");
            e.printStackTrace();
            return null;
        }

        return credentials;
    }
}

