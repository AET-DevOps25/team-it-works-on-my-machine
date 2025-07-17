package de.tum.gh_connector.client;

import de.tum.gh_connector.dto.WGUser;
import de.tum.gh_connector.dto.gh.UserAnalysis;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "UserSRestClient", url = "${users.url}")
public interface UserSRestClient {

    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    String createOrUpdateUser(@RequestBody WGUser request);

    @GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    WGUser getUserById(@PathVariable("id") String id);

    @GetMapping(value = "/ping", produces = MediaType.TEXT_PLAIN_VALUE)
    String ping();

    @PostMapping(
            value = "/users/{id}/analysis",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    String createAnalysis(@PathVariable("id") String id, @RequestBody UserAnalysis analysis);
}
