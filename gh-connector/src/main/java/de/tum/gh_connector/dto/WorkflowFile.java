package de.tum.gh_connector.dto;

import java.util.Base64;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkflowFile {
    String name;
    String content;

    public static WorkflowFile fromContentResponseItem(ContentResponseItem content) {
        byte[] decodedBytes = Base64.getDecoder()
                .decode(content.getContent().replace("\n", "").replace("\r", ""));

        return WorkflowFile.builder()
                .name(content.getPath())
                .content(new String(decodedBytes))
                .build();
    }
}
