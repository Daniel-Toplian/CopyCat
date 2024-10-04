package copyCat.entities;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mongodb.lang.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RestMock.class, name = "rest"),
//        @JsonSubTypes.Type(value = GraphQlMock.class, name = "graphQl")
})
@Document("ApiMocks")
public interface ApiMock {
    @Id
    String id();

    String type();

    String name(); // Name must be unique

    String httpMethod(); // According to org.springframework.http.HttpMethod

    String url();

    String response();

    String body();

    String role();

    int statusCode();

    @Nullable
    Long periodicTrigger(); // in milliseconds

    @Nullable
    HostAndPort destination();
}
