package copyCat.services;

import copyCat.entities.ApiMock;
import copyCat.entities.RestMock;
import copyCat.utils.exceptions.InvalidMockCreation;
import io.vertx.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Service
public class RequestSchedulerService {
    private final Logger LOGGER = LogManager.getLogger(RequestSchedulerService.class);
    private final RestTemplate restTemplate;
    private final Map<UUID, Long> activeRequests = new HashMap<>();
    private final Map<UUID, Integer> attemptsCounter = new HashMap<>();
    private final Vertx vertx = Vertx.vertx();

    @Autowired
    public RequestSchedulerService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void startPeriodicRequest(ApiMock apiMock) {
        if (activeRequests.containsKey(apiMock.id())) {
            cancelPeriodicRequest(apiMock.id());
        }
        long periodicId = vertx.setPeriodic(apiMock.periodicTrigger().get(), ignore -> {
            sendApiRequest(apiMock);
        });
        activeRequests.put(apiMock.id(), periodicId);
    }

    public void cancelPeriodicRequest(UUID id) {
        LOGGER.debug("Stopping periodic request for ApiMock with id: %s".formatted(id));
        if (activeRequests.containsKey(id)) {
            vertx.cancelTimer(activeRequests.remove(id));
        }
    }

    public void triggerSingularRequest(ApiMock apiMock) {
        sendApiRequest(apiMock, exception -> {
            LOGGER.debug("Failure in sending request for ApiMock with url: %s, id: %s. Error: %s"
                    .formatted("http://" + apiMock.url(), apiMock.id(), exception.getMessage()));
        });
    }

    private void sendApiRequest(ApiMock apiMock) {
        sendApiRequest(apiMock, exception -> {
            int MAX_FAILED_REQUEST_TRIES = 3;
            attemptsCounter.putIfAbsent(apiMock.id(), 1);
            LOGGER.debug("Failure attempt number: %s in sending periodic request for ApiMock with url: %s, id: %s. Error: %s"
                    .formatted(attemptsCounter.get(apiMock.id()), "http://" + apiMock.url(), apiMock.id(), exception.getMessage()));

            if (attemptsCounter.get(apiMock.id()) > MAX_FAILED_REQUEST_TRIES) {
                cancelPeriodicRequest(apiMock.id());
                return;
            }
            attemptsCounter.replace(apiMock.id(), attemptsCounter.get(apiMock.id()) + 1);
        });
    }

    private void sendApiRequest(ApiMock apiMock, Consumer<Exception> errorHandler) {
        try {
            if (apiMock instanceof RestMock restMock) {
                sendByRest(restMock);
            } else {
                // Todo: Add graphQl logic - sendByGraphQl
            }
        } catch (InvalidMockCreation e) {
            errorHandler.accept(e);
        }
    }

    private void sendByRest(RestMock restMock) throws InvalidMockCreation {
        if (restMock.destination().isEmpty()) {
            throw new InvalidMockCreation("Cannot complete api request. Error: There is no destination for ApiMock with id: %s and name: %s"
                    .formatted(restMock.id(), restMock.name()));
        }
        HttpEntity<String> payload = new HttpEntity<>(restMock.body());
        String url = "http://" + restMock.destination().get() + restMock.url();
        restTemplate.exchange(url, HttpMethod.valueOf(restMock.httpMethod().toUpperCase()), payload, String.class);
    }
}
