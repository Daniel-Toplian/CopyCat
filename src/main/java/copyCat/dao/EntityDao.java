package copyCat.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EntityDao<T> {

    List<T> selectAll();

    Optional<T> selectById(UUID id);

    T insert(T item);

    T update(UUID id, T item);

    void remove(UUID item);
}
