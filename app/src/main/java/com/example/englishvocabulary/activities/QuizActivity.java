package com.example.englishvocabulary.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.englishvocabulary.R;
import com.example.englishvocabulary.database.LearningHistoryDAO;
import com.example.englishvocabulary.database.WordDAO;
import com.example.englishvocabulary.models.LearningHistory;
import com.example.englishvocabulary.models.SpeakWord;
import com.example.englishvocabulary.models.Word;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    private ImageButton btnBack;

    private TextView tvProgress;
    private TextView tvQuestion;

    private TextView tvAnswer1;
    private TextView tvAnswer2;
    private TextView tvAnswer3;
    private TextView tvAnswer4;

    private ProgressBar progressBar;

    private TextToSpeech textToSpeech;

    private WordDAO wordDAO;

    private LearningHistoryDAO learningHistoryDAO;

    private List<Word> wordList;

    private int currentPosition = 0;


    // ID BỘ TỪ
    private int setId;

    private String correctAnswer;

    private int correctCount = 0;

    private int wrongCount = 0;

    private boolean answerSelected = false;


    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_quiz
        );

        // NHẬN ID BỘ TỪ
        setId =
                getIntent().getIntExtra(
                        "set_id",
                        -1
                );

        btnBack =
                findViewById(
                        R.id.btnBack
                );

        tvProgress =
                findViewById(
                        R.id.tvProgress
                );

        tvQuestion =
                findViewById(
                        R.id.tvQuestion
                );

        tvAnswer1 =
                findViewById(
                        R.id.tvAnswer1
                );

        tvAnswer2 =
                findViewById(
                        R.id.tvAnswer2
                );

        tvAnswer3 =
                findViewById(
                        R.id.tvAnswer3
                );

        tvAnswer4 =
                findViewById(
                        R.id.tvAnswer4
                );

        progressBar =
                findViewById(
                        R.id.progressBar
                );

        // DATABASE
        wordDAO =
                new WordDAO(this);

        learningHistoryDAO =
                new LearningHistoryDAO(this);


        // KIỂM TRA ID BỘ TỪ
        if (setId == -1) {

            Toast.makeText(
                    this,
                    "Không tìm thấy bộ từ",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        // LẤY DANH SÁCH TỪ
        wordList =
                wordDAO.getBySetId(
                        setId
                );


        // KIỂM TRA DỮ LIỆU

        if (wordList == null ||
                wordList.size() < 4) {

            Toast.makeText(
                    this,
                    "Cần ít nhất 4 từ để làm trắc nghiệm",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }

        // HIỂN THỊ CÂU ĐẦU TIÊN
        showCurrentQuestion();

        btnBack.setOnClickListener(v -> {

            finish();

        });
        // TEXT TO SPEECH
        textToSpeech =
                new TextToSpeech(
                        this,
                        status -> {

                            if (status ==
                                    TextToSpeech.SUCCESS) {

                                textToSpeech.setLanguage(
                                        Locale.US
                                );
                            }
                        }
                );

        tvAnswer1.setOnClickListener(v -> {

            checkAnswer(
                    tvAnswer1
            );

        });

        tvAnswer2.setOnClickListener(v -> {

            checkAnswer(
                    tvAnswer2
            );

        });

        tvAnswer3.setOnClickListener(v -> {

            checkAnswer(
                    tvAnswer3
            );

        });

        tvAnswer4.setOnClickListener(v -> {

            checkAnswer(
                    tvAnswer4
            );

        });
    }

    // HIỂN THỊ CÂU HỎI HIỆN TẠI
    private void showCurrentQuestion() {

        if (currentPosition >=
                wordList.size()) {

            showResult();

            return;
        }


        // Cho phép chọn đáp án mới

        answerSelected = false;


        // LẤY TỪ HIỆN TẠI
        Word currentWord =
                wordList.get(
                        currentPosition
                );

        // HIỂN THỊ CÂU HỎI
        tvQuestion.setText(
                currentWord.getEnglish()
        );


        // ĐÁP ÁN ĐÚNG
        correctAnswer =
                currentWord.getMeaning();

        // TẠO DANH SÁCH ĐÁP ÁN
        List<String> answers =
                new ArrayList<>();


        // Thêm đáp án đúng

        answers.add(
                correctAnswer
        );


        // LẤY CÁC TỪ KHÁC
        List<Word> otherWords =
                new ArrayList<>();


        for (Word word : wordList) {

            if (word.getId() !=
                    currentWord.getId()) {

                otherWords.add(word);
            }
        }


        // TRỘN CÁC TỪ KHÁC
        Collections.shuffle(
                otherWords
        );

        // LẤY 3 ĐÁP ÁN SAI
        for (Word word : otherWords) {

            if (answers.size() >= 4) {

                break;
            }


            String meaning =
                    word.getMeaning();


            if (meaning != null &&
                    !meaning.isEmpty() &&
                    !answers.contains(meaning)) {

                answers.add(
                        meaning
                );
            }
        }

        // TRỘN ĐÁP ÁN
        Collections.shuffle(
                answers
        );

        // HIỂN THỊ ĐÁP ÁN
        setAnswerText(
                tvAnswer1,
                "A",
                answers.get(0)
        );

        setAnswerText(
                tvAnswer2,
                "B",
                answers.get(1)
        );

        setAnswerText(
                tvAnswer3,
                "C",
                answers.get(2)
        );

        setAnswerText(
                tvAnswer4,
                "D",
                answers.get(3)
        );

        resetAnswerStyle(
                tvAnswer1
        );

        resetAnswerStyle(
                tvAnswer2
        );

        resetAnswerStyle(
                tvAnswer3
        );

        resetAnswerStyle(
                tvAnswer4
        );

        tvProgress.setText(
                (currentPosition + 1)
                        + "/"
                        + wordList.size()
        );


        // THANH TIẾN ĐỘ
        int progress =
                (int) (
                        ((currentPosition + 1)
                                * 100.0)
                                / wordList.size()
                );


        progressBar.setProgress(
                progress
        );
    }

    private void setAnswerText(
            TextView textView,
            String letter,
            String answer) {

        textView.setText(
                letter
                        + ".   "
                        + answer
        );
    }


    private void checkAnswer(
            TextView selectedAnswer) {

        // KHÔNG CHO CHỌN NHIỀU LẦN

        if (answerSelected) {

            return;
        }


        answerSelected = true;


        // LẤY NỘI DUNG ĐÁP ÁN

        String selectedText =
                selectedAnswer
                        .getText()
                        .toString();


        String selectedAnswerText =
                selectedText.substring(
                        selectedText.indexOf(".") + 1
                ).trim();


        // KIỂM TRA
        boolean isCorrect =
                selectedAnswerText.equals(
                        correctAnswer
                );


        // XỬ LÝ KẾT QUẢ
        if (isCorrect) {

            // ĐÚNG

            correctCount++;


            // Đổi màu xanh

            setCorrectStyle(
                    selectedAnswer
            );

            SpeakWord s = new SpeakWord();
            s.speakWord(textToSpeech, wordList, currentPosition);

        } else {

            // SAI

            wrongCount++;


            // Đổi màu đỏ

            setWrongStyle(
                    selectedAnswer
            );


            // Hiển thị đáp án đúng

            showCorrectAnswer();
            SpeakWord s = new SpeakWord();
            s.speakWord(textToSpeech, wordList, currentPosition);
        }


        // LƯU LỊCH SỬ
        saveLearningHistory(
                isCorrect ? 1 : 0
        );

        // CHỜ 0.8 GIÂY RỒI SANG CÂU TIẾP

        new Handler().postDelayed(
                () -> {

                    currentPosition++;

                    if (currentPosition <
                            wordList.size()) {

                        showCurrentQuestion();

                    }

                    // HẾT CÂU
                    else {

                        showResult();
                    }

                },
                800
        );
    }


    // HIỂN THỊ ĐÁP ÁN ĐÚNG

    private void showCorrectAnswer() {

        if (tvAnswer1.getText()
                .toString()
                .contains(correctAnswer)) {

            setCorrectStyle(
                    tvAnswer1
            );

        } else if (tvAnswer2.getText()
                .toString()
                .contains(correctAnswer)) {

            setCorrectStyle(
                    tvAnswer2
            );

        } else if (tvAnswer3.getText()
                .toString()
                .contains(correctAnswer)) {

            setCorrectStyle(
                    tvAnswer3
            );

        } else if (tvAnswer4.getText()
                .toString()
                .contains(correctAnswer)) {

            setCorrectStyle(
                    tvAnswer4
            );
        }
    }


    // STYLE ĐÁP ÁN ĐÚNG
    private void setCorrectStyle(
            TextView textView) {

        GradientDrawable drawable =
                new GradientDrawable();


        drawable.setColor(
                Color.parseColor(
                        "#E8F5E9"
                )
        );


        drawable.setStroke(
                2,
                Color.parseColor(
                        "#4CAF50"
                )
        );


        drawable.setCornerRadius(
                10
        );


        textView.setBackground(
                drawable
        );


        textView.setTextColor(
                Color.parseColor(
                        "#2E7D32"
                )
        );
    }


    // STYLE ĐÁP ÁN SAI
    private void setWrongStyle(
            TextView textView) {

        GradientDrawable drawable =
                new GradientDrawable();


        drawable.setColor(
                Color.parseColor(
                        "#FFEBEE"
                )
        );


        drawable.setStroke(
                2,
                Color.parseColor(
                        "#E53935"
                )
        );


        drawable.setCornerRadius(
                10
        );


        textView.setBackground(
                drawable
        );


        textView.setTextColor(
                Color.parseColor(
                        "#C62828"
                )
        );
    }


    // RESET STYLE
    private void resetAnswerStyle(
            TextView textView) {

        GradientDrawable drawable =
                new GradientDrawable();


        drawable.setColor(
                Color.WHITE
        );


        drawable.setStroke(
                1,
                Color.parseColor(
                        "#EEEEEE"
                )
        );


        drawable.setCornerRadius(
                10
        );


        textView.setBackground(
                drawable
        );


        textView.setTextColor(
                Color.parseColor(
                        "#333333"
                )
        );
    }


    private void saveLearningHistory(
            int isCorrect) {

        if (currentPosition >=
                wordList.size()) {

            return;
        }


        // LẤY TỪ HIỆN TẠI
        Word word =
                wordList.get(
                        currentPosition
                );


        // THỜI GIAN HIỆN TẠI
        String currentTime =
                new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                ).format(
                        new Date()
                );

        // TẠO HISTORY
        LearningHistory history =
                new LearningHistory();


        history.setSetId(
                setId
        );


        history.setWordId(
                word.getId()
        );


        history.setIsCorrect(
                isCorrect
        );


        history.setLearningMode(
                "QUIZ"
        );


        history.setLearnedAt(
                currentTime
        );

        // LƯU DATABASE
        long id =
                learningHistoryDAO.insert(
                        history
                );


        // KIỂM TRA LƯU
        if (id == -1) {

            Toast.makeText(
                    this,
                    "Lưu lịch sử thất bại",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // CHUYỂN SANG MÀN HÌNH KẾT QUẢ
    private void showResult() {

        Intent intent =
                new Intent(
                        QuizActivity.this,
                        QuizResultActivity.class
                );


        // SỐ CÂU ĐÚNG
        intent.putExtra(
                "correct",
                correctCount
        );


        // SỐ CÂU SAI
        intent.putExtra(
                "wrong",
                wrongCount
        );


        // TỔNG SỐ CÂU
        intent.putExtra(
                "total",
                wordList.size()
        );


        // ID BỘ TỪ
        intent.putExtra(
                "set_id",
                setId
        );

        // MỞ MÀN HÌNH KẾT QUẢ
        startActivity(
                intent
        );


        // Đóng QuizActivity

        finish();
    }
    // GIẢI PHÓNG TTS
    @Override
    protected void onDestroy() {

        if (textToSpeech != null) {

            textToSpeech.stop();

            textToSpeech.shutdown();
        }

        super.onDestroy();
    }
}