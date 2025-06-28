package de.tum.gh_connector.service;

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
import org.springframework.stereotype.Service;

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

        Map<String, String> userResponse = ghAPIRestClient.getUserInfo(auth);
        log.info("userResponse: {}", userResponse);

        User user = User.builder()
                .token(auth)
                .githubId(userResponse.get("id"))
                .username(userResponse.get("login"))
                .build();

        return userSRestClient.createOrUpdateUser(user);
    }

    public GHConnectorResponse analyzeRepo(String repoUri) {

        System.out.println("got called with: " + repoUri);

        try {
            String ownerRepo = constructGHApiContentPath(repoUri);
            String[] ownerRepoParts = ownerRepo.split("/");
            String owner = ownerRepoParts[0];
            String repo = ownerRepoParts[1];
            assertWorkflowDirAccess(owner, repo);

            return crawlWorkflows(owner, repo);
        } catch (IllegalArgumentException e) {
            return constructError("There was an error while working with the provided URL: " + e.getMessage());
        }
    }

    private GHConnectorResponse crawlWorkflows(String owner, String repo) {
        List<WorkflowFile> workflowFiles = new ArrayList<>();

        try {
            crawlWorkflows(owner, repo, ".github/workflows", workflowFiles);
        } catch (Exception e) {
            // TODO
            e.printStackTrace();
        }

        return GHConnectorResponse.builder().status(200).files(workflowFiles).build();
    }

    private void crawlWorkflows(String owner, String repo, String searchPath, List<WorkflowFile> resulList) {
        List<ContentResponseItem> contents = ghAPIRestClient.getFolderContent(owner, repo, searchPath);

        for (ContentResponseItem item : contents) {
            try {

                if (item.getType().equals("dir")) {
                    crawlWorkflows(owner, repo, item.getPath(), resulList);
                }

                if (item.getType().equals("file")
                        && (item.getName().endsWith(".yml") || item.getName().endsWith(".yaml"))) {
                    ContentResponseItem contentItem = ghAPIRestClient.getFileContent(owner, repo, item.getPath());
                    resulList.add(WorkflowFile.fromContentResponseItem(contentItem));
                }
            } catch (Exception e) {
                // todo: rate exeeded
                e.printStackTrace();
            }
        }
    }

    private void assertWorkflowDirAccess(String owner, String repo) throws IllegalArgumentException {
        try {
            ghAPIRestClient.getFolderContent(owner, repo, "");
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException(
                    "The specified Repository doesn't exist or you are not authorized to access it");
        }

        try {
            ghAPIRestClient.getFolderContent(owner, repo, "/.github/workflows");
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("The specified Repository doesn't have a workflow directory");
        }
    }

    private String constructGHApiContentPath(String repoUri) throws IllegalArgumentException {
        URI uri = URI.create(repoUri);

        if (!"https".equals(uri.getScheme())) {
            throw new IllegalArgumentException("The provided URL is not HTTPS: " + uri);
        }

        if (!"github.com".equals(uri.getHost())) {
            throw new IllegalArgumentException("URL does not point to github.com");
        }

        String path = uri.getPath();
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("URL has no specified path.");
        }

        String[] pathParts = path.split("/");
        if (pathParts.length < 3 || "".equals(pathParts[1]) || "".equals(pathParts[2])) {
            throw new IllegalArgumentException("URL Path is not long enough - The repository is not clear.");
        }

        if (!"".equals(pathParts[0])) {
            throw new IllegalArgumentException("This error should never occur");
        }

        return pathParts[1] + "/" + pathParts[2];
    }

    private GHConnectorResponse constructError(String message) {
        return GHConnectorResponse.builder().status(400).message(message).build();
    }

    public String pingUserS() {
        return userSRestClient.ping();
    }

    public String pingGenAI() {
        return genAIRestClient.ping();
    }
}
