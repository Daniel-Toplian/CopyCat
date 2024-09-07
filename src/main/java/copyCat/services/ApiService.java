package copyCat.services;

import copyCat.dao.EntityDao;
import copyCat.entities.ApiMock;
import copyCat.entities.RestMock;
import copyCat.entities.Role;
import copyCat.utils.exceptions.DataBaseOperationException;
import copyCat.utils.exceptions.InvalidMockCreation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApiService {
    EntityDao<ApiMock> DB;

    @Autowired
    public ApiService(EntityDao<ApiMock> repository) {
        this.DB = repository;
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
            throw new DataBaseOperationException("Failed to add new ApiMock, Error: ApiMock is already exists");
        }
        DB.insert(apiMock);
    }

    public void updateApi(UUID id, ApiMock apiMock) throws DataBaseOperationException, InvalidMockCreation {
        validate(apiMock);
        if (DB.selectById(id).isEmpty()) {
            throw new DataBaseOperationException("Failed to update ApiMock with id: %s, Error: ApiMock is not exists".formatted(id));
        }

        if (apiMock instanceof RestMock) {
            DB.update(id, new RestMock.Builder().from(apiMock).id(id).build());
        } else {
            // todo: implement graphQl mock api option in the future
        }
    }

    private boolean isMockExists(ApiMock apiMock) {
        return DB.selectAll().stream()
                .anyMatch(mock -> (mock.url().equals(apiMock.url()) &&
                        mock.httpMethod().equals(apiMock.httpMethod())
                        && mock.role().equals(apiMock.role()))
                        || mock.id().equals(apiMock.id()));
    }

    public void deleteApi(UUID id) {
        DB.remove(id);
    }

    private void validate(ApiMock mock) throws InvalidMockCreation {
        if (mock == null) {
            throw new InvalidMockCreation("Invalid mock! mock is null..");
        }

        if (!Role.SERVER.toString().equals(mock.role().toLowerCase()) &&
                !Role.CLIENT.toString().equals(mock.role().toLowerCase())) {
            throw new InvalidMockCreation("Mock validation was failed. Reason: The desired apiMock has unrecognized role of %s".formatted(mock.role()));
        }

        if (Arrays.stream(HttpMethod.values()).noneMatch(httpMethod -> httpMethod.name().equals(mock.httpMethod().toUpperCase()))) {
            throw new InvalidMockCreation("Mock validation was failed. Reason: The desired apiMock has unrecognized HttpMethod of %s".formatted(mock.role()));
        }

        if ("".equals(mock.url())) {
            throw new InvalidMockCreation("Mock validation was failed. Reason: ApiMock url is empty!");
        }
    }
}
