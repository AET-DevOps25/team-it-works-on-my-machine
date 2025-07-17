package de.tum.gh_connector.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.tum.gh_connector.client.GHAPIRestClient;
import de.tum.gh_connector.dto.*;
import de.tum.gh_connector.dto.gh.*;

import java.util.List;

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
    AuthService authService;

    @InjectMocks
    private GHConnectorService ghConnectorService;

    private WGUser wgUser;

    @BeforeEach
    void setUp() {
        wgUser = wgUser.builder()
                .githubId("ghid")
                .id("wgid")
                .username("username")
                .token("token")
                .build();
    }

    @Test
    void getUserNonExistent() {
        GHConnectorResponse resp = GHConnectorResponse.builder()
                .status(400)
                .errorMessage("Not authenticated")
                .build();

        assertEquals(resp, ghConnectorService.getUserInfo(null));
        assertEquals(resp, ghConnectorService.getUserInfo("non-existent"));
    }

    @Test
    void getUserExistent() {
        UserInfo userInfo = UserInfo.builder()
                .id("ghid")
                .login("username")
                .build();

        GHConnectorResponse resp = GHConnectorResponse.builder()
                .status(200)
                .userInfo(userInfo)
                .build();

        when(authService.getAuthToken("wgid")).thenReturn(wgUser);
        when(ghAPIRestClient.getUserInfo("token")).thenReturn(userInfo);

        GHConnectorResponse ent = ghConnectorService.getUserInfo("wgid");
        assertEquals(resp, ent);
    }

    @Test
    void getUserGHError() {
        GHConnectorResponse resp = GHConnectorResponse.builder()
                .status(400)
                .errorMessage("Error fetching user data from GitHub: error message")
                .build();

        when(authService.getAuthToken("wgid")).thenReturn(wgUser);
        when(ghAPIRestClient.getUserInfo("token")).thenThrow(new RuntimeException("error message"));


        GHConnectorResponse response = ghConnectorService.getUserInfo("wgid");
        assertEquals(resp, response);
    }

    @Test
    void getPrivateReposTest() {
        UserInstallation inst1 = UserInstallation.builder()
                .id(1)
                .build();
        UserInstallation inst2 = UserInstallation.builder()
                .id(2)
                .build();

        UserInstallations installations = UserInstallations.builder()
                .totalCount(2)
                .installations(List.of(inst1, inst2))
                .build();

        UserInstallationRepository repo1 = UserInstallationRepository.builder()
                .htmlUrl("https://github.com/owner1/repo1")
                .name("repo1")
                .visibility("private")
                .build();

        UserInstallationRepository repo2 = UserInstallationRepository.builder()
                .htmlUrl("https://github.com/owner1/repo2")
                .name("repo2")
                .visibility("public")
                .build();

        UserInstallationRepository repo3 = UserInstallationRepository.builder()
                .htmlUrl("https://github.com/owner2/repo3")
                .name("repo3")
                .visibility("private")
                .build();

        UserInstallationRepositories userInstallationRepositories1 = UserInstallationRepositories.builder()
                .totalCount(2)
                .repositories(List.of(repo1, repo2))
                .build();

        UserInstallationRepositories userInstallationRepositories2 = UserInstallationRepositories.builder()
                .totalCount(1)
                .repositories(List.of(repo3))
                .build();

        when(authService.getAuthToken("wgid")).thenReturn(wgUser);
        when(ghAPIRestClient.getUserInstallations("token", 100)).thenReturn(installations);
        when(ghAPIRestClient.getUserInstallationRepositories("token", 1, 100)).thenReturn(userInstallationRepositories1);
        when(ghAPIRestClient.getUserInstallationRepositories("token", 2, 100)).thenReturn(userInstallationRepositories2);

        GHConnectorResponse resp = ghConnectorService.getPrivateRepos("wgid");

        assertEquals(resp, GHConnectorResponse.builder()
                .status(200)
                .repos(List.of(repo1, repo3))
                .build()
        );
    }
}
