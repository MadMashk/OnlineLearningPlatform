package org.example.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.model.Message;

@Schema(description = "чат-комната")
public class ChatRoomDTO {
    @Schema(description = "имя пользователя получателя", example = "Masha")
    private String recipientName;
    @Schema(description = "последнее сообщение")
    private Message lastMessage;

    public ChatRoomDTO(String recipientName, Message lastMessage) {
        this.recipientName = recipientName;
        this.lastMessage = lastMessage;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}