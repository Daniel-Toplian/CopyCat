package copyCat.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpMethod;

import java.util.Optional;
import java.util.UUID;

public record RestMock(@JsonProperty("id") UUID id,
                       @JsonProperty("type") String type,
                       @JsonProperty("name") String name,
                       @JsonProperty("httpMethod") String httpMethod,
                       @JsonProperty("url") String url,
                       @JsonProperty("response") String response,
                       @JsonProperty("body") String body,
                       @JsonProperty("periodicTrigger") Optional<Long> periodicTrigger,
                       @JsonProperty("role") String role,
                       @JsonProperty("statusCode") int statusCode,
                       @JsonProperty("destination") Optional<HostAndPort> destination) implements ApiMock {

    @Override
    public String Name() {
        return name;
    }

    public String type() {
        return type;
    }

    @Override
    public String httpMethod() {
        return httpMethod;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public String response() {
        return response;
    }

    @Override
    public String body() {
        return body;
    }

    @Override
    public Optional<Long> periodicTrigger() {
        return periodicTrigger;
    }

    @Override
    public String role() {
        return role;
    }

    @Override
    public int statusCode() {
        return statusCode;
    }

    @Override
    public Optional<HostAndPort> destination() {
        return destination;
    }

    public static class Builder {
        private Optional<Long> periodicTrigger = Optional.empty();
        private Optional<HostAndPort> destination = Optional.empty();
        private String httpMethod;
        private String response;
        private int statusCode;
        private String body;
        private String url;
        private String role;
        private String name;
        private UUID id;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder httpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder response(String response) {
            this.response = response;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder periodicTrigger(Long periodicTrigger) {
            this.periodicTrigger = Optional.of(periodicTrigger);
            return this;
        }

        public Builder periodicTrigger(Optional<Long> periodicTrigger) {
            this.periodicTrigger = periodicTrigger;
            return this;
        }

        public Builder destination(Optional<HostAndPort> destination) {
            this.destination = destination;
            return this;
        }

        public Builder destination(HostAndPort destination) {
            this.destination = Optional.of(destination);
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public RestMock build() {
            if (this.httpMethod == null) {
                this.httpMethod = HttpMethod.GET.name();
            }
            if (this.id == null) {
                this.id = UUID.randomUUID();
            }
            if (this.url == null) {
                this.url = "";
            }
            if (this.name == null) {
                this.name = "";
            }
            if (this.statusCode <= 0) {
                this.statusCode = 200;
            }
            if (this.response == null) {
                this.response = "{}";
            }
            if (this.body == null) {
                this.body = "";
            }
            if (this.role == null) {
                this.role = Role.SERVER.toString();
            }

            return new RestMock(id, "rest", name, httpMethod, url, response, body, periodicTrigger, role, statusCode, destination);
        }

        public Builder from(ApiMock mock) {
            return this.httpMethod(mock.httpMethod())
                    .id(mock.id())
                    .name(mock.Name())
                    .url(mock.url())
                    .response(mock.response())
                    .body(mock.body())
                    .periodicTrigger(mock.periodicTrigger())
                    .role(mock.role())
                    .destination(mock.destination())
                    .statusCode(mock.statusCode());
        }
    }
}
