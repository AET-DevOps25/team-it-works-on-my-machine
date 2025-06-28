package de.tum.gh_connector.client;

import de.tum.gh_connector.dto.GenAIRequest;
import de.tum.gh_connector.dto.GenAIResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "GenAIRestClient", url = "${genai.url}")
public interface GenAIRestClient {

    @GetMapping(value = "/ping", produces = MediaType.TEXT_PLAIN_VALUE)
    String ping();

    @PostMapping(value = "/analyze-yamls", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    GenAIResponse analyzeYamls(@RequestBody GenAIRequest genAIRequest);
}