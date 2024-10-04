package recovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import copyCat.entities.ApiMock;
import copyCat.entities.RestMock;
import copyCat.recovery.FileRecovery;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class FileRecoveryTest {

    private static final Map<String, ApiMock> mockData = new HashMap<>();;
    private static FileRecovery fileRecovery;
    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void setUp() {
        objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
        fileRecovery = new FileRecovery(objectMapper, "test-recovery.json");

        ApiMock mock = RestMock.builder().url("/test").build();
        mockData.put(mock.id(), mock);
    }

    @Test
    public void testSave_Success() throws Exception {
        CompletableFuture<Void> result = fileRecovery.save(mockData);
        assertNull(result.get());
    }

    @Test
    public void testFetch_Success() throws Exception {
        CompletableFuture<Map<String, ApiMock>> result = fileRecovery.fetch();
        assertEquals(mockData, result.get());
    }

    @Test
    public void testSetupRecoveryFile_FileExists() {
        FileRecovery fileRecovery = new FileRecovery(objectMapper, "test-recovery.json");
        assertNotNull(fileRecovery);
    }
}
