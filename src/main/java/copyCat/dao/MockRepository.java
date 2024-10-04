package copyCat.dao;

import copyCat.entities.ApiMock;
import org.springframework.data.repository.query.QueryCreationException;

import java.util.Optional;

public interface MockRepository extends EntityDao<ApiMock>{
    Optional<ApiMock> selectByUrl(String url) throws QueryCreationException;
}
