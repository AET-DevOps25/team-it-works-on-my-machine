package de.tum.gh_connector.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GHConnectorResponse {
    String status;
    String message;
    List<WorkflowFile> files;
}