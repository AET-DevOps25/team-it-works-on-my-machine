package de.tum.gh_connector;

import de.tum.gh_connector.client.GHAPIRestClient;
import de.tum.gh_connector.client.UserSRestClient;
import de.tum.gh_connector.dto.*;
import de.tum.gh_connector.service.GHConnectorService;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
public class GhConnectorController {

    private final UserSRestClient userSRestClient;
    private final GHAPIRestClient ghAPIRestClient;

    @Value("${genai.url}")
    private String genaiUrl;

    @Value("${client.url}")
    private String clientUrl;

    private final GHConnectorService ghConnectorService;

    public GhConnectorController(
            GHConnectorService ghConnectorService, UserSRestClient userSRestClient, GHAPIRestClient ghAPIRestClient) {
        this.ghConnectorService = ghConnectorService;
        this.userSRestClient = userSRestClient;
        this.ghAPIRestClient = ghAPIRestClient;
    }

    @GetMapping(value = "/oauth/redirect", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> oauthRedirect(@RequestParam String code) {
        String id = ghConnectorService.performAuth(code);

        if (id == null) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Invalid token response from GitHub.");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(clientUrl + "/?login=success"));
        httpHeaders.add(
                HttpHeaders.SET_COOKIE,
                ResponseCookie.from("id", id).path("/").build().toString());
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUser(@CookieValue(value = "id", required = false) String id) {
        User user = getAuthToken(id);
        if (user == null) {
            return ResponseEntity.badRequest().body("Not authenticated");
        }

        Map<String, Object> userResponse;
        try {
            userResponse = ghAPIRestClient.getUserInfo(user.getToken());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error fetching user data from GitHub: " + ex.getMessage());
        }

        return ResponseEntity.ok(userResponse);
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

    @GetMapping(value = "/getInfo-old", produces = MediaType.APPLICATION_JSON_VALUE)
    public GenAIAskResponse getInfoOld(
            @RequestParam String repoUrl, @CookieValue(value = "id", required = false) String id) {
        User user = getAuthToken(id);

        log.debug("got getinfo call {}", repoUrl);

        String uri = repoUrl.replace("github.com", "api.github.com/repos") + "/contents";

        // Create Gitub Rest Client
        RestClient GHRestClient;
        if (user == null) {
            GHRestClient = RestClient.builder()
                    .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github.raw+json")
                    .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                    .build();
        } else {
            GHRestClient = RestClient.builder()
                    .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github.raw+json")
                    .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                    .defaultHeader("Authorization", user.getToken())
                    .build();
        }

        // Get Repo Contents
        ResponseEntity<List<ContentResponseItem>> repoContents = GHRestClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        // Querry GenAI Service
        String files = repoContents.getBody().stream()
                .map(ContentResponseItem::getPath)
                .collect(Collectors.joining(", "));

        Map<String, Object> genAIRequest = new HashMap<>();
        genAIRequest.put(
                "question",
                "Guess what this code project could be about based on these filenames from the root directory. But write only one sentence: "
                        + files);

        WebClient webClient = WebClient.create(genaiUrl);

        GenAIAskResponse resp = webClient
                .post()
                .uri("/ask")
                .header("Content-Type", "application/json")
                .bodyValue(genAIRequest)
                .retrieve()
                .bodyToMono(GenAIAskResponse.class)
                .block();

        System.out.println(resp.getResponse());
        return resp;
    }

    @GetMapping(value = "/getInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GHConnectorResponse> getInfo(
            @RequestParam String repoUrl, @CookieValue(value = "id", required = false) String id) {
        GHConnectorResponse response = ghConnectorService.analyzeRepo(repoUrl, id);
        HttpStatus status = HttpStatus.resolve(response.getStatus()); // z.B. 200, 404, 500 etc.

        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR; // Fallback bei ungültigem Statuscode
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping(value = "/ping", produces = MediaType.TEXT_PLAIN_VALUE)
    public String ping() {
        return "Pong from GH-Connector\n";
    }

    @GetMapping(value = "/pingusers")
    public String pingUser() {
        return "through GH-Connector: " + ghConnectorService.pingUserS();
    }

    @GetMapping(value = "/pinggenai")
    public String pingGenAI() {
        return "through GH-Connector: " + ghConnectorService.pingGenAI();
    }
}
