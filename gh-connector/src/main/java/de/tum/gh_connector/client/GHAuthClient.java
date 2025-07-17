package de.tum.gh_connector.client;

import de.tum.gh_connector.dto.gh.GHAuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "GHAuthClient", url = "https://github.com", configuration = FeignLogConfig.class)
public interface GHAuthClient {

    @PostMapping(
            value = "/login/oauth/access_token",
            consumes = "application/x-www-form-urlencoded",
            headers = "Accept=application/json")
    GHAuthResponse performAuth(
            @RequestParam("code") String code,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret);
}
