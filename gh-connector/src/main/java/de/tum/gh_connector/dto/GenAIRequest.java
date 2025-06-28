package de.tum.gh_connector.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class GenAIRequest {
    List<WorkflowFile> yamls;
}
