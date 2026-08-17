package com.example.englishvocabulary.activities;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.englishvocabulary.R;
import com.example.englishvocabulary.database.LearningHistoryDAO;
import com.example.englishvocabulary.database.WordDAO;
import com.example.englishvocabulary.models.LearningHistory;
import com.example.englishvocabulary.models.Word;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FlashcardActivity extends AppCompatActivity {

    // =====================================================
    // VIEW
    // =====================================================

    private ImageButton btnBack;
    private ImageButton btnSound;
    private ImageButton btnSetting;

    private TextView tvProgress;

    private TextView tvEnglish;

    private TextView tvBackEnglish;
    private TextView tvMeaning;
    private TextView tvExample;

    private TextView tvChuaNho;
    private TextView tvDaNho;

    private LinearLayout layoutBackCard;

    private CardView cardFlashcard;

    private ProgressBar progressBar;


    // =====================================================
    // DATABASE
    // =====================================================

    private WordDAO wordDAO;
    private LearningHistoryDAO learningHistoryDAO;


    // =====================================================
    // DANH SÁCH TỪ
    // =====================================================

    private List<Word> wordList;


    // =====================================================
    // VỊ TRÍ HIỆN TẠI
    // =====================================================

    private int currentPosition = 0;


    // =====================================================
    // ID BỘ TỪ
    // =====================================================

    private int setId;


    // =====================================================
    // TRẠNG THÁI MẶT THẺ
    // false = mặt trước
    // true = mặt sau
    // =====================================================

    private boolean isBackSide = false;


    // =====================================================
    // TEXT TO SPEECH
    // =====================================================

    private TextToSpeech textToSpeech;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_flashcard);


        // =====================================================
        // NHẬN ID BỘ TỪ
        // =====================================================

        setId = getIntent().getIntExtra(
                "set_id",
                -1
        );


        // =====================================================
        // ÁNH XẠ VIEW
        // =====================================================

        btnBack = findViewById(R.id.btnBack);

        btnSound = findViewById(R.id.btnSound);

        btnSetting = findViewById(R.id.btnSetting);

        tvProgress = findViewById(R.id.tvProgress);

        progressBar = findViewById(R.id.progressBar);

        cardFlashcard = findViewById(R.id.cardFlashcard);

        tvEnglish = findViewById(R.id.tvEnglish);

        layoutBackCard = findViewById(R.id.layoutBackCard);

        tvBackEnglish = findViewById(R.id.tvBackEnglish);

        tvMeaning = findViewById(R.id.tvMeaning);

        tvExample = findViewById(R.id.tvExample);

        tvChuaNho = findViewById(R.id.tvChuaNho);

        tvDaNho = findViewById(R.id.tvDaNho);


        // =====================================================
        // DATABASE
        // =====================================================

        wordDAO = new WordDAO(this);

        learningHistoryDAO =
                new LearningHistoryDAO(this);


        // =====================================================
        // KIỂM TRA SET ID
        // =====================================================

        if (setId == -1) {

            Toast.makeText(
                    this,
                    "Không tìm thấy bộ từ",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        // =====================================================
        // LẤY DANH SÁCH TỪ
        // =====================================================

        wordList =
                wordDAO.getBySetId(setId);


        // =====================================================
        // KIỂM TRA DANH SÁCH
        // =====================================================

        if (wordList == null ||
                wordList.isEmpty()) {

            Toast.makeText(
                    this,
                    "Bộ từ chưa có từ vựng",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        // =====================================================
        // CÀI ĐẶT PROGRESS BAR
        // =====================================================

        progressBar.setMax(100);


        // =====================================================
        // HIỂN THỊ TỪ ĐẦU TIÊN
        // =====================================================

        showCurrentWord();


        // =====================================================
        // QUAY LẠI
        // =====================================================

        btnBack.setOnClickListener(v -> {

            finish();

        });


        // =====================================================
        // LẬT THẺ
        // =====================================================

        cardFlashcard.setOnClickListener(v -> {

            flipCard();

        });


        // =====================================================
        // TEXT TO SPEECH
        // =====================================================

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


        // =====================================================
        // PHÁT ÂM
        // =====================================================

        btnSound.setOnClickListener(v -> {

            speakWord();

        });


        // =====================================================
        // CHƯA NHỚ
        // =====================================================

        tvChuaNho.setOnClickListener(v -> {

            saveLearningHistory(0);

        });


        // =====================================================
        // ĐÃ NHỚ
        // =====================================================

        tvDaNho.setOnClickListener(v -> {

            saveLearningHistory(1);

        });


        // =====================================================
        // CÀI ĐẶT
        // =====================================================

        btnSetting.setOnClickListener(v -> {

            Toast.makeText(
                    this,
                    "Chức năng cài đặt sẽ làm sau",
                    Toast.LENGTH_SHORT
            ).show();

        });
    }


    // =====================================================
    // HIỂN THỊ TỪ HIỆN TẠI
    // =====================================================

    private void showCurrentWord() {

        if (currentPosition >= wordList.size()) {

            finish();

            return;
        }


        Word word =
                wordList.get(currentPosition);


        // =================================================
        // MẶT TRƯỚC
        // =================================================

        tvEnglish.setText(
                word.getEnglish()
        );


        // =================================================
        // MẶT SAU
        // =================================================

        tvBackEnglish.setText(
                word.getEnglish()
        );


        tvMeaning.setText(
                word.getMeaning()
        );


        if (word.getExample() != null &&
                !word.getExample().isEmpty()) {

            tvExample.setText(
                    word.getExample()
            );

        } else {

            tvExample.setText(
                    "Chưa có ví dụ"
            );
        }


        // =================================================
        // RESET VỀ MẶT TRƯỚC
        // =================================================

        showFrontSide();


        // =================================================
        // HIỂN THỊ 1/5, 2/5...
        // =================================================

        tvProgress.setText(
                (currentPosition + 1)
                        + "/"
                        + wordList.size()
        );


        // =================================================
        // CẬP NHẬT PROGRESS BAR
        // =================================================

        int progress =
                (int) (
                        ((currentPosition + 1)
                                * 100.0)
                                / wordList.size()
                );

        progressBar.setProgress(progress);
    }


    // =====================================================
    // LẬT THẺ
    // =====================================================

    private void flipCard() {

        cardFlashcard.animate()
                .scaleX(0f)
                .setDuration(150)
                .withEndAction(() -> {

                    if (isBackSide) {

                        showFrontSide();

                    } else {

                        showBackSide();
                    }

                    cardFlashcard.animate()
                            .scaleX(1f)
                            .setDuration(150)
                            .start();

                })
                .start();
    }


    // =====================================================
    // HIỆN MẶT TRƯỚC
    // =====================================================

    private void showFrontSide() {

        tvEnglish.setVisibility(
                View.VISIBLE
        );

        layoutBackCard.setVisibility(
                View.GONE
        );

        isBackSide = false;
    }


    // =====================================================
    // HIỆN MẶT SAU
    // =====================================================

    private void showBackSide() {

        tvEnglish.setVisibility(
                View.GONE
        );

        layoutBackCard.setVisibility(
                View.VISIBLE
        );

        isBackSide = true;
    }


    // =====================================================
    // PHÁT ÂM
    // =====================================================

    private void speakWord() {

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


    // =====================================================
    // LƯU LỊCH SỬ HỌC
    // =====================================================

    private void saveLearningHistory(
            int isCorrect) {

        if (currentPosition >=
                wordList.size()) {

            return;
        }


        Word word =
                wordList.get(currentPosition);


        // =================================================
        // THỜI GIAN HIỆN TẠI
        // =================================================

        String currentTime =
                new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                ).format(
                        new Date()
                );


        // =================================================
        // TẠO HISTORY
        // =================================================

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
                "FLASHCARD"
        );


        history.setLearnedAt(
                currentTime
        );


        // =================================================
        // LƯU DATABASE
        // =================================================

        long id =
                learningHistoryDAO.insert(
                        history
                );


        if (id == -1) {

            Toast.makeText(
                    this,
                    "Lưu lịch sử thất bại",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // =================================================
        // CHUYỂN SANG TỪ TIẾP THEO
        // =================================================

        currentPosition++;


        if (currentPosition <
                wordList.size()) {

            showCurrentWord();

        } else {

            Toast.makeText(
                    this,
                    "Đã hoàn thành Flashcard!",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        }
    }


    // =====================================================
    // GIẢI PHÓNG TTS
    // =====================================================

    @Override
    protected void onDestroy() {

        if (textToSpeech != null) {

            textToSpeech.stop();

            textToSpeech.shutdown();
        }

        super.onDestroy();
    }
}