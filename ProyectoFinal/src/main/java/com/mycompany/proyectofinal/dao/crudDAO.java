package com.mycompany.proyectofinal.dao;

import java.util.List;

/**
 *
 * @author junio
 * @param <T>
 */
public interface crudDAO<T> {
    boolean insertar (T objeto);
    boolean actualizar (T objeto);
    boolean eliminar (int id);
    T buscarporId (int id);
    List<T> listar(); 
}