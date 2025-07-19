package de.tum.gh_connector;

import de.tum.gh_connector.dto.*;
import de.tum.gh_connector.service.AnalysisService;
import de.tum.gh_connector.service.AuthService;
import de.tum.gh_connector.service.GHConnectorService;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class GhConnectorController {

    @Value("${client.url}")
    private String clientUrl;

    private final GHConnectorService ghConnectorService;
    private final AuthService authService;
    private final AnalysisService analysisService;

    public GhConnectorController(
            GHConnectorService ghConnectorService, AuthService authService, AnalysisService analysisService) {
        this.ghConnectorService = ghConnectorService;
        this.authService = authService;
        this.analysisService = analysisService;
    }

    @GetMapping(value = "/oauth/redirect", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> oauthRedirect(@RequestParam String code) {
        String id = authService.performAuth(code);

        if (id == null) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Invalid token response from GitHub.");
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(clientUrl + "/?login=success"));
        String cookieDomain = clientUrl.replaceFirst("^https?://(client.)?", "").replaceFirst(":.*", "");
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
        GHConnectorResponse response = ghConnectorService.getUserInfo(id);
        return wrapGHConnectorResponse(response);
    }

    @GetMapping(value = "/getInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GHConnectorResponse> getInfo(
            @RequestParam String repoUrl, @CookieValue(value = "id", required = false) String id) {
        GHConnectorResponse response = analysisService.analyzeRepo(repoUrl, id);
        return wrapGHConnectorResponse(response);
    }

    @GetMapping(value = "/getPrivateRepos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GHConnectorResponse> getPrivateRepos(@CookieValue(value = "id") String id) {
        GHConnectorResponse response = ghConnectorService.getPrivateRepos(id);
        return wrapGHConnectorResponse(response);
    }

    private ResponseEntity<GHConnectorResponse> wrapGHConnectorResponse(GHConnectorResponse response) {
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

    @GetMapping(value = "/pingusers", produces = MediaType.TEXT_PLAIN_VALUE)
    public String pingUser() {
        return "through GH-Connector: " + ghConnectorService.pingUserS();
    }

    @GetMapping(value = "/pinggenai", produces = MediaType.TEXT_PLAIN_VALUE)
    public String pingGenAI() {
        return "through GH-Connector: " + ghConnectorService.pingGenAI();
    }
}
