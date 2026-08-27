package com.example.englishvocabulary.models;

public class ChatConversation {

    private long id;
    private String userUid;
    private String title;
    private String createdAt;
    private String updatedAt;

    public ChatConversation() {
    }

    public ChatConversation(
            long id,
            String userUid,
            String title,
            String createdAt,
            String updatedAt
    ) {
        this.id = id;
        this.userUid = userUid;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}