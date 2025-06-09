package de.tum.gh_connector.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkflowFile{
    String name;
    String content;

}