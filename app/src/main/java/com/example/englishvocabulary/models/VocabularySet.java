package com.example.englishvocabulary.models;

public class VocabularySet {
    // bo tu vung
    private int id;

    private String userUid;
    private String title;
    private String description;
    private String topic;
    private String level;
    private String coverImage;
    private String createdAt;
    private String updatedAt;

    public VocabularySet() {
    }

    public VocabularySet(int id, String title, String description,
                         String topic, String level, String coverImage,
                         String createdAt, String updatedAt) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.level = level;
        this.coverImage = coverImage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public VocabularySet(int id, String userUid, String title, String description, String topic, String level, String coverImage, String createdAt, String updatedAt) {
        this.id = id;
        this.userUid = userUid;
        this.title = title;
        this.description = description;
        this.topic = topic;
        this.level = level;
        this.coverImage = coverImage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
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