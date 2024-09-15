package copyCat.dao;

import copyCat.entities.ApiMock;
import copyCat.entities.RestMock;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MockRepository implements copyCat.dao.EntityDao<ApiMock> {
    HashMap<UUID, ApiMock> mocks = new HashMap<>();

    public ApiMock insert(ApiMock mock) {
        RestMock newMock = new RestMock.Builder().from(mock).build();
        mocks.put(newMock.id(), newMock);
        return newMock;
    }

    public void remove(UUID mock) {
        mocks.remove(mock);
    }

    public ApiMock update(UUID id, ApiMock updatedMock) {
        mocks.replace(updatedMock.id(), updatedMock);
        return updatedMock;
    }

    @Override
    public Optional<ApiMock> selectById(UUID id) {
        return Optional.ofNullable(mocks.get(id));
    }

    public List<ApiMock> selectAll() {
        return mocks.values().stream().toList();
    }
}
