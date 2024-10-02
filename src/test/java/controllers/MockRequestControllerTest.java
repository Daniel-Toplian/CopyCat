package controllers;

import copyCat.controllers.MockRequestController;
import copyCat.entities.ApiMock;
import copyCat.entities.RestMock;
import copyCat.entities.Role;
import copyCat.services.ApiService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static copyCat.utils.Constants.API_NOT_FOUND;

@ContextConfiguration(classes = MockRequestController.class)
@WebMvcTest(MockRequestController.class)
public class MockRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApiService apiService;

    private final String TEST_URL = "/test";

    @Test
    public void replayToRestRequest_found() throws Exception {
        ApiMock apiMock = new RestMock.Builder()
                .id("mockId")
                .name("MockRequestController-Tests")
                .url(TEST_URL)
                .response("response")
                .role(Role.SERVER.toString())
                .httpMethod(HttpMethod.GET.name())
                .statusCode(200)
                .build();

        Mockito.when(apiService.getByUrl(TEST_URL)).thenReturn(Optional.of(apiMock));

        mockMvc.perform(MockMvcRequestBuilders.get(TEST_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(apiMock.statusCode()))
                .andExpect(MockMvcResultMatchers.content().string(apiMock.response()));
    }

    @Test
    public void replayToNonExistingRestRequest_notFound() throws Exception {
        Mockito.when(apiService.getByUrl(TEST_URL)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get(TEST_URL).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(API_NOT_FOUND.formatted(TEST_URL)));
    }
}
