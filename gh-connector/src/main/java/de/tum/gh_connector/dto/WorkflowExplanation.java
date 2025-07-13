package de.tum.gh_connector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkflowExplanation {

    @JsonProperty("filename")
    String fileName;

    String summary;

    @JsonProperty("related_docs")
    List<String> relatedDocs;

    @JsonProperty("detailed_analysis")
    String detailedAnalysis;
}
