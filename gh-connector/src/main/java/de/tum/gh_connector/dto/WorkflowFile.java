package de.tum.gh_connector.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Base64;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkflowFile {

    @JsonProperty("filename")
    String fileName;

    String content;

    public static WorkflowFile fromContentResponseItem(ContentResponseItem content) {
        byte[] decodedBytes = Base64.getDecoder()
                .decode(content.getContent().replace("\n", "").replace("\r", ""));

        return WorkflowFile.builder()
                .fileName(content.getPath())
                .content(new String(decodedBytes))
                .build();
    }
}
