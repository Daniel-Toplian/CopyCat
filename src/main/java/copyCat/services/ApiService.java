package copyCat.services;

import copyCat.dao.EntityDao;
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
import java.util.UUID;

@Service
public class ApiService {
    private final Logger LOGGER = LogManager.getLogger(ApiService.class);
    private final EntityDao<ApiMock> DB;
    private final RequestSchedulerService schedulerService;

    @Autowired
    public ApiService(EntityDao<ApiMock> DB, RequestSchedulerService schedulerService) {
        this.DB = DB;
        this.schedulerService = schedulerService;
    }

    public List<ApiMock> getAll() {
        return DB.selectAll();
    }

    public Optional<ApiMock> getById(UUID id) {
        return DB.selectById(id);
    }

    public Optional<ApiMock> getByUrl(String url) {
        return DB.selectAll().stream()
                .filter(apiMock -> url.equals(apiMock.url()))
                .findFirst();
    }

    public void addApi(ApiMock apiMock) throws DataBaseOperationException, InvalidMockCreation {
        validate(apiMock);
        if (isMockExists(apiMock)) {
            throw new DataBaseOperationException("ApiMock is already exists");
        }
        if (isApiRequestPeriodic(apiMock)) {
            schedulerService.startPeriodicRequest(DB.insert(apiMock));
        } else {
            DB.insert(apiMock);
        }

        LOGGER.debug("New MockApi was added successfully");
    }

    public void updateApi(UUID id, ApiMock apiMock) throws DataBaseOperationException, InvalidMockCreation {
        validate(apiMock);
        if (DB.selectById(id).isEmpty()) {
            throw new DataBaseOperationException("Failed to update ApiMock with id: %s, Error: ApiMock is not exists".formatted(id));
        }

        if (apiMock instanceof RestMock) {
            if (isApiRequestPeriodic(apiMock)) {
                schedulerService.startPeriodicRequest(DB.update(id, new RestMock.Builder().from(apiMock).id(id).build()));
            } else {
                DB.update(id, new RestMock.Builder().from(apiMock).id(id).build());
            }
        } else {
            // Todo: implement graphQl mock api option in the future
        }
        LOGGER.debug("Update process for MockApi with id: %s, was added successfully".formatted(id));
    }

    public void deleteApi(UUID id) {
        DB.remove(id);
        schedulerService.cancelPeriodicRequest(id);
        LOGGER.debug("MockApi with id: %s was deleted successfully".formatted(id));
    }

    public void startPeriodicRequest(UUID id) {
        DB.selectById(id).ifPresent(schedulerService::startPeriodicRequest);
    }

    public void cancelPeriodicRequest(UUID id) {
        schedulerService.cancelPeriodicRequest(id);
    }

    public void triggerApiRequest(UUID id) {
        DB.selectById(id).ifPresent(schedulerService::triggerSingularRequest);
    }

    private boolean isMockExists(ApiMock apiMock) {
        return DB.selectAll().stream()
                .anyMatch(mock -> (mock.url().equals(apiMock.url()) &&
                        mock.httpMethod().equals(apiMock.httpMethod())
                        && mock.role().equals(apiMock.role()))
                        || mock.id().equals(apiMock.id()));
    }

    private static boolean isApiRequestPeriodic(ApiMock apiMock) {
        return Role.CLIENT.toString().equals(apiMock.role().toLowerCase()) && apiMock.periodicTrigger().isPresent();
    }

    private void validate(ApiMock mock) throws InvalidMockCreation {
        String BAD_VALIDATION_MESSAGE = "Mock validation was failed. Reason: %s";
        if (mock == null) {
            throw new InvalidMockCreation(BAD_VALIDATION_MESSAGE.formatted("The given ApiMock is null.."));
        }

        if (!Role.SERVER.toString().equals(mock.role().toLowerCase()) &&
                !Role.CLIENT.toString().equals(mock.role().toLowerCase())) {
            throw new InvalidMockCreation(BAD_VALIDATION_MESSAGE.formatted("The desired apiMock has unrecognized role of %s".formatted(mock.role())));
        }

        if (Arrays.stream(HttpMethod.values()).noneMatch(httpMethod -> httpMethod.name().equals(mock.httpMethod().toUpperCase()))) {
            throw new InvalidMockCreation(BAD_VALIDATION_MESSAGE.formatted("The desired apiMock has unrecognized HttpMethod of %s".formatted(mock.role())));
        }

        if ("".equals(mock.url())) {
            throw new InvalidMockCreation(BAD_VALIDATION_MESSAGE.formatted("ApiMock url is empty!"));
        }
    }
}
