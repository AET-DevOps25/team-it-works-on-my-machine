package de.tum.gh_connector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserInstallationRepository {
    String name;

    @JsonProperty("html_url")
    String htmlUrl;

    String visibility;
}
