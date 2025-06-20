package de.tum.gh_connector.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    private String id;
    private String githubId;
    private String username;
    private String token;

    public User() {
    }

    public User(String githubId, String username, String token) {
        this.githubId = githubId;
        this.username = username;
        this.token = token;
    }
}
