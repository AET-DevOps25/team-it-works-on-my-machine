package de.tum.gh_connector;

import de.tum.gh_connector.dto.ContentResponseItem;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.List;

@SpringBootApplication
@RestController
public class GhConnectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(GhConnectorApplication.class, args);
    }

    @GetMapping(value = "/getInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getInfo(@RequestParam String repoUrl) {

        String uri = repoUrl.replace("github.com", "api.github.com/repos") + "/contents/.github/workflows";

        RestClient restClient = RestClient.builder()
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github.raw+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .build();

        ResponseEntity<List<ContentResponseItem>> result = restClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        return result.getBody().stream()
                .map(ContentResponseItem::getPath)
                .toList();
    }
}
