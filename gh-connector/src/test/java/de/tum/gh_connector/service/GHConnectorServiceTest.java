package de.tum.gh_connector.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import de.tum.gh_connector.client.GHAPIRestClient;
import de.tum.gh_connector.client.GHAuthClient;
import de.tum.gh_connector.client.GenAIRestClient;
import de.tum.gh_connector.client.UserSRestClient;
import de.tum.gh_connector.dto.*;
import de.tum.gh_connector.dto.gh.ContentResponseItem;
import de.tum.gh_connector.dto.gh.GHAuthResponse;
import de.tum.gh_connector.dto.gh.UserInfo;
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
import org.springframework.http.ResponseEntity;

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
                .message("Not authenticated")
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
                .message("Error fetching user data from GitHub: error message")
                .build();

        when(authService.getAuthToken("wgid")).thenReturn(wgUser);
        when(ghAPIRestClient.getUserInfo("token")).thenThrow(new RuntimeException("error message"));


        GHConnectorResponse response = ghConnectorService.getUserInfo("wgid");
        assertEquals(resp, response);
    }

}
