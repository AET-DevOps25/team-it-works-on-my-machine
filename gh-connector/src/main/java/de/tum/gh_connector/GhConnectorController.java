package de.tum.gh_connector;

import de.tum.gh_connector.client.GHAPIRestClient;
import de.tum.gh_connector.client.UserSRestClient;
import de.tum.gh_connector.dto.*;
import de.tum.gh_connector.service.GHConnectorService;
import java.net.URI;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
        String cookieDomain = clientUrl.replaceFirst("^https?://(client.)?", "");
        httpHeaders.add(
                HttpHeaders.SET_COOKIE,
                ResponseCookie.from("id", id)
                        .path("/")
                        .domain(cookieDomain)
                        .build()
                        .toString());
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

    @GetMapping(value = "/getPrivateRepos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserInstallationRepository>> getPrivateRepos(@CookieValue(value = "id") String id) {
        List<UserInstallationRepository> result = ghConnectorService.getPrivateRepos(id);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(result, HttpStatusCode.valueOf(200));
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
