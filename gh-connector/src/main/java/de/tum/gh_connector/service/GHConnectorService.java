package de.tum.gh_connector.service;

import de.tum.gh_connector.client.GHAPIRestClient;
import de.tum.gh_connector.client.GenAIRestClient;
import de.tum.gh_connector.client.UserSRestClient;
import de.tum.gh_connector.dto.*;
import de.tum.gh_connector.dto.gh.*;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GHConnectorService {

    private final AuthService authService;

    private final GHAPIRestClient ghAPIRestClient;
    private final UserSRestClient userSRestClient;
    private final GenAIRestClient genAIRestClient;

    public GHConnectorService(
            AuthService authService,
            GHAPIRestClient ghAPIRestClient,
            UserSRestClient userSRestClient,
            GenAIRestClient genAIRestClient) {
        this.ghAPIRestClient = ghAPIRestClient;
        this.userSRestClient = userSRestClient;
        this.genAIRestClient = genAIRestClient;
        this.authService = authService;
    }

    GHConnectorResponse constructError(String message) {
        return GHConnectorResponse.builder().status(400).errorMessage(message).build();
    }

    public GHConnectorResponse getUserInfo(String wgID) {
        try {

            WGUser wgUser = authService.getAuthToken(wgID);
            if (wgUser == null) {
                return constructError("Not authenticated");
            }

            UserInfo userResponse;
            try {
                userResponse = ghAPIRestClient.getUserInfo(wgUser.getToken());
            } catch (Exception ex) {
                return constructError("Error fetching user data: " + ex.getMessage());
            }

            return GHConnectorResponse.builder()
                    .status(200)
                    .userInfo(userResponse)
                    .build();
        } catch (Exception e) {
            return constructError("Error fetching user data: " + e.getMessage());
        }
    }

    public GHConnectorResponse getPrivateRepos(String id) {
        try {
            WGUser wgUser = authService.getAuthToken(id);
            if (wgUser == null) {
                return null;
            }

            List<UserInstallationRepository> result = new LinkedList<>();
            UserInstallations userInstallations = ghAPIRestClient.getUserInstallations(wgUser.getToken(), 100);
            for (UserInstallation installation : userInstallations.getInstallations()) {
                UserInstallationRepositories repositories =
                        ghAPIRestClient.getUserInstallationRepositories(wgUser.getToken(), installation.getId(), 100);

                for (UserInstallationRepository repo : repositories.getRepositories()) {
                    if (!repo.getVisibility().equals("public")) {
                        result.add(repo);
                    }
                }
            }

            return GHConnectorResponse.builder().status(200).repos(result).build();
        } catch (Exception e) {
            return constructError("Error fetching data from GitHub: " + e.getMessage());
        }
    }

    public String pingUserS() {
        return userSRestClient.ping();
    }

    public String pingGenAI() {
        return genAIRestClient.ping();
    }
}
