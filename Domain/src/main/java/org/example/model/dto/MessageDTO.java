package org.example.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "сообщение")
public class MessageDTO {
    @Schema(description = "содержание сообщения")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
