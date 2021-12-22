package org.example.model;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "chatrooms", schema = "senla")
public class ChatRoom {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "generator", sequenceName = "sequencechatrooms", schema = "senla", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    private Long id;
    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name="messages_of_room",
            joinColumns = {@JoinColumn(name="chatroom_id", referencedColumnName="id")},
            inverseJoinColumns = {@JoinColumn(name="message_id", referencedColumnName="id")}
    )
    @OrderBy
    private List<Message> messages;
    @Column(name = "senderId")
    private Integer senderId;

    @Column(name = "recipientId")
    private Integer recipientId;

    public ChatRoom() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoom chatRoom = (ChatRoom) o;
        return Objects.equals(id, chatRoom.id) && Objects.equals(messages, chatRoom.messages) && Objects.equals(senderId, chatRoom.senderId) && Objects.equals(recipientId, chatRoom.recipientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, messages, senderId, recipientId);
    }
}
