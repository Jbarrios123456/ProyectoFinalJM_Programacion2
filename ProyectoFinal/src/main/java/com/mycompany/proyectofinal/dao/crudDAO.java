package com.mycompany.proyectofinal.dao;

import java.sql.SQLException;
import java.util.List;

public interface crudDAO<T> {

    void insertar(T objeto) throws SQLException;

    void actualizar(T objeto) throws SQLException;

    T buscarPorId(int id) throws SQLException;

    List<T> listar() throws SQLException;
}