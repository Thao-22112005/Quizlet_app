package com.example.englishvocabulary.models;

import android.speech.tts.TextToSpeech;

import java.util.List;

public class SpeakWord {
    public void speakWord(TextToSpeech textToSpeech, List<Word> wordList, int currentPosition) {

        if (currentPosition >=
                wordList.size()) {

            return;
        }


        Word word =
                wordList.get(currentPosition);


        String english =
                word.getEnglish();


        if (textToSpeech != null) {

            textToSpeech.speak(
                    english,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
            );
        }
    }

}
