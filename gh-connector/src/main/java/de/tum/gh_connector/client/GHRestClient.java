package de.tum.gh_connector.client;

import de.tum.gh_connector.dto.ContentResponseItem;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "GHClient", url = "https://api.github.com", configuration = GHFeighConfig.class)
public interface GHRestClient {

    @GetMapping(
            value = "/repos/{owner}/{repo}/contents/{filepath}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ContentResponseItem getFileContent(
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @PathVariable("filepath") String filePath);

    @GetMapping(
            value = "/repos/{owner}/{repo}/contents/{filepath}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<ContentResponseItem> getFolderContent(
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @PathVariable("filepath") String filePath);
}
