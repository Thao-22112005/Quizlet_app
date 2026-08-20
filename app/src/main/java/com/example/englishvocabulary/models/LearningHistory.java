package com.example.englishvocabulary.models;

public class LearningHistory {

    private int id;
    private int setId;
    private int wordId;
    private int isCorrect;
    private String learningMode;
    private String learnedAt;


    public LearningHistory() {
    }


    public LearningHistory(
            int id,
            int setId,
            int wordId,
            int isCorrect,
            String learningMode,
            String learnedAt) {

        this.id = id;
        this.setId = setId;
        this.wordId = wordId;
        this.isCorrect = isCorrect;
        this.learningMode = learningMode;
        this.learnedAt = learnedAt;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getSetId() {
        return setId;
    }

    public void setSetId(int setId) {
        this.setId = setId;
    }


    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }


    public int getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(int isCorrect) {
        this.isCorrect = isCorrect;
    }


    public String getLearningMode() {
        return learningMode;
    }

    public void setLearningMode(String learningMode) {
        this.learningMode = learningMode;
    }


    public String getLearnedAt() {
        return learnedAt;
    }

    public void setLearnedAt(String learnedAt) {
        this.learnedAt = learnedAt;
    }
}