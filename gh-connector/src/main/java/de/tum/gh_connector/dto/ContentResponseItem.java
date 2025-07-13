package de.tum.gh_connector.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContentResponseItem {
    private String path;
    private String name;
    private String type;
    private String content;
}
