package copyCat.recovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import copyCat.entities.ApiMock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Component
@ConditionalOnProperty(name = "recovery.type", havingValue = "file")
public class FileRecovery implements Recovery {
    private static final String DEFAULT_PATH = "src/main/resources/recovery.json";
    private final Logger LOGGER = LogManager.getLogger(FileRecovery.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private File recoveryFile;

    @Autowired
    public FileRecovery(ObjectMapper objectMapper, @Value("${recovery.file.path}") String filePath) {
        this.objectMapper = objectMapper;
        setupRecoveryFile(filePath);
    }

    private void setupRecoveryFile(String filePath) {
        recoveryFile = new File("".equals(filePath) || filePath == null ? DEFAULT_PATH : filePath);
        if (!recoveryFile.exists()) {
            try {
                LOGGER.debug("The file on path: %s is not found, creating new file given path.".formatted(recoveryFile.getPath()));
                recoveryFile.createNewFile();
            } catch (IOException e) {
                LOGGER.error("Failed to create new recovery-file, initializing without recovery. Error: %s".formatted(e.getMessage()));
                return;
            }
        }
        LOGGER.info("File-Recovery is initialized with file path: %s".formatted(recoveryFile.getPath()));
    }

    @Async
    @Override
    public CompletableFuture<Map<UUID, ApiMock>> fetch() throws IOException {
        return CompletableFuture.completedFuture(objectMapper.readValue(recoveryFile,
                objectMapper.getTypeFactory().constructMapType(Map.class, UUID.class, ApiMock.class)));
    }

    @Async
    @Override
    public CompletableFuture<Void> save(Map<UUID, ApiMock> apiMocks) throws IOException {
        objectMapper.writeValue(recoveryFile, apiMocks);
        return CompletableFuture.completedFuture(null);
    }
}
