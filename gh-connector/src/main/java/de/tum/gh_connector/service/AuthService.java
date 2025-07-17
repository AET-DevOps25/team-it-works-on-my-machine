package de.tum.gh_connector.service;

import de.tum.gh_connector.client.GHAPIRestClient;
import de.tum.gh_connector.client.GHAuthClient;
import de.tum.gh_connector.client.GenAIRestClient;
import de.tum.gh_connector.client.UserSRestClient;
import de.tum.gh_connector.dto.WGUser;
import de.tum.gh_connector.dto.gh.GHAuthResponse;
import de.tum.gh_connector.dto.gh.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class AuthService {
    private final GHAuthClient ghAuthClient;
    private final GHAPIRestClient ghAPIRestClient;
    private final UserSRestClient userSRestClient;

    @Value("${gh.oauth.client.id}")
    private String clientId;

    @Value("${gh.oauth.client.secret}")
    private String clientSecret;

    public AuthService(
            GHAPIRestClient ghAPIRestClient,
            UserSRestClient userSRestClient,
            GenAIRestClient genAIRestClient,
            GHAuthClient ghAuthClient) {
        this.ghAPIRestClient = ghAPIRestClient;
        this.userSRestClient = userSRestClient;
        this.ghAuthClient = ghAuthClient;
    }

    public String performAuth(String code) {
        GHAuthResponse ghAuthResponse = ghAuthClient.performAuth(code, clientId, clientSecret);
        String accessToken = ghAuthResponse.getAccessToken();
        String tokenType = ghAuthResponse.getTokenType();

        if (accessToken == null || tokenType == null) {
            return null;
        }

        String auth = tokenType + " " + accessToken;

        UserInfo userResponse = ghAPIRestClient.getUserInfo(auth);
        log.info("userResponse: {}", userResponse);

        WGUser wgUser = WGUser.builder()
                .token(auth)
                .githubId(userResponse.getId())
                .username(userResponse.getLogin())
                .build();

        return userSRestClient.createOrUpdateUser(wgUser);
    }

    WGUser getAuthToken(String id) {
        if (id == null) {
            return null;
        }
        try {
            return userSRestClient.getUserById(id);
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Error fetching user data from GitHub: " + ex.getMessage());
        }
    }
}
