package de.tum.gh_connector.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.tum.gh_connector.client.GHAPIRestClient;
import de.tum.gh_connector.client.GHAuthClient;
import de.tum.gh_connector.client.GenAIRestClient;
import de.tum.gh_connector.client.UserSRestClient;
import de.tum.gh_connector.dto.*;
import feign.FeignException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class GHConnectorService {

    private final GHAPIRestClient ghAPIRestClient;
    private final GHAuthClient ghAuthClient;

    private final UserSRestClient userSRestClient;
    private final GenAIRestClient genAIRestClient;

    @Value("${gh.oauth.client.id}")
    private String clientId;

    @Value("${gh.oauth.client.secret}")
    private String clientSecret;

    public GHConnectorService(
            GHAPIRestClient ghAPIRestClient,
            UserSRestClient userSRestClient,
            GenAIRestClient genAIRestClient,
            GHAuthClient ghAuthClient) {
        this.ghAPIRestClient = ghAPIRestClient;
        this.userSRestClient = userSRestClient;
        this.genAIRestClient = genAIRestClient;
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

        Map<String, Object> userResponse = ghAPIRestClient.getUserInfo(auth);
        log.info("userResponse: {}", userResponse);

        User user = User.builder()
                .token(auth)
                .githubId(userResponse.get("id").toString())
                .username((String) userResponse.get("login"))
                .build();

        return userSRestClient.createOrUpdateUser(user);
    }

    public GHConnectorResponse analyzeRepo(String repoUri, String id) {

        log.debug("got called with: " + repoUri);

        User user = getAuthToken(id);
        String bearerToken = user == null ? null : user.getToken();

        try {
            String ownerRepo = constructGHApiContentPath(repoUri);
            String[] ownerRepoParts = ownerRepo.split("/");
            String owner = ownerRepoParts[0];
            String repo = ownerRepoParts[1];
            assertWorkflowDirAccess(owner, repo, bearerToken);

            List<WorkflowFile> yamls = crawlWorkflows(owner, repo, bearerToken);

            GenAIRequest genAIRequest = GenAIRequest.builder().yamls(yamls).build();

            GenAIResponse genAIResponse = genAIRestClient.analyzeYamls(genAIRequest);
            if (user != null) {
                userSRestClient.createAnalysis(
                        id,
                        UserAnalysis.builder()
                                .content(JsonMapper.builder().build().writeValueAsString(genAIResponse.getResults()))
                                .repository(repoUri)
                                .build());
            }
            return GHConnectorResponse.fromGenAIResponse(genAIResponse);

        } catch (IllegalArgumentException | JsonProcessingException e) {
            return constructError("There was an error while working with the provided URL: " + e.getMessage());
        }
    }

    private List<WorkflowFile> crawlWorkflows(String owner, String repo, String bearerToken) {
        List<WorkflowFile> workflowFiles = new ArrayList<>();

        try {
            crawlWorkflows(owner, repo, ".github/workflows", workflowFiles, bearerToken);
        } catch (Exception e) {
            // TODO
            e.printStackTrace();
        }

        return workflowFiles;
    }

    private void crawlWorkflows(
            String owner, String repo, String searchPath, List<WorkflowFile> resulList, String bearerToken) {
        List<ContentResponseItem> contents = ghAPIRestClient.getFolderContent(owner, repo, searchPath, bearerToken);

        for (ContentResponseItem item : contents) {
            try {

                if (item.getType().equals("dir")) {
                    crawlWorkflows(owner, repo, item.getPath(), resulList, bearerToken);
                }

                if (item.getType().equals("file")
                        && (item.getName().endsWith(".yml") || item.getName().endsWith(".yaml"))) {
                    ContentResponseItem contentItem =
                            ghAPIRestClient.getFileContent(owner, repo, item.getPath(), bearerToken);
                    resulList.add(WorkflowFile.fromContentResponseItem(contentItem));
                }
            } catch (Exception e) {
                // todo: rate exeeded
                e.printStackTrace();
            }
        }
    }

    private void assertWorkflowDirAccess(String owner, String repo, String bearerToken)
            throws IllegalArgumentException {
        try {
            ghAPIRestClient.getFolderContent(owner, repo, "", bearerToken);
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException(
                    "The specified Repository doesn't exist or you are not authorized to access it");
        }

        try {
            ghAPIRestClient.getFolderContent(owner, repo, ".github/workflows", bearerToken);
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("The specified Repository doesn't have a workflow directory");
        }
    }

    private String constructGHApiContentPath(String repoUri) throws IllegalArgumentException {
        URI uri = URI.create(repoUri);

        if (!"https".equals(uri.getScheme())) {
            throw new IllegalArgumentException("The provided URL is not HTTPS");
        }

        if (!"github.com".equals(uri.getHost())) {
            throw new IllegalArgumentException("URL does not point to github.com");
        }

        String path = uri.getPath();
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("URL has no specified path");
        }

        String[] pathParts = path.split("/");
        if (pathParts.length < 3 || "".equals(pathParts[1]) || "".equals(pathParts[2])) {
            throw new IllegalArgumentException("URL Path is not long enough - The repository is not clear");
        }

        if (!"".equals(pathParts[0])) {
            throw new IllegalArgumentException("This error should never occur");
        }

        return pathParts[1] + "/" + pathParts[2];
    }

    private GHConnectorResponse constructError(String message) {
        return GHConnectorResponse.builder().status(400).message(message).build();
    }

    private User getAuthToken(String id) {
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

    public String pingUserS() {
        return userSRestClient.ping();
    }

    public String pingGenAI() {
        return genAIRestClient.ping();
    }
}
