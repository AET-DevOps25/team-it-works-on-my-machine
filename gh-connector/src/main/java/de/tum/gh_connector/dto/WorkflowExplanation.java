package de.tum.gh_connector.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WorkflowExplanation {

    @JsonProperty("filename")
    String fileName;

    String summary;

    @JsonProperty("related_docs")
    List<String> relatedDocs;

    @JsonProperty("detailed_analysis")
    String detailedAnalysis;

}
