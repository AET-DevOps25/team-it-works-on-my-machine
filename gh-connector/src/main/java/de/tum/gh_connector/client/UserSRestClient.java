package de.tum.gh_connector.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "UserSRestClient", url = "${users.url}")
public interface UserSRestClient {

    @GetMapping(value = "/ping", produces = MediaType.TEXT_PLAIN_VALUE)
    String ping();

}
