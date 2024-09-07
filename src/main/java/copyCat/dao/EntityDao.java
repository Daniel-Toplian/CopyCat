package copyCat.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EntityDao<T> {

    void insert(T item);

    void update(UUID id, T item);

    void remove(UUID item);

    Optional<T> selectById(UUID id);

    List<T> selectAll();
}
