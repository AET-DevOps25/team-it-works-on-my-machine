package de.tum.gh_connector.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenAIResponse {
    List<WorkflowExplanation> results;
}
