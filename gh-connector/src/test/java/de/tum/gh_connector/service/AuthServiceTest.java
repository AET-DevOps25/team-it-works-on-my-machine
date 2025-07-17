package de.tum.gh_connector.service;

import de.tum.gh_connector.client.GHAPIRestClient;
import de.tum.gh_connector.client.GHAuthClient;
import de.tum.gh_connector.client.UserSRestClient;
import de.tum.gh_connector.dto.gh.GHAuthResponse;
import de.tum.gh_connector.dto.gh.UserInfo;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthServiceTest {
    @Mock
    private GHAPIRestClient ghAPIRestClient;

    @Mock
    private GHAuthClient ghAuthClient;

    @Mock
    private UserSRestClient userSRestClient;

    @InjectMocks
    private AuthService authService;


    @Test
    void performAuthNoType() {
        GHAuthResponse ghAuthResponse =
                GHAuthResponse.builder().accessToken("token").build();

        when(ghAuthClient.performAuth("code", null, null)).thenReturn(ghAuthResponse);
        assertNull(authService.performAuth("code"));
    }

    @Test
    void performAuthNoToken() {
        GHAuthResponse ghAuthResponse =
                GHAuthResponse.builder().tokenType("Bearer").build();

        when(ghAuthClient.performAuth("code", null, null)).thenReturn(ghAuthResponse);
        assertNull(authService.performAuth("code"));
    }

    @Test
    void performAuthTest() {
        GHAuthResponse ghAuthResponse = GHAuthResponse.builder()
                .tokenType("Bearer")
                .accessToken("token")
                .build();

        when(ghAuthClient.performAuth("code", null, null)).thenReturn(ghAuthResponse);
        when(ghAPIRestClient.getUserInfo("Bearer token")).thenReturn(UserInfo.builder()
                .id("ghid")
                .login("username")
                .build());
        when(userSRestClient.createOrUpdateUser(any())).thenReturn("success");

        assertEquals("success", authService.performAuth("code"));
    }

}
