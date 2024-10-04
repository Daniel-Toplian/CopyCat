package services;

import copyCat.dao.MockRepository;
import copyCat.entities.ApiMock;
import copyCat.entities.HostAndPort;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ApiServiceTest {

    @Mock
    private MockRepository mockRepository;
    @Mock
    private RequestSchedulerService schedulerService;
    @InjectMocks
    private ApiService apiService;
    private ApiMock apiMock;
    private String mockId;

    private static AutoCloseable closeable;

    @BeforeEach
    public void setup() {
        closeable = MockitoAnnotations.openMocks(this);
        mockId = "mockId";
        apiMock = RestMock.builder()
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
    void testGetMockByUrl(){
        when(mockRepository.selectByUrl(apiMock.url())).thenReturn(Optional.of(apiMock));
        Optional<ApiMock> result = apiService.getByUrl(apiMock.url());
        assertEquals(apiMock, result.get());
    }

    @Test
    void testGetMockByUrlWithNoMatch(){
        when(mockRepository.selectAll()).thenReturn(List.of(apiMock));
        Optional<ApiMock> result = apiService.getByUrl("randomUrl");
        assertTrue(result.isEmpty());
    }

    @Test
    void testNoneExistingGetMockById() {
        when(mockRepository.selectById(mockId)).thenReturn(Optional.empty());

        String noneExistingId = "randomId";
        Optional<ApiMock> result = apiService.getById(noneExistingId);
        assertTrue(result.isEmpty());
    }

    @Test
    void testAddNewServerApiMock() throws DataBaseOperationException, InvalidMockCreation {
        when(mockRepository.selectAll()).thenReturn(Collections.emptyList());
        apiService.addApi(apiMock);
        verify(mockRepository, times(1)).add(apiMock);
    }

    @Test
    void testAddNewClientApiMock() throws DataBaseOperationException, InvalidMockCreation {
        when(mockRepository.selectAll()).thenReturn(Collections.emptyList());
        ApiMock newMock = RestMock.builder().from(apiMock).role(Role.CLIENT.toString()).destination(new HostAndPort("localhost", 80)).build();
        apiService.addApi(newMock);
        verify(mockRepository, times(1)).add(newMock);
    }

    @Test
    void testAddNewApiMockWithUpperCaseRole() throws DataBaseOperationException, InvalidMockCreation {
        when(mockRepository.selectAll()).thenReturn(Collections.emptyList());
        ApiMock newMock = RestMock.builder().from(apiMock).role(Role.SERVER.toString().toUpperCase()).build();
        apiService.addApi(newMock);
        verify(mockRepository, times(1)).add(newMock);
    }

    @Test
    void testAddNewApiMockWithLowerCaseHttpMethod() throws DataBaseOperationException, InvalidMockCreation {
        when(mockRepository.selectAll()).thenReturn(Collections.emptyList());
        ApiMock newMock = RestMock.builder().from(apiMock).httpMethod("get").build();
        apiService.addApi(newMock);
        verify(mockRepository, times(1)).add(newMock);
    }

    @Test
    void testAddNewApiMockWithSameUrlButDifferentRole() throws DataBaseOperationException, InvalidMockCreation {
        when(mockRepository.selectAll()).thenReturn(List.of(apiMock));

        String newId = "newMockId";
        ApiMock newMock = RestMock.builder().from(apiMock).role(Role.CLIENT.toString()).destination(new HostAndPort("localhost", 8080)).id(newId).build();
        apiService.addApi(newMock);
        verify(mockRepository, times(1)).add(newMock);
    }

    @Test
    void testAddNullApiMock() {
        when(mockRepository.selectAll()).thenReturn(Collections.emptyList());
        assertThrows(InvalidMockCreation.class, () -> apiService.addApi(null));
    }

    @Test
    void testAddApiMockWithUnrecognizedRole() {
        when(mockRepository.selectAll()).thenReturn(Collections.emptyList());
        ApiMock newMock = RestMock.builder().from(apiMock).role("unrecognizedRole").build();
        assertThrows(InvalidMockCreation.class, () -> apiService.addApi(newMock));
    }

    @Test
    void testAddApiMockWithUnrecognizedHttpMethod() {
        when(mockRepository.selectAll()).thenReturn(Collections.emptyList());
        ApiMock newMock = RestMock.builder().from(apiMock).httpMethod("unrecognizedMethod").build();
        assertThrows(InvalidMockCreation.class, () -> apiService.addApi(newMock));
    }

    @Test
    void testAddClientMockWithoutDestination(){
        when(mockRepository.selectAll()).thenReturn(Collections.emptyList());
        ApiMock newMock = RestMock.builder().from(apiMock).role(Role.CLIENT.toString()).build();
        assertThrows(InvalidMockCreation.class, () -> apiService.addApi(newMock));
    }

    @Test
    void testAddApiMockWithEmptyUrl() {
        when(mockRepository.selectAll()).thenReturn(Collections.emptyList());
        ApiMock newMock = RestMock.builder().from(apiMock).url("").build();
        assertThrows(InvalidMockCreation.class, () -> apiService.addApi(newMock));
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

    @Test
    void testUpdateWithExistingApiMock() throws DataBaseOperationException, InvalidMockCreation {
        ApiMock updatedMock = RestMock.builder().from(apiMock).body("update test").build();
        when(mockRepository.selectById(mockId)).thenReturn(Optional.of(updatedMock));
        apiService.updateApi(mockId, updatedMock);
        verify(mockRepository, times(1)).update(mockId, updatedMock);
    }

    @Test
    void testUpdateWithNonExistingApiMock() {
        when(mockRepository.selectAll()).thenReturn(List.of(apiMock));
        String newId = "newMockId";
        ApiMock updatedMock = RestMock.builder().from(apiMock).id(newId).body("update test").build();
        assertThrows(DataBaseOperationException.class, () -> apiService.updateApi(newId, updatedMock));
    }
}
