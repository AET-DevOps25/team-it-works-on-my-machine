package de.tum.gh_connector.dto.gh;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserAnalysis {
    private String content;
    private String repository;
}
