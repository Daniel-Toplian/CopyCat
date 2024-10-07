package recovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import copyCat.entities.ApiMock;
import copyCat.entities.RestMock;
import copyCat.recovery.FileRecovery;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileRecoveryTest {

    private static final Map<String, ApiMock> mockData = new HashMap<>();
    private static final String RECOVERY_FILE_PATH = "src/test/test-recovery.json";
    private static FileRecovery fileRecovery;
    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void setUp() {
        objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
        fileRecovery = new FileRecovery(objectMapper, RECOVERY_FILE_PATH);

        ApiMock mock = RestMock.builder().id("mockId").name("test").url("/test").build();
        mockData.put(mock.id(), mock);
    }

    @AfterAll
    public static void teardown() {
        Path path = Path.of(RECOVERY_FILE_PATH);
        if (path.toFile().exists()) {
            path.toFile().delete();
        }
    }

    @Test
    @Order(1)
    public void testSave_Success() throws Exception {
        CompletableFuture<Void> result = fileRecovery.save(mockData);
        assertNull(result.get());
    }

    @Test
    @Order(2)
    public void testFetch_Success() throws Exception {
        CompletableFuture<Map<String, ApiMock>> result = fileRecovery.fetch();
        assertEquals(mockData, result.get());
    }

    @Test
    @Order(3)
    public void testFetch_EmptyFile_Success() throws Exception {
        Path.of(RECOVERY_FILE_PATH).toFile().delete();
        CompletableFuture<Map<String, ApiMock>> result = fileRecovery.fetch();
        assertEquals(new HashMap<>(), result.get());
    }
}
