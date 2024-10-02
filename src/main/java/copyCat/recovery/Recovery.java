package copyCat.recovery;

import copyCat.entities.ApiMock;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface Recovery {
    CompletableFuture<Map<String,ApiMock>> fetch() throws Exception;

    CompletableFuture<Void> save(Map<String,ApiMock> apiMocks) throws Exception;
}
