package de.tum.gh_connector.dto;

import lombok.Data;

import java.util.List;

@Data
public class GenAIResponse {
    List<WorkflowExplanation> results;
}
