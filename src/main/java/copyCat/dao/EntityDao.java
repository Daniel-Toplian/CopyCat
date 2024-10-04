package copyCat.dao;

import copyCat.utils.exceptions.DataBaseOperationException;

import java.util.List;
import java.util.Optional;

public interface EntityDao<T> {

    List<T> selectAll() throws DataBaseOperationException;

    Optional<T> selectById(String id) throws DataBaseOperationException;

    T add(T item) throws DataBaseOperationException;

    T update(String id, T item) throws DataBaseOperationException;

    void remove(String item) throws DataBaseOperationException;
}
