package de.tum.gh_connector.dto.gh;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class UserInstallations {

    @JsonProperty("total_count")
    int totalCount;

    List<UserInstallation> installations;
}
