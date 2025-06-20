package de.tum.gh_connector;

import de.tum.gh_connector.dto.ContentResponseItem;
import de.tum.gh_connector.dto.GHConnectorResponse;
import de.tum.gh_connector.dto.GenAIAskResponse;
import de.tum.gh_connector.service.GHConnectorService;
import jakarta.servlet.http.HttpSession;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
public class GhConnectorController {

    @Value("${gh.oauth.client.id}")
    private String clientId;

    @Value("${gh.oauth.client.secret}")
    private String clientSecret;

    @Value("${genai.url}")
    private String genaiUrl;

    @Value("${client.url}")
    private String clientUrl;

    private final RestClient restClient = RestClient.create();

    private final GHConnectorService ghConnectorService;

    public GhConnectorController(GHConnectorService ghConnectorService) {
        this.ghConnectorService = ghConnectorService;
    }

    @GetMapping(value = "/oauth/redirect", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> oauthRedirect(@RequestParam String code, HttpSession session) {

        Map<String, Object> tokenResponse;

        try {
            String body = "client_id=" + clientId + "&client_secret=" + clientSecret + "&code=" + code;

            tokenResponse = restClient
                    .post()
                    .uri("https://github.com/login/oauth/access_token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .getBody();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error fetching access token from GitHub: " + ex.getMessage());
        }

        String accessToken = (String) tokenResponse.get("access_token");
        String tokenType = (String) tokenResponse.get("token_type");

        if (accessToken == null || tokenType == null) {
            return ResponseEntity.badRequest()
                    .body("Invalid token response from GitHub. "
                            + tokenResponse.entrySet().stream()
                                    .map(entry -> entry.getKey() + ": " + entry.getValue())
                                    .reduce((a, b) -> a + ", " + b)
                                    .orElse(""));
        }

        // ✅ Step 2: Store access token and user info in session
        session.setAttribute("accessToken", accessToken);
        session.setAttribute("tokenType", tokenType);

        // Step 3: Redirect to frontend
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(clientUrl + "/?login=success"));
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUser(HttpSession session) {
        String accessToken = (String) session.getAttribute("accessToken");
        String tokenType = (String) session.getAttribute("tokenType");

        if (accessToken == null || tokenType == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }

        Map<String, Object> userResponse;
        try {
            userResponse = restClient
                    .get()
                    .uri("https://api.github.com/user")
                    .header("Authorization", tokenType + " " + accessToken)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .getBody();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error fetching user data from GitHub: " + ex.getMessage());
        }

        return ResponseEntity.ok(userResponse);
    }

    @GetMapping(value = "/getInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public GenAIAskResponse getInfo(@RequestParam String repoUrl, HttpSession session) {

        String accessToken = (String) session.getAttribute("accessToken");
        String tokenType = (String) session.getAttribute("tokenType");

        System.out.println("got getinfo call " + repoUrl + " " + accessToken + " " + tokenType);

        String uri = repoUrl.replace("github.com", "api.github.com/repos") + "/contents";

        // Create Gitub Rest Client
        RestClient GHRestClient;
        if (tokenType == null) {
            GHRestClient = RestClient.builder()
                    .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github.raw+json")
                    .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                    .build();
        } else {
            GHRestClient = RestClient.builder()
                    .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github.raw+json")
                    .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                    .defaultHeader("Authorization", tokenType + " " + accessToken)
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

    @GetMapping(value = "/getInfo2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GHConnectorResponse> getInfo2(@RequestParam String repoUrl) {
        GHConnectorResponse response = ghConnectorService.analyzeRepo(repoUrl);
        HttpStatus status = HttpStatus.resolve(response.getStatus()); // z.B. 200, 404, 500 etc.

        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR; // Fallback bei ungültigem Statuscode
        }

        return new ResponseEntity<>(response, status);
    }

    @GetMapping(value = "/ping")
    public String ping() {
        return "Pong from GH-Connector\n";
    }

    @GetMapping(value = "/pingusers")
    public String pingUser() {
        return "through GH-Connector: " + ghConnectorService.pingUserS();
    }
    @GetMapping(value = "/pinggenai")
    public String pingGenAI() {
        return "through GH-Connector: " +  ghConnectorService.pingGenAI();
    }
}
