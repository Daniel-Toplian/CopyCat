package services;

import copyCat.dao.EntityDao;
import copyCat.entities.ApiMock;
import copyCat.entities.RestMock;
import copyCat.entities.Role;
import copyCat.services.ApiService;
import copyCat.services.RequestSchedulerService;
import copyCat.utils.exceptions.DataBaseOperationException;
import copyCat.utils.exceptions.InvalidMockCreation;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class RequestSchedulerServiceTest {

    @InjectMocks
    private RequestSchedulerService schedulerService;
    private ApiMock apiMock;
    private UUID mockId;

    private static AutoCloseable closeable;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        mockId = UUID.randomUUID();
        apiMock = new RestMock.Builder()
                .id(mockId)
                .name("testMock")
                .url("test")
                .role(Role.SERVER.toString())
                .httpMethod(HttpMethod.PUT.name())
                .build();
    }

    @AfterAll
    public static void teardown() throws Exception {
        closeable.close();
    }

//    @Test
//    void RequestSchedulerService() {
//        List<ApiMock> result = apiService.getAll();
//        assertEquals(result.size(), 1);
//        assertEquals(result.get(0).id(), mockId);
//    }
//
//    @Test
//    void stopPeriodicRequest() {
//        when(mockRepository.selectById(mockId)).thenReturn(Optional.of(apiMock));
//
//        Optional<ApiMock> result = apiService.getById(mockId);
//        assertTrue(result.isPresent());
//        assertEquals(result.get().id(), mockId);
//    }
//
//    @Test
//    void testGetMockByUrl(){
//        when(mockRepository.selectAll()).thenReturn(List.of(apiMock));
//        Optional<ApiMock> result = apiService.getByUrl(apiMock.url());
//        assertEquals(apiMock, result.get());
//    }
//
//    @Test
//    void testGetMockByUrlWithNoMatch(){
//        when(mockRepository.selectAll()).thenReturn(List.of(apiMock));
//        Optional<ApiMock> result = apiService.getByUrl("randomUrl");
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    void testNoneExistingGetMockById() {
//        when(mockRepository.selectById(mockId)).thenReturn(Optional.empty());
//
//        UUID noneExistingId = UUID.randomUUID();
//        Optional<ApiMock> result = apiService.getById(noneExistingId);
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    void testAddNewApiMock() throws DataBaseOperationException, InvalidMockCreation {
//        when(mockRepository.selectAll()).thenReturn(Collections.emptyList());
//        apiService.addApi(apiMock);
//        verify(mockRepository, times(1)).insert(apiMock);
//    }
}
