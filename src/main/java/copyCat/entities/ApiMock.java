package copyCat.entities;


import java.util.Optional;
import java.util.UUID;

public interface ApiMock {
    UUID  id();

    String Name(); // Name must be unique

    String httpMethod(); // According to org.springframework.http.HttpMethod

    String url();

    String response();

    String body();

    Optional<Long> periodicTrigger(); // in milliseconds

    String role();

    int statusCode();

    Optional<HostAndPort> destination();
}
