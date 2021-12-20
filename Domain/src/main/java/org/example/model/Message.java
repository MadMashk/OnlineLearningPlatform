package org.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.model.constants.MessageStatus;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name = "messages", schema = "senla")
public class Message {
    @Id
    @SequenceGenerator(name = "generator", sequenceName = "sequencemessages", schema = "senla", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @Column(name = "id")
    private Long id;
    @Column(name = "senderId")
    private Integer senderId;
    @Column(name = "recipientId")
    private Integer recipientId;
    @Column(name = "content")
    private String content;
    @Column(name = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "EEE, d MMM yyyy HH:mm:ss")
    private Date time;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MessageStatus messageStatus;

    public Message() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}