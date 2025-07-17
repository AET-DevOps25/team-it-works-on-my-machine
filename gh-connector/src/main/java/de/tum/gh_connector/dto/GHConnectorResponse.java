package de.tum.gh_connector.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.tum.gh_connector.dto.gh.UserInfo;
import de.tum.gh_connector.dto.gh.UserInstallationRepository;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GHConnectorResponse {
    int status;

    @JsonProperty("error_message")
    String errorMessage;

    List<WorkflowExplanation> results;

    @JsonProperty("user_info")
    UserInfo userInfo;

    List<UserInstallationRepository> repos;

    public static GHConnectorResponse fromGenAIResponse(GenAIResponse genAIResponse) {
        return GHConnectorResponse.builder()
                .status(200)
                .results(genAIResponse.getResults())
                .build();
    }
}
