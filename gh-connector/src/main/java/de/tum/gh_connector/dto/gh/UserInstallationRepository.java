package de.tum.gh_connector.dto.gh;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInstallationRepository {
    String name;

    @JsonProperty("html_url")
    String htmlUrl;

    String visibility;
}
