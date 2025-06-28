package de.tum.gh_connector.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GHConnectorResponse {
    int status;
    String message;
    List<WorkflowExplanation> results;

    public static GHConnectorResponse fromGenAIResponse(GenAIResponse genAIResponse) {
        return GHConnectorResponse.builder()
                .status(200)
                .results(genAIResponse.getResults())
                .build();
    }
}
