package de.tum.gh_connector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Analysis {
    String id;
    String repository;

    @JsonProperty("created_at")
    String createdAt;

    List<WorkflowExplanation> content;
}
