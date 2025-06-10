package de.tum.gh_connector.client;

import de.tum.gh_connector.dto.ContentResponseItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(
        name = "GHClient",
        url = "https://api.github.com",
        configuration = GHFeighConfig.class
)
public interface GHRestClient2 {

    @GetMapping(
            value= "{path}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ContentResponseItem getFileContent(@PathVariable("path") String path);

    @GetMapping(
            value= "{path}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    List<ContentResponseItem> getFolderContent(@PathVariable("path") String path);
}