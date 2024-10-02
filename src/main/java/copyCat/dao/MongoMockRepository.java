package copyCat.dao;

import copyCat.entities.ApiMock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static copyCat.utils.Constants.MONGO_RECOVERY;
import static copyCat.utils.Constants.RECOVERY_TYPE;

@Repository
@ConditionalOnProperty(name = RECOVERY_TYPE, havingValue = MONGO_RECOVERY)
public interface MongoMockRepository extends MockRepository, MongoRepository<ApiMock,String> {

    @Override
    default Optional<ApiMock> selectByUrl(String url) {
        return Optional.empty();
    }

    @Override
    default List<ApiMock> selectAll() {
        return List.of();
    }

    @Override
    default Optional<ApiMock> selectById(String id) {
        return Optional.empty();
    }

    @Override
    default ApiMock insert(ApiMock item) {
        return null;
    }

    @Override
    default ApiMock update(String id, ApiMock item) {
        return null;
    }

    @Override
    default void remove(String item) {

    }
}
