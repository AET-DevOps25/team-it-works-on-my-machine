package de.tum.gh_connector.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.tum.gh_connector.client.GHAPIRestClient;
import de.tum.gh_connector.client.GenAIRestClient;
import de.tum.gh_connector.client.UserSRestClient;
import de.tum.gh_connector.dto.*;
import de.tum.gh_connector.dto.gh.ContentResponseItem;
import de.tum.gh_connector.dto.gh.UserAnalysis;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AnalysisService {

    private final GHConnectorService ghConnectorService;
    private final AuthService authService;

    private final GHAPIRestClient ghAPIRestClient;
    private final UserSRestClient userSRestClient;
    private final GenAIRestClient genAIRestClient;

    @Value("${gh.oauth.client.id}")
    private String clientId;

    @Value("${gh.oauth.client.secret}")
    private String clientSecret;

    public AnalysisService(
            GHConnectorService ghConnectorService,
            AuthService authService,
            GHAPIRestClient ghAPIRestClient,
            UserSRestClient userSRestClient,
            GenAIRestClient genAIRestClient) {
        this.ghAPIRestClient = ghAPIRestClient;
        this.userSRestClient = userSRestClient;
        this.genAIRestClient = genAIRestClient;
        this.authService = authService;
        this.ghConnectorService = ghConnectorService;
    }

    public GHConnectorResponse analyzeRepo(String repoUri, String id) {

        log.debug("got called with: " + repoUri);

        WGUser WGUser = authService.getAuthToken(id);
        String bearerToken = WGUser == null ? null : WGUser.getToken();

        try {
            String ownerRepo = constructGHApiContentPath(repoUri);
            String[] ownerRepoParts = ownerRepo.split("/");
            String owner = ownerRepoParts[0];
            String repo = ownerRepoParts[1];
            assertWorkflowDirAccess(owner, repo, bearerToken);

            List<WorkflowFile> yamls = crawlWorkflows(owner, repo, bearerToken);

            GenAIRequest genAIRequest = GenAIRequest.builder().yamls(yamls).build();

            GenAIResponse genAIResponse = genAIRestClient.analyzeYamls(genAIRequest);
            if (WGUser != null) {
                userSRestClient.createAnalysis(
                        id,
                        UserAnalysis.builder()
                                .content(JsonMapper.builder().build().writeValueAsString(genAIResponse.getResults()))
                                .repository(repoUri)
                                .build());
            }
            return GHConnectorResponse.fromGenAIResponse(genAIResponse);

        } catch (IllegalArgumentException | JsonProcessingException e) {
            return ghConnectorService.constructError("There was an error while working with the provided URL: " + e.getMessage());
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

}