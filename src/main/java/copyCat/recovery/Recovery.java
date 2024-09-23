package copyCat.recovery;

import copyCat.entities.ApiMock;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Recovery {
    CompletableFuture<Map<UUID,ApiMock>> fetch() throws Exception;

    CompletableFuture<Void> save(Map<UUID,ApiMock> apiMocks) throws Exception;
}
