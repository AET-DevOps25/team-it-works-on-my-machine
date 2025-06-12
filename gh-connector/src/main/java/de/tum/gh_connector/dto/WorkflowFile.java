package de.tum.gh_connector.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Base64;

@Data
@Builder
public class WorkflowFile{
    String name;
    String content;

    public static WorkflowFile fromContentResponseItem(ContentResponseItem content) {
//        System.out.println(content.getContent().replace("\n", "").replace("\r", ""));
        byte[] decodedBytes = Base64.getDecoder()
                .decode(content.getContent()
                .replace("\n", "")
                .replace("\r", ""));

        return WorkflowFile.builder()
                .name(content.getPath())
                .content(new String(decodedBytes))
                .build();
    }
}
