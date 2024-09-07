package copyCat.controllers;

import copyCat.entities.ApiMock;
import copyCat.entities.RestMock;
import copyCat.utils.exceptions.DataBaseOperationException;
import copyCat.services.ApiService;
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
public class CrudApiController {
    private final String REST_MOCK_SUFFIX = "rest";
    private final String GRAPH_QL_MOCK_SUFFIX = "graphql";
    private final String BASE_ROUTE_SUFFIX = "apiMock";
    private final ApiService apiService;

    @Autowired
    public CrudApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("apiMocks")
    public ResponseEntity<List<ApiMock>> getAllMocks() {
        return ResponseEntity.status(HttpStatus.OK).body(apiService.getAll());
    }

    @GetMapping(BASE_ROUTE_SUFFIX + "/{id}")
    public ResponseEntity<Optional<ApiMock>> getMockById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(apiService.getById(id));
    }

    @DeleteMapping(BASE_ROUTE_SUFFIX + "/{id}")
    public ResponseEntity<Void> deleteMock(@PathVariable UUID id) {
        apiService.deleteApi(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(REST_MOCK_SUFFIX)
    public ResponseEntity<String> addMock(@RequestBody RestMock apiMock) {
        try {
            apiService.addApi(apiMock);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (DataBaseOperationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.toString());
        }
    }

    @PutMapping(REST_MOCK_SUFFIX)
    public ResponseEntity<String> replaceMock(@PathVariable UUID id, @RequestBody RestMock apiMock) {
        try {
            apiService.updateApi(id, apiMock);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (DataBaseOperationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
