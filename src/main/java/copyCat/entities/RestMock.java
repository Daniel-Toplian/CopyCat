package copyCat.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

public record RestMock(@JsonProperty("id") @Id String id,
                       @JsonProperty("name") @Indexed(unique = true) String name,
                       @JsonProperty("type") String type,
                       @JsonProperty("httpMethod") String httpMethod,
                       @JsonProperty("url") String url,
                       @JsonProperty("response") String response,
                       @JsonProperty("body") String body,
                       @JsonProperty("periodicTrigger") Long periodicTrigger,
                       @JsonProperty("role") String role,
                       @JsonProperty("statusCode") int statusCode,
                       @JsonProperty("destination") HostAndPort destination) implements ApiMock {

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private int statusCode = HttpStatus.OK.value();
        private HostAndPort destination;
        private Long periodicTrigger;
        private String httpMethod;
        private String response;
        private String body;
        private String url;
        private String role;
        private String name;
        private String id;

        public Builder id(String id) {
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
            this.periodicTrigger = periodicTrigger;
            return this;
        }

        public Builder destination(HostAndPort destination) {
            this.destination = destination;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public RestMock build() {
            if (httpMethod == null) {
                httpMethod = HttpMethod.GET.name();
            }
            if (id == null) {
                id = "";
            }
            if (url == null) {
                url = "";
            }
            if (name == null) {
                name = "";
            }
            if (statusCode < 100 || statusCode > 999) {
                statusCode = HttpStatus.OK.value();
            }
            if (response == null) {
                response = "{}";
            }
            if (body == null) {
                body = "";
            }
            if (role == null) {
                role = Role.SERVER.toString();
            }
            if(periodicTrigger != null && this.periodicTrigger < 0){
                periodicTrigger = null;
            }

            return new RestMock(id, "rest", name, httpMethod, url, response, body, periodicTrigger, role, statusCode, destination);
        }

        public Builder from(ApiMock mock) {
            return this.httpMethod(mock.httpMethod())
                    .id(mock.id())
                    .name(mock.name())
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
