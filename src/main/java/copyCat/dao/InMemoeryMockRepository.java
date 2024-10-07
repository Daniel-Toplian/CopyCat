package copyCat.dao;

import copyCat.entities.ApiMock;
import copyCat.entities.RestMock;
import copyCat.recovery.Recovery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static copyCat.utils.Constants.FILE_RECOVERY;
import static copyCat.utils.Constants.RECOVERY_TYPE;

@Repository
@ConditionalOnProperty(name = RECOVERY_TYPE, havingValue = FILE_RECOVERY)
public class InMemoeryMockRepository implements MockRepository {
    private final Logger LOGGER = LogManager.getLogger(InMemoeryMockRepository.class);
    private final Recovery recovery;
    private HashMap<String, ApiMock> mocks;

    @Autowired
    public InMemoeryMockRepository(Recovery recovery) {
        this.recovery = recovery;
        loadRecovery();
    }

    private void loadRecovery() {
        try {
            this.recovery.fetch()
                    .thenApply(apiMocks -> mocks = (HashMap<String, ApiMock>) apiMocks);
        } catch (Exception e) {
            LOGGER.error("Unable to fetch recovery data, starting empty. Error: %s".formatted(e.getMessage()));
            mocks = new HashMap<>();
        }
    }

    @Override
    public List<ApiMock> selectAll() {
        return mocks.values().stream().toList();
    }

    @Override
    public Optional<ApiMock> selectById(String id) {
        return Optional.ofNullable(mocks.get(id));
    }

    @Override
    public Optional<ApiMock> selectByUrl(String url) {
        return mocks.values().stream().filter(apiMock -> url.equals(apiMock.url())).findFirst();
    }

    @Override
    public ApiMock add(ApiMock mock) {
        RestMock newMock = mock.id() == null ? RestMock.builder().from(mock).id(generateId()).build() : RestMock.builder().from(mock).build();
        mocks.put(newMock.id(), newMock);
        saveToRecovery();
        return newMock;
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public ApiMock update(String id, ApiMock updatedMock) {
        mocks.replace(updatedMock.id(), updatedMock);
        saveToRecovery();
        return updatedMock;
    }

    @Override
    public void remove(String mock) {
        mocks.remove(mock);
        saveToRecovery();
    }

    private void saveToRecovery() {
        try {
            recovery.save(mocks);
        } catch (Exception e) {
            LOGGER.error("Unable to save current ApiMock data to recovery, Error: %s".formatted(e.getMessage()));
        }
    }
}
