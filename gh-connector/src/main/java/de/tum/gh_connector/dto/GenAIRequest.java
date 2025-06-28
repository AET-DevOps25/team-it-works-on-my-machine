package de.tum.gh_connector.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GenAIRequest {
    List<WorkflowFile> yamls;
}
