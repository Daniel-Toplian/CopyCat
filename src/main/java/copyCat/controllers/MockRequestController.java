package copyCat.controllers;

import copyCat.entities.ApiMock;
import copyCat.services.ApiService;
import copyCat.utils.exceptions.DataBaseOperationException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

import static copyCat.utils.Constants.API_NOT_FOUND;

@RestController
public class MockRequestController {
    private final Logger LOGGER = LogManager.getLogger(MockRequestController.class);
    private final ApiService apiService;

    @Autowired
    public MockRequestController(ApiService apiService) {
        this.apiService = apiService;
    }

    @RequestMapping("/**")
    public ResponseEntity<String> replay(HttpServletRequest request) {
        String requestedUrl = request.getRequestURI();
        LOGGER.debug("Received request for: {}", requestedUrl);

        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            Optional<ApiMock> api = apiService.getServerSideApiMock(requestedUrl, request.getMethod(), body);
            return api.map(apiMock -> ResponseEntity.status(apiMock.statusCode()).body(apiMock.response()))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(API_NOT_FOUND.formatted(requestedUrl)));
        } catch (IOException | DataBaseOperationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
