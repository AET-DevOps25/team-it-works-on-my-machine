package de.tum.gh_connector.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "GenAIRestClient", url = "${genai.url}")
public interface GenAIRestClient {

    @GetMapping(value = "/ping", produces = MediaType.TEXT_PLAIN_VALUE)
    String ping();
}
