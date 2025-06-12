package de.tum.gh_connector.service;

import de.tum.gh_connector.client.GHRestClient;
import de.tum.gh_connector.dto.ContentResponseItem;
import de.tum.gh_connector.dto.GHConnectorResponse;
import de.tum.gh_connector.dto.WorkflowFile;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GHConnectorService {

    private final GHRestClient ghRestClient;

    public GHConnectorResponse analyzeRepo(String repoUri) {

        System.out.println("got called with: " + repoUri);

        String contentPath;
        try {
            contentPath = constructGHApiContentPath(repoUri);
            assertWorkflowDirAccess(contentPath);

            return crawlWorkflows(contentPath);
        } catch (IllegalArgumentException e) {
            return constructError("There was an error while working with the provided URL: " + e.getMessage());
        }

//        return constructError("not implemented yet");
    }

    private GHConnectorResponse crawlWorkflows(String contentPath) {
        List<WorkflowFile> workflowFiles = new ArrayList<>();

        try {
            crawlWorkflows(".github/workflows", contentPath, workflowFiles);
        } catch (Exception e) {
            //TODO
            e.printStackTrace();
        }

        return GHConnectorResponse.builder()
                .status("200")
                .files(workflowFiles)
                .build();
    }

    private void crawlWorkflows(String searchPath, String basePath, List<WorkflowFile> resulList) {
        List<ContentResponseItem> contents = ghRestClient.getFolderContent(basePath + searchPath);

        for (ContentResponseItem item : contents) {
            try {

                if (item.getType().equals("dir")) {
                    crawlWorkflows(item.getPath(), basePath, resulList);
                }

                if (item.getType().equals("file")
                        && (item.getName().endsWith(".yml") || item.getName().endsWith(".yaml"))) {
                    ContentResponseItem contentItem = ghRestClient.getFileContent(basePath + item.getPath());
                    resulList.add(WorkflowFile.fromContentResponseItem(contentItem));
                }
            } catch (Exception e) {
                // todo: rate exeeded
                e.printStackTrace();

            }
        }
    }

    private void assertWorkflowDirAccess(String contentPath) throws IllegalArgumentException {
        try {
            ghRestClient.getFolderContent(contentPath);
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("The specified Repository doesn't exist or you are not authorized to access it");
        }

        String workflowsPath = contentPath + "/.github/workflows";

        try {
            ghRestClient.getFolderContent(workflowsPath);
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

        return "/repos/" + pathParts[1] + "/" + pathParts[2] + "/contents/";
    }

    private GHConnectorResponse constructError(String message) {
        return GHConnectorResponse.builder()
                .status("400")
                .message(message)
                .build();
    }
}
