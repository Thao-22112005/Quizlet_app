package com.example.englishvocabulary.models;

public class Word {

    private int id;
    private int setId;
    private String english;
    private String pronunciation;
    private String meaning;
    private String example;
    private String note;
    public int isLearned;

    public Word() {
    }

    public Word(int id, int setId, String english,
                String pronunciation, String meaning,
                String example, String note, int isLearned) {

        this.id = id;
        this.setId = setId;
        this.english = english;
        this.pronunciation = pronunciation;
        this.meaning = meaning;
        this.example = example;
        this.note = note;
        this.isLearned = isLearned;
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

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getIsLearned() {
        return isLearned;
    }

    public void setIsLearned(int isLearned) {
        this.isLearned = isLearned;
    }
}