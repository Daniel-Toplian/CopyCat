package copyCat.dao;

import copyCat.entities.ApiMock;
import copyCat.entities.RestMock;
import copyCat.recovery.Recovery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MockRepository implements copyCat.dao.EntityDao<ApiMock> {
    private final Logger LOGGER = LogManager.getLogger(MockRepository.class);
    private final Recovery recovery;
    private HashMap<UUID, ApiMock> mocks;

    @Autowired
    public MockRepository(Recovery recovery){
        this.recovery = recovery;
        loadRecovery();
    }

    private void loadRecovery() {
        try {
            this.recovery.fetch()
                    .thenApply(apiMocks -> mocks = (HashMap<UUID, ApiMock>) apiMocks);
        } catch (Exception e) {
            LOGGER.error("Unable to fetch recovery data, starting empty. Error: %s".formatted(e.getMessage()));
            mocks = new HashMap<>();
        }
    }

    public ApiMock insert(ApiMock mock) {
        RestMock newMock = new RestMock.Builder().from(mock).build();
        mocks.put(newMock.id(), newMock);
        saveToRecovery();
        return newMock;
    }

    public void remove(UUID mock) {
        mocks.remove(mock);
        saveToRecovery();
    }

    public ApiMock update(UUID id, ApiMock updatedMock) {
        mocks.replace(updatedMock.id(), updatedMock);
        saveToRecovery();
        return updatedMock;
    }

    @Override
    public Optional<ApiMock> selectById(UUID id) {
        return Optional.ofNullable(mocks.get(id));
    }

    public List<ApiMock> selectAll() {
        return mocks.values().stream().toList();
    }

    private void saveToRecovery(){
        try {
            recovery.save(mocks);
        } catch (Exception e) {
            LOGGER.error("Unable to save current ApiMock data to recovery, Error: %s".formatted(e.getMessage()));
        }
    }
}
