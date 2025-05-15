package de.tum.gh_connector;

import de.tum.gh_connector.dto.ContentResponseItem;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@SpringBootApplication
@RestController
public class GhConnectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(GhConnectorApplication.class, args);
    }

    @Value("${oauth.client-id}")
    private String clientId;

    @Value("${oauth.client-secret}")
    private String clientSecret;

    private final RestClient restClient = RestClient.create();

    @GetMapping(value = "/oauth/redirect", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> oauthRedirect(@RequestParam String code, HttpSession session) {

        Map tokenResponse;

        try {
            String body = "client_id=" + clientId +
                    "&client_secret=" + clientSecret +
                    "&code=" + code;

            tokenResponse = restClient.post()
                    .uri("https://github.com/login/oauth/access_token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error fetching access token from GitHub: " + ex.getMessage());
        }

        String accessToken = (String) tokenResponse.get("access_token");
        String tokenType = (String) tokenResponse.get("token_type");

        if (accessToken == null || tokenType == null) {
            return ResponseEntity.badRequest().body("Invalid token response from GitHub.");
        }

        // Step 2: Use token to fetch user profile
        Map userResponse;
        try {
            userResponse = restClient.get()
                    .uri("https://api.github.com/user")
                    .header("Authorization", tokenType + " " + accessToken)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error fetching user data from GitHub: " + ex.getMessage());
        }

        // ✅ Step 3: Store access token and user info in session
        session.setAttribute("accessToken", accessToken);
        session.setAttribute("tokenType", tokenType);

        Map<String, Object> result = new HashMap<>();
        result.put("userData", userResponse);
        result.put("token", accessToken);
        result.put("tokenType", tokenType);

        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/getInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getInfo(@RequestParam String repoUrl, HttpSession session) {

        String accessToken = (String) session.getAttribute("accessToken");
        String tokenType = (String) session.getAttribute("tokenType");

        System.out.println("got getinfo call " + repoUrl + " " + accessToken + " " + tokenType);

        String uri = repoUrl.replace("github.com", "api.github.com/repos") + "/contents";

        RestClient restClient;
        if (tokenType == null) {
            restClient = RestClient.builder()
                    .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github.raw+json")
                    .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                    .build();
        } else {
            restClient = RestClient.builder()
                    .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github.raw+json")
                    .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                    .defaultHeader("Authorization", tokenType + " " + accessToken)
                    .build();
        }

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
