package copyCat.dao;

import copyCat.entities.ApiMock;
import copyCat.utils.exceptions.DataBaseOperationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.QueryCreationException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static copyCat.utils.Constants.MONGO_RECOVERY;
import static copyCat.utils.Constants.RECOVERY_TYPE;

@Repository
@ConditionalOnProperty(name = RECOVERY_TYPE, havingValue = MONGO_RECOVERY)
public interface MongoMockRepository extends MockRepository, MongoRepository<ApiMock, String> {

    @Override
    default List<ApiMock> selectAll() throws DataBaseOperationException{
        try {
            return findAll();
        } catch (Exception e){
            throw new DataBaseOperationException("An error accrue while trying to fetch all ApiMocks, Error: %s".formatted(e.getMessage()));
        }
    }

    @Override
    @Query("{ 'url' : ?0 }")
    Optional<ApiMock> selectByUrl(String url) throws QueryCreationException;

    @Override
    default Optional<ApiMock> selectById(String id) throws DataBaseOperationException{
        try {
            return findById(id);
        } catch (Exception e){
            throw new DataBaseOperationException("An error accrue while trying to fetch ApiMock with id: %s, Error: %s".formatted(id, e.getMessage()));
        }
    }

    @Override
    default ApiMock add(ApiMock apiMock) throws DataBaseOperationException{
        try {
            return save(apiMock);
        } catch (Exception e){
            throw new DataBaseOperationException("An error accrue while trying to add a new ApiMock, Error: %s".formatted(e.getMessage()));
        }
    }

    @Override
    default ApiMock update(String id, ApiMock apiMock) throws DataBaseOperationException {
        try {
            findById(id).ifPresent(this::delete);
            return save(apiMock);
        } catch (Exception e) {
            throw new DataBaseOperationException("An error accrue while trying to update ApiMock with id: %s, Error: %s".formatted(id, e.getMessage()));
        }
    }

    @Override
    default void remove(String id) throws DataBaseOperationException{
        try {
            findById(id).ifPresent(this::delete);
        } catch (Exception e) {
            throw new DataBaseOperationException("An error accrue while trying to delete ApiMock with id: %s, Error: %s".formatted(id, e.getMessage()));
        }
    }
}
