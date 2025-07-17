package de.tum.gh_connector.dto.gh;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserInfo {
    @JsonProperty("avatar_url")
    private String avatarUrl;

    private String login;

    private String id;

    private int followers;

    private int following;

    @JsonProperty("public_repos")
    private int publicRepos;
}