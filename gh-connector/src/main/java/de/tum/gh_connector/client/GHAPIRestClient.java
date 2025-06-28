package de.tum.gh_connector.client;

import de.tum.gh_connector.dto.ContentResponseItem;
import feign.Headers;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@Headers({"X-GitHub-Api-Version: 2022-11-28", "Accept: application/vnd.github+json"})
@FeignClient(name = "GHAPIRestClient", url = "https://api.github.com", configuration = FeignLogConfig.class)
public interface GHAPIRestClient {

    @GetMapping(
            value = "/repos/{owner}/{repo}/contents/{filepath}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ContentResponseItem getFileContent(
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @PathVariable("filepath") String filePath,
            @RequestHeader("Authorization") String bearerToken);

    @GetMapping(
            value = "/repos/{owner}/{repo}/contents/{filepath}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<ContentResponseItem> getFolderContent(
            @PathVariable("owner") String owner,
            @PathVariable("repo") String repo,
            @PathVariable("filepath") String filePath,
            @RequestHeader("Authorization") String bearerToken
    );

    @GetMapping(
            value = "/user",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, String> getUserInfo(@RequestHeader("Authorization") String bearerToken);
}
