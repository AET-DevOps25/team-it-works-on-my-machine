package de.tum.gh_connector.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.tum.gh_connector.client.GHAPIRestClient;
import de.tum.gh_connector.client.GHAuthClient;
import de.tum.gh_connector.client.GenAIRestClient;
import de.tum.gh_connector.client.UserSRestClient;
import de.tum.gh_connector.dto.*;
import de.tum.gh_connector.dto.gh.ContentResponseItem;
import de.tum.gh_connector.dto.gh.GHAuthResponse;
import feign.FeignException;
import feign.Request;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GHConnectorServiceTest {

    @Mock
    private GHAPIRestClient ghAPIRestClient;

    @Mock
    private GHAuthClient ghAuthClient;

    @Mock
    private UserSRestClient userSRestClient;

    @Mock
    private GenAIRestClient genAIRestClient;

    @InjectMocks
    private GHConnectorService ghConnectorService;

    private WGUser WGUser;

    ContentResponseItem item1 = ContentResponseItem.builder()
            .name("item1.yml")
            .path(".github/workflows/item1.yml")
            .type("file")
            .content("Y29udGVudDE=") // this needs to be base64 encoded - it reads "content1"
            .build();

    ContentResponseItem item2 = ContentResponseItem.builder()
            .name("item2.yaml")
            .path(".github/workflows/item2.yaml")
            .type("file")
            .content("Y29udGVudDI=") // "content2"
            .build();

    ContentResponseItem dir = ContentResponseItem.builder()
            .name("dir")
            .path(".github/workflows/dir")
            .type("dir")
            .build();

    ContentResponseItem item3 = ContentResponseItem.builder()
            .name("item3.yml")
            .path(".github/workflows/dir/item3.yml")
            .type("file")
            .content("Y29udGVudDM=") // "content3"
            .build();

    GenAIRequest genAIRequest = GenAIRequest.builder()
            .yamls(List.of(
                    WorkflowFile.builder()
                            .fileName(".github/workflows/item1.yml")
                            .content("content1")
                            .build(),
                    WorkflowFile.builder()
                            .fileName(".github/workflows/item2.yaml")
                            .content("content2")
                            .build(),
                    WorkflowFile.builder()
                            .fileName(".github/workflows/dir/item3.yml")
                            .content("content3")
                            .build()))
            .build();

    GenAIResponse genAIResponse = GenAIResponse.builder()
            .results(List.of(WorkflowExplanation.builder()
                    .fileName(".github/workflows/item1.yml")
                    .summary("summary1")
                    .detailedAnalysis("analysis1")
                    .relatedDocs(List.of())
                    .build()))
            .build();

    GHConnectorResponse ghConnectorResponse = GHConnectorResponse.builder()
            .status(200)
            .results(List.of(WorkflowExplanation.builder()
                    .fileName(".github/workflows/item1.yml")
                    .summary("summary1")
                    .detailedAnalysis("analysis1")
                    .relatedDocs(List.of())
                    .build()))
            .build();

    @BeforeEach
    void setUp() {
        WGUser = WGUser.builder()
                .githubId("ghid")
                .id("wgid")
                .username("username")
                .token("token")
                .build();
    }

    @Test
    void performAuthNoType() {
        GHAuthResponse ghAuthResponse =
                GHAuthResponse.builder().accessToken("token").build();

        when(ghAuthClient.performAuth("code", null, null)).thenReturn(ghAuthResponse);
        assertNull(ghConnectorService.performAuth("code"));
    }

    @Test
    void performAuthNoToken() {
        GHAuthResponse ghAuthResponse =
                GHAuthResponse.builder().tokenType("Bearer").build();

        when(ghAuthClient.performAuth("code", null, null)).thenReturn(ghAuthResponse);
        assertNull(ghConnectorService.performAuth("code"));
    }

    @Test
    void performAuthTest() {
        GHAuthResponse ghAuthResponse = GHAuthResponse.builder()
                .tokenType("Bearer")
                .accessToken("token")
                .build();

        when(ghAuthClient.performAuth("code", null, null)).thenReturn(ghAuthResponse);
        when(ghAPIRestClient.getUserInfo("Bearer token")).thenReturn(Map.of("id", "id", "username", "username"));
        when(userSRestClient.createOrUpdateUser(any())).thenReturn("success");

        assertEquals("success", ghConnectorService.performAuth("code"));
    }

    @Test
    void analyzeRepoTest() {
        when(ghAPIRestClient.getFolderContent("ls1intum", "Artemis", ".github/workflows", null))
                .thenReturn(List.of(item1, item2, dir));
        when(ghAPIRestClient.getFolderContent("ls1intum", "Artemis", ".github/workflows/dir", null))
                .thenReturn(List.of(item3));

        when(ghAPIRestClient.getFileContent("ls1intum", "Artemis", ".github/workflows/item1.yml", null))
                .thenReturn(item1);
        when(ghAPIRestClient.getFileContent("ls1intum", "Artemis", ".github/workflows/item2.yaml", null))
                .thenReturn(item2);
        when(ghAPIRestClient.getFileContent("ls1intum", "Artemis", ".github/workflows/dir/item3.yml", null))
                .thenReturn(item3);

        when(genAIRestClient.analyzeYamls(genAIRequest)).thenReturn(genAIResponse);

        assertEquals(ghConnectorResponse, ghConnectorService.analyzeRepo("https://github.com/ls1intum/Artemis", "id"));
    }

    @Test
    void construkctContentPathTest() {
        assertEquals(
                GHConnectorResponse.builder()
                        .status(400)
                        .message(
                                "There was an error while working with the provided URL: The provided URL is not HTTPS")
                        .build(),
                ghConnectorService.analyzeRepo("http://github.com/ls1intum/Artemis", "id"));
        assertEquals(
                GHConnectorResponse.builder()
                        .status(400)
                        .message(
                                "There was an error while working with the provided URL: URL does not point to github.com")
                        .build(),
                ghConnectorService.analyzeRepo("https://gitlab.com/ls1intum/Artemis", "id"));
        assertEquals(
                GHConnectorResponse.builder()
                        .status(400)
                        .message("There was an error while working with the provided URL: URL has no specified path")
                        .build(),
                ghConnectorService.analyzeRepo("https://github.com", "id"));
        assertEquals(
                GHConnectorResponse.builder()
                        .status(400)
                        .message(
                                "There was an error while working with the provided URL: URL Path is not long enough - The repository is not clear")
                        .build(),
                ghConnectorService.analyzeRepo("https://github.com/Artemis", "id"));
    }

    @Test
    void testInvalidRepo() {
        Map<String, Collection<String>> myMap = new HashMap<>();
        FeignException.NotFound notFound = new FeignException.NotFound(
                null, Request.create(Request.HttpMethod.GET, "", myMap, (byte[]) null, null), null, null);
        when(ghAPIRestClient.getFolderContent("hello", "world", "", null)).thenThrow(notFound);

        assertEquals(
                GHConnectorResponse.builder()
                        .status(400)
                        .message(
                                "There was an error while working with the provided URL: The specified Repository doesn't exist or you are not authorized to access it")
                        .build(),
                ghConnectorService.analyzeRepo("https://github.com/hello/world", "id"));
    }

    @Test
    void testNoWorkflowDir() {
        Map<String, Collection<String>> myMap = new HashMap<>();
        FeignException.NotFound notFound = new FeignException.NotFound(
                null, Request.create(Request.HttpMethod.GET, "", myMap, (byte[]) null, null), null, null);
        when(ghAPIRestClient.getFolderContent("hello", "world", ".github/workflows", null))
                .thenThrow(notFound);

        assertEquals(
                GHConnectorResponse.builder()
                        .status(400)
                        .message(
                                "There was an error while working with the provided URL: The specified Repository doesn't have a workflow directory")
                        .build(),
                ghConnectorService.analyzeRepo("https://github.com/hello/world", "id"));
    }
}
