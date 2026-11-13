package com.mycompany.proyectofinal;

import com.mycompany.proyectofinal.config.Conexion;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProyectoFinal {

    public static void main(String[] args) {
        Conexion.probar();
        SpringApplication.run(ProyectoFinal.class, args);
    }
}