package copyCat.dao;

import java.util.List;
import java.util.Optional;

public interface EntityDao<T> {

    List<T> selectAll();

    Optional<T> selectById(String id);

    T insert(T item);

    T update(String id, T item);

    void remove(String item);
}
