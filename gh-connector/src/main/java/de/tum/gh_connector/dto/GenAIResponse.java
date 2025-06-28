package de.tum.gh_connector.dto;

import java.util.List;
import lombok.Data;

@Data
public class GenAIResponse {
    List<WorkflowExplanation> results;
}
