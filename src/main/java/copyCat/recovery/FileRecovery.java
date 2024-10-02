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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static copyCat.utils.Constants.FILE_RECOVERY;
import static copyCat.utils.Constants.RECOVERY_TYPE;


@Component
@ConditionalOnProperty(name = RECOVERY_TYPE, havingValue = FILE_RECOVERY)
public class FileRecovery implements Recovery {
    private static final String DEFAULT_PATH = "src/main/resources/recovery.json";
    private final Logger LOGGER = LogManager.getLogger(FileRecovery.class);
    private final ObjectMapper objectMapper;
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
    public CompletableFuture<Map<String, ApiMock>> fetch() {
        try {
            HashMap<String,ApiMock> data = objectMapper.readValue(recoveryFile,
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, ApiMock.class));
            return CompletableFuture.completedFuture(data);
        } catch (Exception e){
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    @Override
    public CompletableFuture<Void> save(Map<String, ApiMock> apiMocks) {
        try {
            objectMapper.writeValue(recoveryFile, apiMocks);
            return CompletableFuture.completedFuture(null);
        } catch (IOException e){
            return CompletableFuture.failedFuture(e);
        }
    }
}
