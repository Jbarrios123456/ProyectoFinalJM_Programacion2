package com.mycompany.proyectofinal.config;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
public class Conexion {
 
    private static final String URL = "jdbc:mysql://localhost:3306/proyectofinal?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root";
    private static final String CLAVE = "Mynor45074472";
 
    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
 
    public static boolean probar() {
        try (Connection con = obtener()) {
            System.out.println("CONEXION EXITOSA a la base de datos: " + con.getCatalog());
            return true;
        } catch (SQLException e) {
            System.err.println("CONEXION FALLIDA");
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("Codigo de error: " + e.getErrorCode());
            switch (e.getErrorCode()) {
                case 1045:
                    System.err.println("Causa probable: usuario o clave incorrectos");
                    break;
                case 1049:
                    System.err.println("Causa probable: la base de datos proyectofinal no existe");
                    break;
                default:
                    System.err.println("Causa probable: MySQL apagado o puerto incorrecto");
            }
            return false;
        }
    }
}
