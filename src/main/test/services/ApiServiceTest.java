package services;

import copyCat.dao.EntityDao;
import copyCat.entities.ApiMock;
import copyCat.entities.RestMock;
import copyCat.entities.Role;
import copyCat.services.ApiService;
import copyCat.utils.exceptions.DataBaseOperationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ApiServiceTest {

    @Mock
    private EntityDao<ApiMock> mockRepository;

    @InjectMocks
    private ApiService apiService;

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

    @Test
    void testGetAllMocks() {
        when(mockRepository.selectAll()).thenReturn(List.of(apiMock));

        List<ApiMock> result = apiService.getAll();
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).id(), mockId);
    }

    @Test
    void testGetMockById() {
        when(mockRepository.selectById(mockId)).thenReturn(Optional.of(apiMock));

        Optional<ApiMock> result = apiService.getById(mockId);
        assertTrue(result.isPresent());
        assertEquals(result.get().id(), mockId);
    }

    @Test
    void testNoneExistingGetMockById() {
        when(mockRepository.selectById(mockId)).thenReturn(Optional.empty());

        UUID noneExistingId = UUID.randomUUID();
        Optional<ApiMock> result = apiService.getById(noneExistingId);
        assertTrue(result.isEmpty());
    }

    @Test
    void testAddNewApiMock() throws DataBaseOperationException {
        when(mockRepository.selectAll()).thenReturn(Collections.emptyList());
        apiService.addApi(apiMock);
        verify(mockRepository, times(1)).insert(apiMock);
    }

    @Test
    void testAddNewApiMockWithSameUrl() throws DataBaseOperationException {
        when(mockRepository.selectAll()).thenReturn(List.of(apiMock));

        UUID newId = UUID.randomUUID();
        ApiMock newMock = new RestMock.Builder().from(apiMock).role(Role.CLIENT.toString()).id(newId).build();
        apiService.addApi(newMock);
        verify(mockRepository, times(1)).insert(newMock);
    }

    @Test
    void testAddExistingApiMock() throws DataBaseOperationException {
        when(mockRepository.selectAll()).thenReturn(List.of(apiMock));
        assertThrows(DataBaseOperationException.class, () -> apiService.addApi(apiMock));
    }

    @Test
    void testDeleteExistingApiMock(){
        when(mockRepository.selectAll()).thenReturn(List.of(apiMock));
        apiService.deleteApi(mockId);
        verify(mockRepository, times(1)).remove(mockId);
    }

    @Test
    void testDeleteNonExistingApiMock(){
        when(mockRepository.selectAll()).thenReturn(Collections.emptyList());
        apiService.deleteApi(mockId);
        verify(mockRepository, times(1)).remove(mockId);
    }



}
