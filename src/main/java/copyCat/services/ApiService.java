package copyCat.services;

import copyCat.dao.MockRepository;
import copyCat.entities.ApiMock;
import copyCat.entities.RestMock;
import copyCat.entities.Role;
import copyCat.utils.exceptions.DataBaseOperationException;
import copyCat.utils.exceptions.InvalidMockCreation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ApiService {
    private final Logger LOGGER = LogManager.getLogger(ApiService.class);
    private final MockRepository DB;
    private final RequestSchedulerService schedulerService;

    @Autowired
    public ApiService(MockRepository DB, RequestSchedulerService schedulerService) {
        this.DB = DB;
        this.schedulerService = schedulerService;
    }

    public List<ApiMock> getAll() throws DataBaseOperationException {
        return DB.selectAll();
    }

    public Optional<ApiMock> getById(String id) throws DataBaseOperationException {
        return DB.selectById(id);
    }

    public Optional<ApiMock> getByUrl(String url) {
        return DB.selectByUrl(url);
    }

    public Optional<ApiMock> getServerSideApiMock(String url, String httpMethod, String body) throws DataBaseOperationException {
        return DB.selectAll().stream()
                .filter(apiMock -> Role.SERVER.toString().equals(apiMock.role())
                        && url.equals(apiMock.url())
                        && httpMethod.equals(apiMock.httpMethod())
                        && (body.equals(apiMock.body()))).findFirst();
    }

    public void addApi(ApiMock apiMock) throws DataBaseOperationException, InvalidMockCreation {
        validate(apiMock);
        if (isMockExists(apiMock)) {
            throw new DataBaseOperationException("ApiMock is already exists");
        }
        if (isApiRequestPeriodic(apiMock)) {
            schedulerService.startPeriodicRequest(DB.add(apiMock));
        } else {
            apiMock = DB.add(apiMock);
        }

        LOGGER.debug("New MockApi was added successfully with id: %s".formatted(apiMock.id()));
    }

    public void updateApi(String id, ApiMock apiMock) throws DataBaseOperationException, InvalidMockCreation {
        validate(apiMock);
        if (DB.selectById(id).isEmpty()) {
            throw new DataBaseOperationException("Failed to update ApiMock with id: %s, Error: ApiMock is not exists".formatted(id));
        } else if (!id.equals(apiMock.id())) {
            throw new DataBaseOperationException("Failed to update ApiMock, url-param id: %s is not equal to apiMock's id: %s".formatted(id, apiMock.id()));
        }

        if (apiMock instanceof RestMock) {
            if (isApiRequestPeriodic(apiMock)) {
                schedulerService.startPeriodicRequest(DB.update(id, RestMock.builder().from(apiMock).id(id).build()));
            } else {
                DB.update(id, RestMock.builder().from(apiMock).id(id).build());
            }
        } else {
            // Todo: implement graphQl mock api option in the future
        }
        LOGGER.debug("Update process for MockApi with id: %s, was added successfully".formatted(id));
    }

    public void deleteApi(String id) throws DataBaseOperationException {
        DB.remove(id);
        schedulerService.cancelPeriodicRequest(id);
        LOGGER.debug("MockApi with id: %s was deleted successfully".formatted(id));
    }

    public void startPeriodicRequest(String id) throws DataBaseOperationException {
        DB.selectById(id).ifPresent(schedulerService::startPeriodicRequest);
    }

    public void cancelPeriodicRequest(String id) {
        schedulerService.cancelPeriodicRequest(id);
    }

    public void triggerApiRequest(String id) throws DataBaseOperationException {
        DB.selectById(id).ifPresent(schedulerService::triggerSingularRequest);
    }

    private boolean isMockExists(ApiMock apiMock) throws DataBaseOperationException {
        return DB.selectAll().stream()
                .anyMatch(mock -> (mock.url().equals(apiMock.url())
                        && mock.httpMethod().equals(apiMock.httpMethod())
                        && mock.role().equals(apiMock.role())
                        && mock.body().equals(apiMock.body())
                        && mock.response().equals(apiMock.response()))
                        || mock.id().equals(apiMock.id()));
    }

    private static boolean isApiRequestPeriodic(ApiMock apiMock) {
        return Role.CLIENT.toString().equals(apiMock.role().toLowerCase()) && apiMock.periodicTrigger() != null;
    }

    private void validate(ApiMock mock) throws InvalidMockCreation {
        String BAD_VALIDATION_MESSAGE = "Mock validation was failed. Reason: %s";
        if (mock == null) {
            throw new InvalidMockCreation(BAD_VALIDATION_MESSAGE.formatted("The given ApiMock is null.."));
        }

        if (mock.statusCode() < 100 || mock.statusCode() > 999) {
            throw new InvalidMockCreation(BAD_VALIDATION_MESSAGE
                    .formatted("StatusCode: %s is not valid, it should be a 3 digit number.".formatted(mock.statusCode())));
        }

        if (!Role.SERVER.toString().equals(mock.role().toLowerCase()) &&
                !Role.CLIENT.toString().equals(mock.role().toLowerCase())) {
            throw new InvalidMockCreation(BAD_VALIDATION_MESSAGE.formatted("The desired apiMock has unrecognized role of %s".formatted(mock.role())));
        }

        if (Role.CLIENT.toString().equals(mock.role()) && mock.destination() == null) {
            throw new InvalidMockCreation(BAD_VALIDATION_MESSAGE.formatted("ApiMock with 'Client' role has no destination"));
        }

        if (mock.periodicTrigger() != null && mock.periodicTrigger() < 0) {
            throw new InvalidMockCreation(BAD_VALIDATION_MESSAGE.formatted("ApiMock has negative periodicTrigger value of %s."
                    .formatted(mock.periodicTrigger())));
        }

        if (Arrays.stream(HttpMethod.values()).noneMatch(httpMethod -> httpMethod.name().equals(mock.httpMethod().toUpperCase()))) {
            throw new InvalidMockCreation(BAD_VALIDATION_MESSAGE.formatted("The desired apiMock has unrecognized HttpMethod of %s".formatted(mock.role())));
        }

        if ("".equals(mock.url())) {
            throw new InvalidMockCreation(BAD_VALIDATION_MESSAGE.formatted("ApiMock url is empty!"));
        }
    }
}
