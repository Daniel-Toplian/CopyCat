package copyCat.entities;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Optional;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RestMock.class, name = "rest"),
//        @JsonSubTypes.Type(value = GraphQlMock.class, name = "graphQl")
})
public interface ApiMock {
    UUID id();

    String type();

    String Name(); // Name must be unique

    String httpMethod(); // According to org.springframework.http.HttpMethod

    String url();

    String response();

    String body();

    String role();

    int statusCode();

    Optional<Long> periodicTrigger(); // in milliseconds

    Optional<HostAndPort> destination();
}
