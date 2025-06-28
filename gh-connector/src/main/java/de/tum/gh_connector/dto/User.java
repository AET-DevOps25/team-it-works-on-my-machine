package de.tum.gh_connector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

    private String id;

    @JsonProperty("github_id")
    private String githubId;

    private String username;
    private String token;
}
