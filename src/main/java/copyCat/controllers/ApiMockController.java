package copyCat.controllers;

import copyCat.entities.ApiMock;
import copyCat.entities.RestMock;
import copyCat.utils.exceptions.DataBaseOperationException;
import copyCat.services.ApiService;
import copyCat.utils.exceptions.InvalidMockCreation;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static copyCat.utils.Constants.BASE_ROUTE;

@RestController
@Primary
@RequestMapping(path = BASE_ROUTE)

/*
  This controller purpose is to handle CRUD operations including triggering requests as client
 */
public class ApiMockController {
    private final Logger LOGGER = LogManager.getLogger(ApiMockController.class);
    private final String REST_MOCK_SUFFIX = "rest";
    private final String GRAPH_QL_MOCK_SUFFIX = "graphql";
    private final String BASE_ROUTE_SUFFIX = "apiMock";
    private final ApiService apiService;

    @Autowired
    public ApiMockController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("apiMocks")
    public ResponseEntity<List<ApiMock>> getAllMocks() {
        LOGGER.debug("Received getAllMocks request");
        return ResponseEntity.status(HttpStatus.OK).body(apiService.getAll());
    }

    @GetMapping(BASE_ROUTE_SUFFIX + "/{id}")
    public ResponseEntity<Optional<ApiMock>> getMockById(@PathVariable UUID id) {
        LOGGER.debug("Received getMockById request for id: %s".formatted(id));
        return ResponseEntity.status(HttpStatus.OK).body(apiService.getById(id));
    }

    @DeleteMapping(BASE_ROUTE_SUFFIX + "/{id}")
    public ResponseEntity<Void> deleteMock(@PathVariable UUID id) {
        LOGGER.debug("Received deleteMock request for id: %s".formatted(id));
        apiService.deleteApi(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(REST_MOCK_SUFFIX)
    public ResponseEntity<String> addMock(@RequestBody RestMock apiMock) {
        try {
            LOGGER.info("Received new MockApi with the following data: %s".formatted(apiMock.toString())); // check if it prints well or parsing to json is needed
            apiService.addApi(apiMock);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (DataBaseOperationException | InvalidMockCreation e) {
            LOGGER.error("Failed to add new ApiMock, Error: %s".formatted(e.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.toString());
        }
    }

    @PutMapping(REST_MOCK_SUFFIX)
    public ResponseEntity<String> replaceMock(@PathVariable UUID id, @RequestBody RestMock apiMock) {
        try {
            LOGGER.info("Received update for id: %s with the following data: %s".formatted(id, apiMock.toString())); // check if it prints well or parsing to json is needed
            apiService.updateApi(id, apiMock);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (DataBaseOperationException | InvalidMockCreation e) {
            LOGGER.error("Failed to update ApiMock with id: %s, Error: %s".formatted(id, e.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/trigger")
    public ResponseEntity<String> triggerApiRequest(@PathVariable UUID id) {
        LOGGER.info("Received trigger api request on ApiMock with id: %s".formatted(id));
        apiService.triggerApiRequest(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/trigger/periodic/start")
    public ResponseEntity<String> startPeriodicRequests(@PathVariable UUID id) {
        LOGGER.info("Received start send periodically request on ApiMock with id: %s".formatted(id));
        apiService.startPeriodicRequest(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/trigger/periodic/stop")
    public ResponseEntity<String> stopPeriodicRequests(@PathVariable UUID id) {
        LOGGER.info("Received stop send periodically request on ApiMock with id: %s".formatted(id));
        apiService.cancelPeriodicRequest(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
