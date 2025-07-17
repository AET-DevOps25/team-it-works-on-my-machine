package de.tum.gh_connector;

import static org.junit.jupiter.api.Assertions.*;

import de.tum.gh_connector.client.GHAPIRestClient;
import de.tum.gh_connector.client.UserSRestClient;
import de.tum.gh_connector.service.GHConnectorService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GhConnectorControllerTest {

    @Mock
    private UserSRestClient userSRestClient;

    @Mock
    private GHAPIRestClient ghAPIRestClient;

    @Mock
    GHConnectorService ghConnectorService;

    @InjectMocks
    private GhConnectorController ghConnectorController;
}
