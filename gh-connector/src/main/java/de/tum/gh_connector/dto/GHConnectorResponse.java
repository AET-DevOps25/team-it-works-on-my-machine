package de.tum.gh_connector.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.tum.gh_connector.dto.gh.UserInfo;
import de.tum.gh_connector.dto.gh.UserInstallationRepository;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GHConnectorResponse {
    int status;

    @JsonProperty("error_message")
    String errorMessage;

    Analysis analysis;

    @JsonProperty("user_info")
    UserInfo userInfo;

    List<UserInstallationRepository> repos;

    public static GHConnectorResponse fromGenAIResponse(
            GenAIResponse genAIResponse, String analysisId, String repository, String createdAt) {
        return GHConnectorResponse.builder()
                .status(200)
                .analysis(Analysis.builder()
                        .content(genAIResponse.getResults())
                        .id(analysisId)
                        .repository(repository)
                        .createdAt(createdAt)
                        .build())
                .build();
    }
}
