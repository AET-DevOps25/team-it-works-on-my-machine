package de.tum.gh_connector;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import de.tum.gh_connector.client.GHAPIRestClient;
import de.tum.gh_connector.client.UserSRestClient;
import de.tum.gh_connector.dto.GHConnectorResponse;
import de.tum.gh_connector.dto.WGUser;

import de.tum.gh_connector.dto.gh.UserInfo;
import de.tum.gh_connector.service.GHConnectorService;
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

    @Mock
    GHConnectorService ghConnectorService;

    @InjectMocks
    private GhConnectorController ghConnectorController;

}
