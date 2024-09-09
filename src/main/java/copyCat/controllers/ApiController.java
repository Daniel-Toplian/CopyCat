package copyCat.controllers;

import copyCat.entities.ApiMock;
import copyCat.services.ApiService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class ApiController {
    private final Logger LOGGER = LogManager.getLogger(ApiController.class);
    private final ApiService apiService;

    @Autowired
    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @RequestMapping("/**")
    public ResponseEntity<String> replayToRestRequest(HttpServletRequest request) {
        String requestedUrl = request.getRequestURI();
        Optional<ApiMock> api = apiService.getByUrl(requestedUrl);

        return api.map(apiMock -> ResponseEntity.status(apiMock.statusCode()).body(apiMock.response()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("API Mock not found for URL: " + requestedUrl));
    }
}
