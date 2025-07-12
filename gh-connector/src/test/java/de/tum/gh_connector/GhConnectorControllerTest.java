package de.tum.gh_connector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import de.tum.gh_connector.client.GHAPIRestClient;
import de.tum.gh_connector.client.UserSRestClient;
import de.tum.gh_connector.dto.User;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
class GhConnectorControllerTest {

    @Mock
    private UserSRestClient userSRestClient;

    @Mock
    private GHAPIRestClient ghAPIRestClient;

    @InjectMocks
    private GhConnectorController ghConnectorController;

    private User user;

    @BeforeEach
    void setUp() {
        // if i uncomment this line the mocks don't get replaced correctly.
        // i realize, that this makes no fucking sense, but i cant be bothered to look into it any further.
        //        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .githubId("ghid")
                .id("wgid")
                .username("username")
                .token("token")
                .build();
    }

    @Test
    void testPing() {
        String ret = ghConnectorController.ping();
        assertEquals("Pong from GH-Connector\n", ret);
    }

    @Test
    void getUserNonExistent() {
        assertEquals(ResponseEntity.badRequest().body("Not authenticated"), ghConnectorController.getUser(null));
        assertEquals(
                ResponseEntity.badRequest().body("Not authenticated"), ghConnectorController.getUser("non-existent"));
    }

    @Test
    void getUserExistent() {
        Map<String, Object> userMap = Map.of("userdata", "all the data");
        when(userSRestClient.getUserById(anyString())).thenReturn(user);
        when(ghAPIRestClient.getUserInfo("token")).thenReturn(userMap);

        ResponseEntity<?> ent = ghConnectorController.getUser("wgid");
        assertEquals(ResponseEntity.ok().body(userMap), ent);
    }

    @Test
    void getUserGHError() {
        when(userSRestClient.getUserById("wgid")).thenReturn(user);
        when(ghAPIRestClient.getUserInfo("token")).thenThrow(new RuntimeException("error message"));

        ResponseEntity<?> ent = ghConnectorController.getUser("wgid");
        assertEquals(ResponseEntity.badRequest().body("Error fetching user data from GitHub: error message"), ent);
    }
}
