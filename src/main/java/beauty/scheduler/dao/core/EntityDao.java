package beauty.scheduler.dao.core;

import beauty.scheduler.util.ExtendedException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EntityDao<T> {

    Optional<T> getById(int id) throws SQLException, ExtendedException;

    List<T> getAll() throws SQLException, ExtendedException;

    boolean create(T entity) throws SQLException, ExtendedException;

    boolean update(T entity) throws SQLException, ExtendedException;

    boolean delete(T entity) throws SQLException, ExtendedException;
}
