package com.example.englishvocabulary.models;

public class ChatMessage {

    private long id;
    private long conversationId;
    private String sender;
    private String message;
    private String createdAt;

    public ChatMessage() {
    }

    public ChatMessage(
            long id,
            long conversationId,
            String sender,
            String message,
            String createdAt
    ) {
        this.id = id;
        this.conversationId = conversationId;
        this.sender = sender;
        this.message = message;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}