package copyCat.dao;

import copyCat.entities.ApiMock;

import java.util.Optional;

public interface MockRepository extends EntityDao<ApiMock>{
    Optional<ApiMock> selectByUrl(String url);
}
