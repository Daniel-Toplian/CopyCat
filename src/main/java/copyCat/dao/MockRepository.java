package copyCat.dao;

import copyCat.entities.ApiMock;
import copyCat.entities.RestMock;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MockRepository implements EntityDao<ApiMock> {
    HashMap<UUID, ApiMock> mocks = new HashMap<>();

    public MockRepository() {
    }

    public void insert(ApiMock mock) {
        RestMock newMock = new RestMock.Builder().from(mock).build();
        mocks.put(newMock.id(), newMock);
    }

    public void remove(UUID mock) {
        mocks.remove(mock);
    }

    @Override
    public Optional<ApiMock> selectById(UUID id) {
        return Optional.ofNullable(mocks.get(id));
    }

    public void update(UUID id, ApiMock updatedMock) {
        mocks.replace(updatedMock.id(), updatedMock);
    }

    public List<ApiMock> selectAll() {
        return mocks.values().stream().toList();
    }
}
