package com.tradetracker.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String conversationName;
    private String senderBusinessName;
    private String recipientBusinessName;
    private String messageText;
    private LocalDateTime sentAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConversationName() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    public String getSenderBusinessName() {
        return senderBusinessName;
    }

    public void setSenderBusinessName(String senderBusinessName) {
        this.senderBusinessName = senderBusinessName;
    }

    public String getRecipientBusinessName() {
        return recipientBusinessName;
    }

    public void setRecipientBusinessName(String recipientBusinessName) {
        this.recipientBusinessName = recipientBusinessName;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
