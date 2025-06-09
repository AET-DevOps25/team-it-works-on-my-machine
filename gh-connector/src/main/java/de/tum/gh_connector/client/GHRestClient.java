package de.tum.gh_connector.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import de.tum.gh_connector.dto.ContentResponseItem;
import de.tum.gh_connector.dto.WorkflowFile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class GHRestClient {

    private final RestClient restClient;

    public GHRestClient(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .build();
    }

    private ResponseEntity<List<ContentResponseItem>> getFolderContent(String path) {
        return restClient.get()
                .uri(path)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    private ResponseEntity<ContentResponseItem> getFileContent(String path) {
        return restClient.get()
                .uri(path)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

    public boolean repoHasWorkflowsDirectory(String repoUrl) {
        URI uri = URI.create(repoUrl);
        String path = "/repos" + uri.getPath() + "/contents/.github/workflows";

        try {
            getFolderContent(path);
            return true;
        } catch (HttpClientErrorException e) {
            return false;
        }
    }

    public List<WorkflowFile> crawlWorkflows(String repoUrl) {
        URI uri = URI.create(repoUrl);
        String contentsPath = "/repos" + uri.getPath() + "/contents";
        String workflows_dir = contentsPath + "/.github/workflows";

        ResponseEntity<List<ContentResponseItem>> folderResponse = getFolderContent(workflows_dir);
        var contents = folderResponse.getBody();

        for (ContentResponseItem item : contents) {
            System.out.println(item.getPath());
        }

        contents = contents.stream()
                .filter(cri ->
                        cri.getType().equals("file")
                                && (cri.getPath().endsWith(".yml")
                                || cri.getPath().endsWith(".yaml")))
                .toList();

        List<WorkflowFile> workflowFiles = new ArrayList<>();

        for (ContentResponseItem item : contents) {

            ResponseEntity<ContentResponseItem> fileResponse;

            try {
                fileResponse = getFileContent(contentsPath + "/" + item.getPath());
            } catch (HttpClientErrorException e) {
                continue;
            }

            ContentResponseItem content = fileResponse.getBody();

            System.out.println(content.getContent().replace("\n", "").replace("\r", ""));
            byte[] decodedBytes = Base64.getDecoder().decode(content.getContent().replace("\n", "").replace("\r", ""));
            String decodedString = new String(decodedBytes);

            WorkflowFile file = WorkflowFile.builder()
                    .name(content.getPath())
                    .content(decodedString)
                    .build();

            workflowFiles.add(file);
        }

        return workflowFiles;
    }
}
