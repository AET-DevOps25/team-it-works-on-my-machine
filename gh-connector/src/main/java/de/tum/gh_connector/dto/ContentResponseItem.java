package de.tum.gh_connector.dto;

import lombok.Data;

@Data
public class ContentResponseItem {
    private String path;
    private String type;
    private String content;
}
