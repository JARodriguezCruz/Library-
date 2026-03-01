package com.biblioteca.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * PATRÓN TEMPLATE METHOD / contrato genérico para todos los DAO.
 *
 * Define las operaciones CRUD estándar que cada repositorio debe implementar.
 * Esto garantiza consistencia y permite que el service dependa de la
 * abstracción en lugar de las implementaciones concretas.
 *
 * @param <T>  Tipo de entidad (Autor, Libro, Usuario, Biblioteca)
 * @param <ID> Tipo del identificador (generalmente Integer)
 */
public interface CrudDAO<T, ID> {

    /** Inserta un nuevo registro. Actualiza el campo id del objeto si el driver lo soporta. */
    void crear(T entidad) throws SQLException;

    /** Retorna todos los registros de la tabla correspondiente. */
    List<T> listar() throws SQLException;

    /** Busca un registro por su clave primaria. */
    Optional<T> buscarPorId(ID id) throws SQLException;

    /** Actualiza todos los campos editables del registro. */
    void actualizar(T entidad) throws SQLException;

    /** Elimina el registro con el id indicado. */
    void eliminar(ID id) throws SQLException;
}
